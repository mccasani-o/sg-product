package pe.com.nttdata.sgproduct.service.impl;

import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pe.com.nttdata.sgproduct.controller.DateUtil;
import pe.com.nttdata.sgproduct.exception.CustomerException;
import pe.com.nttdata.sgproduct.model.entity.Product;
import pe.com.nttdata.sgproduct.repository.ProductRepository;
import pe.com.nttdata.sgproduct.service.ProductService;
import pe.com.nttdata.sgproduct.service.mapper.ProductMapper;
import pe.com.nttdata.sgproduct.webclient.ApiWebClientCustomer;
import pe.com.nttdata.sgproduct.webclient.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    public static final Integer NUMBER_ONE = 1;

    private final ProductRepository productRepository;
    private final ApiWebClientCustomer webClientCustomer;
    private final ProductMapper productMapper;


    public ProductServiceImpl(ProductRepository productRepository, ApiWebClientCustomer webClientCustomer, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.webClientCustomer = webClientCustomer;
        this.productMapper = productMapper;
    }

    @Override
    public Mono<Void> createAccount(ProductRequest productRequest) {

        return this.findByCustomerId(productRequest.getClientId())
                .flatMap(customerDto -> {
                    // BUSINESS =1 PERSONAL =2
                    switch (productRequest.getProductType().getValue()) {
                        case "AHORRO":
                            return handleSaving(productRequest, customerDto);

                        case "CUENTA_CORRIENTE":
                            return handleCurrentAccount(productRequest, customerDto);

                        case "PLAZO_FIJO":
                            return handleFixedTerm(productRequest, customerDto);
                        default:
                            return Mono.error(new CustomerException("Tipo de producto no reconocido", "400", HttpStatus.BAD_REQUEST));
                    }
                })
                .then();

    }

    @Override
    public Mono<Void> creditAccount(ProductRequest productRequest) {
        return null;
    }


    @Override
    public Flux<ProductResponse> getAllProduct() {
        return this.productRepository.findAll()
                .doOnNext(product -> log.info("response products: {}", product))
                .map(this.productMapper::toProductResponse);

    }

    @Override
    public Mono<ProductResponse> findById(String id) {
        return this.productRepository.findById(id)
                .doOnNext(product -> log.info("Product entity {}", product))
                .switchIfEmpty(Mono.error(new CustomerException("Producto no encontrado con el id ".concat(id), "400", HttpStatus.BAD_REQUEST)))
                .map(this.productMapper::toProductResponse);

    }


    @Override
    public Mono<Void> update(String id, ProductRequest productRequest) {
        return this.productRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerException("Producto no encontrado con el id ".concat(id), "400", HttpStatus.BAD_REQUEST)))
                .flatMap(product -> this.productRepository.save(this.productMapper.toProductUpdate(productRequest, product)))
                .then();
    }


    @Override
    public Mono<Void> delete(String id) {
        return this.findById(id)
                .flatMap(productResponse -> this.productRepository.deleteById(productResponse.getId()));
    }

    @Override
    public Flux<ProductResponse> searchProductsByCustomerId(String customId) {
        return this.productRepository.findByClientId(customId)
                .switchIfEmpty(Mono.error(new CustomerException("No se encontró cliente por id ".concat(customId), "204", HttpStatus.NO_CONTENT)))
                .flatMap(productResponse -> this.findByCustomerId(productResponse.getClientId())
                        .map(customerResponse -> this.productMapper.toProductResponse(productResponse))
                );
    }

    private Mono<CustomerDto> findByCustomerId(String id) {
        return this.webClientCustomer.findByClientId(id);
    }


    private Mono<Void> handleFixedTerm(ProductRequest productRequest, CustomerDto customerDto) {

        if (NUMBER_ONE.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta a plazo fijo", "400", HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitMnthlyMovements(0);
        productRequest.setDayMovement(DateUtil.localDateTimeToString());
        return validateSingleAccount(productRequest);
    }

    private Mono<Void> handleCurrentAccount(ProductRequest productRequest, CustomerDto customerDto) {
        // Cualquier cliente puede tener cuentas corrientes
        // return this.productRepository.save(this.productMapper.toProduct(productRequest)).then();
        if (NUMBER_ONE.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta de ahorro",
                    "400",
                    HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitMnthlyMovements(100);
        return validateSingleAccount(productRequest);

    }

    private Mono<Void> handleSaving(ProductRequest productRequest, CustomerDto customerDto) {

        if (NUMBER_ONE.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta de ahorro",
                    "400",
                    HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitMnthlyMovements(100);
        return validateSingleAccount(productRequest);
    }

    private Mono<Void> validateSingleAccount(ProductRequest productRequest) {
        return this.productRepository.findByClientId(productRequest.getClientId())
                .flatMap(existingProduct ->
                        this.productRepository.findByProductTypeAndClientId(existingProduct.getProductType(), existingProduct.getClientId())
                                .flatMap(product -> {
                                    // Lanza una excepción si el tipo de producto ya existe
                                    if (product.getProductType().equals(productRequest.getProductType().getValue())) {
                                        return Mono.error(new CustomerException(
                                                "El cliente ya tiene un producto de este tipo.",
                                                "400",
                                                HttpStatus.BAD_REQUEST
                                        ));
                                    }
                                    return Mono.empty();
                                })
                )
                .switchIfEmpty(this.productRepository.save(this.productMapper.toProduct(productRequest)))
                .then();
    }

}
