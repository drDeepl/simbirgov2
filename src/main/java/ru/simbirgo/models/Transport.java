package ru.simbirgo.models;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.simbirgo.models.serializers.TransportSerializer;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="transports")
@JsonSerialize(using= TransportSerializer.class)
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(name="ownerId", example = "0")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account ownerId;

    @Column(name="can_be_ranted", nullable = false)
    private Boolean canBeRented;

//    @Enumerated(EnumType.STRING)
    @Column(name="transport_type", nullable = false)
    private String transportType;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String identifier;

    @Column(name="description")
    private String description;

    @Column
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name="minute_price")
    private Double minutePrice;

    @Column(name="day_price")
    private Double dayPrice;

}
