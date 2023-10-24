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
        jsonGenerator.writeBooleanField("canBeRentedet", transport.getCanBeRented());
        jsonGenerator.writeStringField("model", transport.getModel());
        jsonGenerator.writeStringField("color", transport.getColor());
        jsonGenerator.writeStringField("identifier", transport.getIdentifier());
        jsonGenerator.writeStringField("description", transport.getDescription());
        jsonGenerator.writeNumberField("latitude", transport.getLatitude());
        jsonGenerator.writeNumberField("longitude", transport.getLongitude());
        jsonGenerator.writeNumberField("minutePrice", transport.getMinutePrice());
        jsonGenerator.writeNumberField("dayPrice", transport.getDayPrice());
        jsonGenerator.writeEndObject();


    }
}
