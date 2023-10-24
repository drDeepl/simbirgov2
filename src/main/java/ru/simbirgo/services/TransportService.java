package ru.simbirgo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.exceptions.AccountNotExistsException;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.Account;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.CreateTransportAdminRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.TransportRepository;
import ru.simbirgo.repositories.interfaces.TransportI;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Arrays;
import java.util.List;

@Service
public class TransportService {
    private final Logger LOGGER = LoggerFactory.getLogger(TransportService.class);
    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transport createTransport(CreateTransportAdminRequest createTransportAdminRequest) throws IllegalArgumentException{
        LOGGER.info("CREATE TRANSPORT");
        ETransportType transportType = ETransportType.valueOf(createTransportAdminRequest.getTransportType().toUpperCase());
        long ownerId = createTransportAdminRequest.getOwnerId();
        Account ownerAccount = accountRepository.findById(ownerId).orElseThrow(() -> new AccountExistsException(String.format("аккаунт с id %s не найден", ownerId)));
        Transport transport = new Transport();
        transport.setOwnerId(ownerAccount);
        transport.setCanBeRented(createTransportAdminRequest.getCanBeRented());
        transport.setTransportType(createTransportAdminRequest.getTransportType());
        transport.setModel(createTransportAdminRequest.getModel());
        transport.setColor(createTransportAdminRequest.getColor());
        transport.setIdentifier(createTransportAdminRequest.getIdentifier());
        transport.setDescription(createTransportAdminRequest.getDescription());;
        transport.setLatitude(createTransportAdminRequest.getLatitude());
        transport.setLongitude(createTransportAdminRequest.getLongitude());
        transport.setMinutePrice(createTransportAdminRequest.getMinutePrice());
        transport.setDayPrice(createTransportAdminRequest.getDayPrice());
        return transportRepository.save(transport);
    }

    public Transport findById(Long id){
        return transportRepository.findById(id).orElseThrow(() -> new TransportNotExistsException("транспорт не найден"));
    }

    public Transport updateTransportForAdmin(Long id, CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("UPDATE TRANSPORT FOR ADMIN");
        ETransportType transportType = ETransportType.valueOf(createTransportAdminRequest.getTransportType().toUpperCase());
        Account owner = accountRepository.findById(createTransportAdminRequest.getOwnerId()).orElseThrow(() -> new AccountNotExistsException("аккаунт владельца не найден"));
        Transport transport = transportRepository.findById(id).orElseThrow(() -> new TransportNotExistsException("транспорт не найден"));
        transport.setOwnerId(owner);
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


    public List<Transport> findTransports(int start, int count, String transportType){
        LOGGER.info("FIND TRANSPORTS");
        Pageable pageable = PageRequest.of(start, count);
        ETransportType typeTransport = ETransportType.valueOf(transportType.toUpperCase());
       return transportRepository.findAllByTransportType(transportType,pageable);
    }

}
