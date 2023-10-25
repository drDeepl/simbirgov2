package ru.simbirgo.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
public class CreateTransportUserRequest {
    private Boolean canBeRented;

    private String transportType;

    private String model;

    private String color;

    private String identifier;
    private String description;

    private Double latitude;

    private Double longitude;
    private Double minutePrice;
    private Double dayPrice;
}
