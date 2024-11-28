package pe.com.nttdata.sgproduct.util;

import com.nttdata.sgproduct.model.CustomerResponse;
import com.nttdata.sgproduct.model.ProductResponse;

public final class UtilMock {

    private UtilMock(){}

    public static ProductResponse buildProductResponse() {
        ProductResponse productResponse=new ProductResponse();
        productResponse.setId("35847efi4");
        productResponse.setProductType("AHORRO");
        productResponse.setBalance(440.9);
        productResponse.setDayMovement(1);
        productResponse.setLimitMnthlyMovements(1);
        productResponse.setCustomer(toCustomerResponse());
        return productResponse;
    }

    private static CustomerResponse toCustomerResponse(){
        CustomerResponse customerResponse=new CustomerResponse();
        customerResponse.setId("437f8666");
        customerResponse.setClientType("1");
        customerResponse.setDocumentType("1");
        customerResponse.setDocumentNumber("12345678");
        customerResponse.setName("Juan");
        customerResponse.setLastName("Cardenas");
        customerResponse.setEmail("juan@emeal.com");
        return customerResponse;
    }

}
