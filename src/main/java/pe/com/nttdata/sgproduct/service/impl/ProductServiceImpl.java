package pe.com.nttdata.sgproduct.service.impl;

import com.nttdata.sgproduct.model.CustomerResponse;
import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.nttdata.sgproduct.model.entity.Product;
import pe.com.nttdata.sgproduct.repository.ProductRepository;
import pe.com.nttdata.sgproduct.service.ProductService;
import pe.com.nttdata.sgproduct.webclient.ApiWebClientCustomer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ApiWebClientCustomer webClientCustomer;


    public ProductServiceImpl(ProductRepository productRepository, ApiWebClientCustomer webClientCustomer) {
        this.productRepository = productRepository;
        this.webClientCustomer = webClientCustomer;
    }

    @Override
    public Mono<Void> insert(ProductRequest productRequest) {

        return this.findByClientId(productRequest.getClientId())
                .flatMap(customerResponse -> this.productRepository.save(toProduct(productRequest, customerResponse)))
                .then();

    }



    @Override
    public Flux<ProductResponse> getAllProduct() {
        return this.productRepository.findAll()
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontraron productos")))
                .flatMap(productResponse -> this.findByClientId(productResponse.getClientId())
                                .map(customerResponse -> this.toProductResponse(productResponse, customerResponse))
                );

    }

    @Override
    public Mono<ProductResponse> findById(String id) {
        return this.productRepository.findById(id)
                .flatMap(product -> this.findByClientId(product.getClientId())
                        .map(customerResponse->this.toProductResponse(product,customerResponse)));

    }



    @Override
    public Mono<Void> update(String id, ProductRequest productRequest) {
        return this.productRepository.findById(id)
                .flatMap(product -> this.productRepository.save(this.toProductUpdate(productRequest, product)))
                .then();
    }



    @Override
    public Mono<Void> delete(String id) {
        return this.findById(id)
                .flatMap(productResponse -> this.productRepository.deleteById(productResponse.getId()));
    }

    private Mono<CustomerResponse> findByClientId(String id){
        return this.webClientCustomer.findByClientId(id);
    }



    private ProductResponse toProductResponse(Product product, CustomerResponse customerRespons) {
        ProductResponse productResponse=new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setProductType(product.getProductType());
        productResponse.setBalance(product.getBalance());
        productResponse.setLimitMnthlyMovements(product.getLimitMnthlyMovements());
        productResponse.setDayMovement(product.getDayMovement());
        productResponse.setCustomer(customerRespons);

        return productResponse;
    }

    private Product toProduct(ProductRequest productRequest, CustomerResponse customerResponse) {
        return Product.builder()
                .productType(productRequest.getProductType().getValue())
                .balance(productRequest.getBalance())
                .limitMnthlyMovements(productRequest.getLimitMnthlyMovements())
                .dayMovement(productRequest.getDayMovement())
                .clientId(customerResponse.getId())
                .build();
    }

    private Product toProductUpdate(ProductRequest productRequest, Product product) {
        return Product.builder()
                .id(product.getId())
                .productType(productRequest.getProductType().getValue())
                .balance(productRequest.getBalance())
                .limitMnthlyMovements(productRequest.getLimitMnthlyMovements())
                .dayMovement(productRequest.getDayMovement())
                .clientId(product.getClientId())
                .build();
    }
}
