package pe.com.nttdata.sgproduct.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import pe.com.nttdata.sgproduct.model.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {


    Flux<Product> findByClientId(String clientId);

    Mono<Optional<Product>> findByProductType(String productType);

    Mono<Product> findByProductTypeAndClientId(String productType, String clientId);
}
