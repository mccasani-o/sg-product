package pe.com.nttdata.sgproduct.service.mapper;

import com.nttdata.sgproduct.model.CustomerResponse;
import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import org.springframework.stereotype.Component;
import pe.com.nttdata.sgproduct.model.entity.Product;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product product, CustomerResponse customerRespons) {
        ProductResponse productResponse=new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setProductType(product.getProductType());
        productResponse.setBalance(product.getBalance());
        productResponse.setLimitMnthlyMovements(product.getLimitMnthlyMovements());
        productResponse.setDayMovement(product.getDayMovement());
        productResponse.setCustomer(customerRespons);

        return productResponse;
    }

    public Product toProduct(ProductRequest productRequest, CustomerResponse customerResponse) {
        return Product.builder()
                .productType(productRequest.getProductType().getValue())
                .balance(productRequest.getBalance())
                .limitMnthlyMovements(productRequest.getLimitMnthlyMovements())
                .dayMovement(productRequest.getDayMovement())
                .clientId(customerResponse.getId())
                .build();
    }

    public Product toProductUpdate(ProductRequest productRequest, Product product) {
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
