package ru.simbirgo.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simbirgo.models.serializers.RentSerializer;



import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="rents")
@JsonSerialize(using= RentSerializer.class)
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(example="0")
    @ManyToOne(fetch =  FetchType.LAZY)
    private Transport transport;

    @Schema(example = "0")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(nullable = false)
    private Timestamp timeStart;

    @Column
    private Timestamp timeEnd;

    @Column(nullable = false)
    private Double priceOfUnit;

    @Column(nullable = false)
    private String priceOfType;

    @Column
    private Double finalPrice;


}
