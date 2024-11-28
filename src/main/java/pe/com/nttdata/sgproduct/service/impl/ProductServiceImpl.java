package pe.com.nttdata.sgproduct.service.impl;

import com.nttdata.sgproduct.model.CustomerResponse;
import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pe.com.nttdata.sgproduct.exception.CustomerException;
import pe.com.nttdata.sgproduct.repository.ProductRepository;
import pe.com.nttdata.sgproduct.service.ProductService;
import pe.com.nttdata.sgproduct.service.mapper.ProductMapper;
import pe.com.nttdata.sgproduct.webclient.ApiWebClientCustomer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    public static final String STRING_ONE = "1";

    private final ProductRepository productRepository;
    private final ApiWebClientCustomer webClientCustomer;
    private final ProductMapper productMapper;


    public ProductServiceImpl(ProductRepository productRepository, ApiWebClientCustomer webClientCustomer, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.webClientCustomer = webClientCustomer;
        this.productMapper = productMapper;
    }

    @Override
    public Mono<Void> createAccounts(ProductRequest productRequest) {

        return this.findByClientId(productRequest.getClientId())
                .flatMap(customerResponse -> {
                    String productType = productRequest.getProductType().getValue();

                    switch (productType) {
                        case "AHORRO":
                            productRequest.setLimitMnthlyMovements(1);
                            return handleSaving(productRequest, customerResponse);

                        case "CUENTA_CORRIENTE":
                            return handleCurrentAccount(productRequest, customerResponse);

                        case "PLAZO_FIJO":
                            return handleFixedTerm(productRequest, customerResponse);

                        case "CREDITO_PERSONAL":
                            return handlePersonalCredit(productRequest, customerResponse);

                        case "CREDITO_EMPRESARIAL":
                            return handleBusinessCredit(productRequest, customerResponse);

                        case "TARJETA_CREDITO":
                            return handleCreditCard(productRequest, customerResponse);
                        default:
                            return Mono.error(new CustomerException("Tipo de producto no reconocido", "400", HttpStatus.BAD_REQUEST));
                    }
                })
                .then();

    }


    @Override
    public Flux<ProductResponse> getAllProduct() {
        return this.productRepository.findAll()
                .switchIfEmpty(Mono.error(new CustomerException("Producto vacio", "200", HttpStatus.OK)))
                .flatMap(productResponse -> this.findByClientId(productResponse.getClientId())
                        .map(customerResponse -> this.productMapper.toProductResponse(productResponse, customerResponse))
                );

    }

    @Override
    public Mono<ProductResponse> findById(String id) {
        return this.productRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerException("Producto no encontrado con el id ".concat(id),"400", HttpStatus.BAD_REQUEST)))
                .flatMap(product -> this.findByClientId(product.getClientId())
                        .map(customerResponse -> this.productMapper.toProductResponse(product, customerResponse)));

    }


    @Override
    public Mono<Void> update(String id, ProductRequest productRequest) {
        return this.productRepository.findById(id)
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
                .flatMap(productResponse -> this.findByClientId(productResponse.getClientId())
                        .map(customerResponse -> this.productMapper.toProductResponse(productResponse, customerResponse))
                );
    }

    private Mono<CustomerResponse> findByClientId(String id) {
        return this.webClientCustomer.findByClientId(id);
    }


    private Mono<Void> handlePersonalCredit(ProductRequest productRequest, CustomerResponse customerResponse) {

        if (STRING_ONE.equals(customerResponse.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener crédito personal", "400", HttpStatus.BAD_REQUEST));
        }
        return this.productRepository.findByClientIdAndProductType(customerResponse.getId(), "CREDITO_PERSONAL")
                .flatMap(existingProduct -> Mono.error(new CustomerException("El cliente ya tiene un crédito personal", "400", HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(this.productRepository.save(productMapper.toProduct(productRequest, customerResponse)).then()).then();
    }

    private Mono<Void> handleBusinessCredit(ProductRequest productRequest, CustomerResponse customerResponse) {
        if (!STRING_ONE.equals(customerResponse.getClientType())) {
            return Mono.error(new CustomerException("Solo los clientes empresariales pueden tener crédito empresarial", "400", HttpStatus.BAD_REQUEST));
        }
        return this.productRepository.save(this.productMapper.toProduct(productRequest, customerResponse)).then();
    }

    private Mono<Void> handleCreditCard(ProductRequest productRequest, CustomerResponse customerResponse) {
        // Las tarjetas de crédito son permitidas para cualquier cliente
        return this.productRepository.save(this.productMapper.toProduct(productRequest, customerResponse)).then();
    }

    private Mono<Void> handleFixedTerm(ProductRequest productRequest, CustomerResponse customerResponse) {
        if (STRING_ONE.equals(customerResponse.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta a plazo fijo", "400", HttpStatus.BAD_REQUEST));
        }
        return validateSingleAccount(productRequest, customerResponse);
    }

    private Mono<Void> handleCurrentAccount(ProductRequest productRequest, CustomerResponse customerResponse) {
        // Cualquier cliente puede tener cuentas corrientes
        return this.productRepository.save(this.productMapper.toProduct(productRequest, customerResponse)).then();
    }

    private Mono<Void> handleSaving(ProductRequest productRequest, CustomerResponse customerResponse) {
        // BUSINESS =1 No puede tener una cuenta de ahorro o de plazo fijo pero sí múltiples cuentas corrientes.
        // PERSONAL =2 Solo puede tener un máximo de una cuenta de ahorro, una cuenta corriente o cuentas a plazo fijo.
        log.info("Data client type: {}", customerResponse.getClientType());
        if (STRING_ONE.equals(customerResponse.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta de ahorro",
                    "400",
                    HttpStatus.BAD_REQUEST));
        }
        return validateSingleAccount(productRequest, customerResponse);
    }

    private Mono<Void> validateSingleAccount(ProductRequest productRequest, CustomerResponse customerResponse) {
        return this.productRepository.findByClientId(customerResponse.getId())
                .collectList()
                .flatMap(existingProducts -> {
                    boolean hasConflictingAccount = existingProducts.stream()
                            .anyMatch(product -> "AHORRO".equals(product.getProductType()) ||
                                    "PLAZO_FIJO".equals(product.getProductType()));
                    if (hasConflictingAccount) {
                        return Mono.error(new CustomerException("El cliente ya tiene una cuenta de ahorro o a plazo fijo", "400", HttpStatus.BAD_REQUEST));
                    }
                    return this.productRepository.save(this.productMapper.toProduct(productRequest, customerResponse)).then();
                });
    }
}
