package pe.com.nttdata.sgproduct.webclient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private String id;

    private Integer clientType;

    private Integer documentType;

    private String documentNumber;

    private String name;

    private String lastName;

    private String email;
}