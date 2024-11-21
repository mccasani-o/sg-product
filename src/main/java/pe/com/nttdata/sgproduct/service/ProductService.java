package pe.com.nttdata.sgproduct.service;

import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<Void> insert(ProductRequest productRequest);

    Flux<ProductResponse>getAllProduct();

    Mono<ProductResponse> findById(String id);

    Mono<Void> update(String id, ProductRequest productRequest);

    Mono<Void> delete(String id);

}
