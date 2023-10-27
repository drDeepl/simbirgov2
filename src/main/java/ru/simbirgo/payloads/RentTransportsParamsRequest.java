package ru.simbirgo.payloads;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentTransportsParamsRequest {
    private Double lat;
    private Double lng;
    @Schema(description = "радиус считается в км")
    private Double radius;
    private String type;
}
