package pe.com.nttdata.sgproduct.webclient.impl;

import lombok.extern.slf4j.Slf4j;
import com.nttdata.sgproduct.model.CustomerResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.com.nttdata.sgproduct.webclient.ApiWebClientCustomer;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class ApiWebClientCustomerImpl implements ApiWebClientCustomer {

    private final WebClient webClient;

    public ApiWebClientCustomerImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8882/api/v1").build();
    }


    @Override
    public Mono<CustomerResponse> findByClientId(String id) {
        return this.webClient.get()
                .uri("/customers/{id}", id)

                .retrieve()

                .bodyToMono(CustomerResponse.class);
    }




}
