package pe.com.nttdata.sgproduct.util;

import com.nttdata.sgproduct.model.ProductResponse;

public final class UtilMock {

    private UtilMock(){}

    public static ProductResponse buildProductResponse() {
        ProductResponse productResponse=new ProductResponse();
        productResponse.setId("35847efi4");
        productResponse.setProductType("AHORRO");
        productResponse.setBalance(440.9);
        productResponse.setDayMovement("1");
        productResponse.setLimitMnthlyMovements(1);
        productResponse.setCustomerId("1284635");
        return productResponse;
    }



}
