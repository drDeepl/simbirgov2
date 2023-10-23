package ru.simbirgo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.CreateTransportAdminRequest;
import ru.simbirgo.repositories.TransportRepository;

import java.util.List;

@Service
public class TransportService {
    private final Logger LOGGER = LoggerFactory.getLogger(TransportService.class);
    @Autowired
    private TransportRepository transportRepository;

    public Transport findById(Long id){
        return transportRepository.findById(id).orElseThrow(() -> new TransportNotExistsException("транспорт не найден"));
    }

    public Transport updateTransportForAdmin(Long id, CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("UPDATE TRANSPORT FOR ADMIN");
        Transport transport = transportRepository.findById(id).orElseThrow(() -> new TransportNotExistsException("транспорт не найден"));
        transport.setOwnerId(createTransportAdminRequest.getOwnerId());
        transport.setCanBeRented(createTransportAdminRequest.getCanBeRented());
        transport.setTransportType(createTransportAdminRequest.getTransportType());
        transport.setModel(createTransportAdminRequest.getModel());
        transport.setColor(createTransportAdminRequest.getColor());
        transport.setIdentifier(createTransportAdminRequest.getIdentifier());
        transport.setDescription(createTransportAdminRequest.getDescription());
        transport.setLatitude(createTransportAdminRequest.getLatitude());
        transport.setLongitude(createTransportAdminRequest.getLongitude());
        transport.setMinutePrice(createTransportAdminRequest.getMinutePrice());
        transport.setDayPrice(createTransportAdminRequest.getDayPrice());
        return transportRepository.save(transport);
    }

    public void deleteById(Long id){
        LOGGER.info("DELETE BY ID");
        transportRepository.deleteById(id);
    }

    public List<Transport> findTransports(int start, int count,String transportType){
        LOGGER.info("FIND TRANSPORTS");
        Pageable pageable = PageRequest.of(start, count);
       return transportRepository.findAllByTransportType(transportType,pageable);
    }

}
