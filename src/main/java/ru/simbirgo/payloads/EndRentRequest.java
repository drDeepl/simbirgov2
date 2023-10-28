package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndRentRequest {
    private Double lat;
    private Double lng;
}
