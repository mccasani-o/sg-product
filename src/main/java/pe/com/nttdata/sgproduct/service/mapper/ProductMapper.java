package pe.com.nttdata.sgproduct.service.mapper;

import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import org.springframework.stereotype.Component;
import pe.com.nttdata.sgproduct.controller.DateUtil;
import pe.com.nttdata.sgproduct.model.entity.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product product) {
        ProductResponse productResponse=new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setProductType(product.getProductType());
        productResponse.setBalance(product.getBalance());
        productResponse.setLimitMnthlyMovements(product.getLimitMnthlyMovements());
        productResponse.setDayMovement(product.getDayMovement());
        productResponse.setCustomerId(product.getClientId());

        return productResponse;
    }

    public Product toProduct(ProductRequest productRequest) {
        return Product.builder()
                .productType(productRequest.getProductType().getValue())
                .balance(productRequest.getBalance())
                .limitMnthlyMovements(productRequest.getLimitMnthlyMovements())
                .dayMovement(productRequest.getDayMovement())
                .clientId(productRequest.getClientId())
                .build();
    }

    public Product toProductUpdate(ProductRequest productRequest, Product product) {
        return Product.builder()
                .id(product.getId())
                .productType(productRequest.getProductType().getValue())
                .balance(productRequest.getBalance())
                .limitMnthlyMovements(productRequest.getLimitMnthlyMovements())
                .dayMovement(DateUtil.localDateTimeToString())
                .clientId(product.getClientId())
                .build();
    }

}
