package pe.com.nttdata.sgproduct.controller;


import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.com.nttdata.sgproduct.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    Mono<Void> insert(@RequestBody ProductRequest productRequest) {
        return this.productService.insert(productRequest);
    }

    @GetMapping
    Flux<ProductResponse> getAll() {
        return this.productService.getAllProduct();
    }

    @GetMapping("/{id}")
    public Mono<ProductResponse> getById(@PathVariable String id) {
        return this.productService.findById(id);


    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> update(@PathVariable String id, @RequestBody ProductRequest productRequest) {
        return this.productService.update(id, productRequest);


    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return this.productService.delete(id);
    }


}
