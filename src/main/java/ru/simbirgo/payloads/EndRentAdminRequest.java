package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndRentAdminRequest {
    private Double lat;
    private Double lng;
}
