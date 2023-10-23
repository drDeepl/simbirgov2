package ru.simbirgo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="transports")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Long ownerId;
    @Column(name="can_be_ranted")
    private Boolean canBeRented;
    @Enumerated(EnumType.STRING)
    @Column(name="transport_type")
    private ETransportType transportType;
    private String model;
    private String color;
    private String identifier;
    @Column(name="description")
    private String description;
    private Double latitude;
    private Double longitude;
    @Column(name="minute_price")
    private Double minutePrice;
    @Column(name="day_price")
    private Double dayPrice;







}
