package pe.com.nttdata.sgproduct.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



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

    private Integer dayMovement;

    private String clientId;
}
