package ru.simbirgo.payloads;

import lombok.Data;
import ru.simbirgo.models.ETransportType;

@Data
public class CreateTransportAdminRequest {
    private Long ownerId;
    private Boolean canBeRented;
    private ETransportType transportType;
    private String model;
    private String color;
    private String identifier;
    private String description;
    private Double latitude;
    private Double longitude;
    private Double minutePrice;
    private Double dayPrice;

}
