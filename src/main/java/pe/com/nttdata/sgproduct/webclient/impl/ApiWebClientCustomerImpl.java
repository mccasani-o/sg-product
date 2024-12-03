package pe.com.nttdata.sgproduct.webclient.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.com.nttdata.sgproduct.exception.CustomerException;
import pe.com.nttdata.sgproduct.webclient.ApiWebClientCustomer;
import pe.com.nttdata.sgproduct.webclient.dto.CustomerDto;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class ApiWebClientCustomerImpl implements ApiWebClientCustomer {

    private final WebClient webClient;

    public ApiWebClientCustomerImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8882/api/v1").build();
    }


    @Override
    public Mono<CustomerDto> findByClientId(String id) {
        return this.webClient.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Client not found for ID: {}", id);
                    return Mono.error(new CustomerException("Client not found", "404", HttpStatus.NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Server error while retrieving client for ID: {}", id);
                    return Mono.error(new CustomerException("Ocurrió un error en el servidor", "500", HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .bodyToMono(CustomerDto.class)
                .doOnNext(customer ->
                        log.info("Client retrieved successfully: {}", customer)
                )
                .doOnError(error ->
                        log.error("Error retrieving client for ID {}: {}", id, error.getMessage())
                );
    }





}
