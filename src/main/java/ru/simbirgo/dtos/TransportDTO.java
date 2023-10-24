package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.simbirgo.payloads.CreateTransportAdminRequest;

@Data
@AllArgsConstructor
public class TransportDTO{
    private Long id;
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
