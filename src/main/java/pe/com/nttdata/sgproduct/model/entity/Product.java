package pe.com.nttdata.sgproduct.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String productType;

    private Double balance;

    private Integer limitMnthlyMovements;

    private String dayMovement;
    private BigDecimal limitCredit;
    private String clientId;
}
