package ru.simbirgo.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.simbirgo.models.Rent;
import ru.simbirgo.models.Transport;

import java.io.IOException;

public class RentSerializer extends StdSerializer<Rent> {

    public RentSerializer(){
        this(null);
    }

    public RentSerializer(Class<Rent> r){
        super(r);
    }
    @Override
    public void serialize(Rent rent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", rent.getId());
        jsonGenerator.writeNumberField("transportId", rent.getTransport().getId());
        jsonGenerator.writeNumberField("userId", rent.getAccount().getId());
        jsonGenerator.writeStringField("timeStart", rent.getTimeStart().toString());
        if(rent.getTimeEnd() == null){
            jsonGenerator.writeNullField("timeEnd");
        }
        else{
            jsonGenerator.writeStringField("timeEnd", rent.getTimeEnd().toString());
        }

        jsonGenerator.writeStringField("priceType", rent.getPriceOfType());
        jsonGenerator.writeNumberField("priceOfUnit", rent.getPriceOfUnit());
        if(rent.getFinalPrice() == null){
            jsonGenerator.writeNullField("finalPrice");
        }
        else{
            jsonGenerator.writeNumberField("finalPrice", rent.getFinalPrice());
        }
        jsonGenerator.writeEndObject();


    }
}
