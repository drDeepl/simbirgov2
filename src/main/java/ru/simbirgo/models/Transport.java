package ru.simbirgo.models;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Account ownerId;

    @Column(name="can_be_ranted")
    private Boolean canBeRented;

//    @Enumerated(EnumType.STRING)
    @Column(name="transport_type")
    private String transportType;

    @Column
    private String model;

    @Column
    private String color;

    @Column
    private String identifier;

    @Column(name="description")
    private String description;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name="minute_price")
    private Double minutePrice;

    @Column(name="day_price")
    private Double dayPrice;

}
