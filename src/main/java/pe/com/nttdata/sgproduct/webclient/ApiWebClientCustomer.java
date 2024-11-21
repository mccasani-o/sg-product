package pe.com.nttdata.sgproduct.webclient;

import com.nttdata.sgproduct.model.CustomerResponse;

import reactor.core.publisher.Mono;


public interface ApiWebClientCustomer  {

    Mono<CustomerResponse> findByClientId(String id);

}
