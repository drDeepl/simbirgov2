package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRentRequest {

    private Double priceOfUnit;
    private String rentType;
}
