package pe.com.nttdata.sgproduct.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import pe.com.nttdata.sgproduct.model.entity.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
