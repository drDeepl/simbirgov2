package ru.simbirgo.dtos;



import lombok.AllArgsConstructor;
import lombok.Data;



import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class RentDTO{

    private Long id;
    private Long transport;
    private Long account;
    private Timestamp timeStart;
    private Timestamp timeEnd;
    private Double priceOfUnit;
    private String priceOfType;
    private Double finalPrice;

}
