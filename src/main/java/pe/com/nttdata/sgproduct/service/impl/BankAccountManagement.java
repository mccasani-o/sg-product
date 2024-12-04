package pe.com.nttdata.sgproduct.service.impl;

import com.nttdata.sgproduct.model.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pe.com.nttdata.sgproduct.controller.DateUtil;
import pe.com.nttdata.sgproduct.exception.CustomerException;
import pe.com.nttdata.sgproduct.repository.ProductRepository;
import pe.com.nttdata.sgproduct.service.mapper.ProductMapper;
import pe.com.nttdata.sgproduct.util.ConstantProduct;
import pe.com.nttdata.sgproduct.webclient.dto.CustomerDto;
import reactor.core.publisher.Mono;

// Gestión de Cuentas Bancarias
@RequiredArgsConstructor
@Component
public class BankAccountManagement {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Plazo fijo
    public Mono<Void> handleFixedTerm(ProductRequest productRequest, CustomerDto customerDto) {

        if (ConstantProduct.BUSINESS_CLIENT.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta a plazo fijo", "400", HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitMnthlyMovements(0);
        productRequest.setDayMovement(DateUtil.localDateTimeToString());
        return validateSingleAccount(productRequest);
    }

    // Current account
    public Mono<Void> handleCurrentAccount(ProductRequest productRequest, CustomerDto customerDto) {
        if (ConstantProduct.BUSINESS_CLIENT.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener una cuenta de ahorro",
                    "400",
                    HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitMnthlyMovements(0);
        return validateSingleAccount(productRequest);

    }

    // Current ahorro
    public Mono<Void> handleSaving(ProductRequest productRequest, CustomerDto customerDto) {

        if (ConstantProduct.BUSINESS_CLIENT.equals(customerDto.getClientType())) {
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
