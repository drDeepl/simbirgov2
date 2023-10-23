package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.simbirgo.models.ETransportType;

@Data
@AllArgsConstructor
public class FindTransportsRequest {
    private int start;
    private int count;
    private ETransportType transportType;
}
