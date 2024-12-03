package pe.com.nttdata.sgproduct.webclient;


import pe.com.nttdata.sgproduct.webclient.dto.CustomerDto;
import reactor.core.publisher.Mono;


public interface ApiWebClientCustomer  {

    Mono<CustomerDto> findByClientId(String id);

}
