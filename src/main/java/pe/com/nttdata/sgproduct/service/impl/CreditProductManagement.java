package pe.com.nttdata.sgproduct.service.impl;

import com.nttdata.sgproduct.model.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pe.com.nttdata.sgproduct.exception.CustomerException;
import pe.com.nttdata.sgproduct.repository.ProductRepository;
import pe.com.nttdata.sgproduct.service.mapper.ProductMapper;
import pe.com.nttdata.sgproduct.util.ConstantProduct;
import pe.com.nttdata.sgproduct.webclient.dto.CustomerDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CreditProductManagement {
    public static final Integer NUMBER_ONE = 1;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    //// BUSINESS =1 PERSONAL =2
    public Mono<Void> handlePersonalCredit(ProductRequest productRequest, CustomerDto customerResponse) {
        //Credito 	Personal: solo se permite un solo crédito por persona.
        if (ConstantProduct.BUSINESS_CLIENT.equals(customerResponse.getClientType())) {
            return Mono.error(new CustomerException("Los clientes empresariales no pueden tener crédito personal", "400", HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitCredit(BigDecimal.valueOf(5000.00));
        return this.productRepository.findByProductTypeAndClientId(productRequest.getProductType().getValue(), productRequest.getClientId())
                 .flatMap(existingProduct -> Mono.error(new CustomerException("Ya tienes una cuenta de credito personal","400",HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(this.productRepository.save(productMapper.toProductCredit(productRequest))).then();

    }

    public Mono<Void> handleBusinessCredit(ProductRequest productRequest, CustomerDto customerDto) {
        if (ConstantProduct.PERSONAL_CLIENT.equals(customerDto.getClientType())) {
            return Mono.error(new CustomerException("Solo los clientes empresariales pueden tener crédito empresarial", "400", HttpStatus.BAD_REQUEST));
        }
        productRequest.setLimitCredit(BigDecimal.valueOf(10000.00));
        return this.productRepository.save(this.productMapper.toProductCredit(productRequest)).then();
    }


}
