package ru.simbirgo.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.simbirgo.models.ETransportType;

@Data
public class CreateTransportAdminRequest {
    private Long ownerId;
    
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
