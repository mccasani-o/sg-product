package pe.com.nttdata.sgproduct.controller;

import com.nttdata.sgproduct.model.ProductRequest;
import com.nttdata.sgproduct.model.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.nttdata.sgproduct.service.ProductService;
import pe.com.nttdata.sgproduct.util.UtilMock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    public static final String URI = "/api/v1/products";
    @Mock
    private ProductService  productService;

    private WebTestClient client;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToController(new ProductController(productService)).build();
        productRequest=new ProductRequest();
        productRequest.setProductType(ProductRequest.ProductTypeEnum.AHORRO);
        productRequest.setBalance(100.0);
        productRequest.setDayMovement("27");
        productRequest.setLimitMnthlyMovements(1);
        productRequest.setClientId("333g445");
    }

    @Test
    void createAccount() {
        when(this.productService.createAccount(any(ProductRequest.class)))
                .thenReturn(Mono.empty());

        this.client.post().uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(this.productRequest), ProductRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void getAll() {
        when(this.productService.getAllProduct())
                .thenReturn(Flux.just(UtilMock.buildProductResponse()));

        client.get().uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponse.class)
                .consumeWith(response -> {
                    List<ProductResponse> products = response.getResponseBody();
                    Assertions.assertNotNull(products);
                    assertFalse(products.isEmpty());
                });
    }

    @Test
    void searchProductsByCustomerId() {
        when(this.productService.searchProductsByCustomerId(anyString()))
                .thenReturn(Flux.just(UtilMock.buildProductResponse()));

        client.get().uri(URI.concat("/customer/437f8666"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponse.class)
                .consumeWith(response -> {
                    List<ProductResponse> products = response.getResponseBody();
                    Assertions.assertNotNull(products);
                    assertFalse(products.isEmpty());
                });
    }

    @Test
    void getById() {
        given(this.productService.findById(anyString()))
                .willReturn(Mono.just(UtilMock.buildProductResponse()));

        client.get().uri(URI.concat("/35847efi4"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponse.class)
                .consumeWith(response -> {
                    ProductResponse movementResponse = response.getResponseBody();
                    assertNotNull(movementResponse);
                    assertEquals("35847efi4", movementResponse.getId());
                    assertEquals("AHORRO", movementResponse.getProductType());
                });
    }

    @Test
    void update() {
        given(this.productService.update(any(String.class), any( ProductRequest.class)))
                .willAnswer(invocationOnMock -> Mono.empty());

        client.put().uri(URI.concat("/35847efi4"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(this.productRequest), ProductRequest.class)
                .exchange()
                .expectStatus()
                .isAccepted();
    }

    @Test
    void delete() {
        when(this.productService.delete(anyString()))
                .thenReturn(Mono.empty());

        client.delete().uri(URI.concat("/35847efi4"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}