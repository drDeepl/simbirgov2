package ru.simbirgo.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.simbirgo.models.Transport;

import java.io.IOException;

public class TransportSerializer extends StdSerializer<Transport> {

    public TransportSerializer(){
        this(null);
    }

    public TransportSerializer(Class<Transport> t){
        super(t);
    }
    @Override
    public void serialize(Transport transport, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", transport.getId());
        jsonGenerator.writeNumberField("ownerId", transport.getOwnerId().getId());
        jsonGenerator.writeStringField("transportType", transport.getTransportType().toUpperCase());
        jsonGenerator.writeBooleanField("canBeRented", transport.getCanBeRented());
        jsonGenerator.writeStringField("model", transport.getModel());
        jsonGenerator.writeStringField("color", transport.getColor());
        jsonGenerator.writeStringField("identifier", transport.getIdentifier());
        if(transport.getDescription() == null){
            jsonGenerator.writeNullField("description");
        }
        else{
            jsonGenerator.writeStringField("description", transport.getDescription());
        }
        jsonGenerator.writeNumberField("latitude", transport.getLatitude());
        jsonGenerator.writeNumberField("longitude", transport.getLongitude());
        if(transport.getMinutePrice() == null){
            jsonGenerator.writeNullField("minutePrice");
        }
        else{
            jsonGenerator.writeNumberField("minutePrice", transport.getMinutePrice());
        }
        if(transport.getDayPrice() == null){
            jsonGenerator.writeNullField("dayPrice");
        }
        else {
            jsonGenerator.writeNumberField("dayPrice", transport.getDayPrice());
        }

        jsonGenerator.writeEndObject();


    }
}
