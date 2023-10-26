package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRentAdminRequest {

    private Long transportId;

    private Long userId; // == acocuntId

    private String timeStart;

    private String timeEnd;

    private Double priceOfUnit;

    private String priceType;

    private Double finalPrice;



}
