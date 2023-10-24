package ru.simbirgo.repositories.interfaces;

import jakarta.persistence.*;
import lombok.Data;
import ru.simbirgo.models.Account;
import ru.simbirgo.models.ETransportType;


public interface TransportI {

     Long getId();
     Long getOwnerId();
     Boolean getCanBeRented();
     String getTransportType();
     String getModel();
     String getColor();
     String getIdentifier();
     String getDescription();
     Double getLatitude();
     Double getLongitude();
     Double getMinutePrice();
     Double getDayPrice();
}
