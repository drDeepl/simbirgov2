package ru.simbirgo.services;

import org.modelmapper.ModelMapper;
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
import ru.simbirgo.payloads.CreateTransportUserRequest;
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

    @Autowired
    private ModelMapper modelMapper;

    public Transport createTransportForAdmin(CreateTransportAdminRequest createTransportAdminRequest) throws IllegalArgumentException{
        LOGGER.info("CREATE TRANSPORT");
        ETransportType.valueOf(createTransportAdminRequest.getTransportType().toUpperCase());
        long ownerId = createTransportAdminRequest.getOwnerId();
        Account ownerAccount = accountRepository.findById(ownerId).orElseThrow(() -> new AccountExistsException(String.format("аккаунт с id %s не найден", ownerId)));
        Transport transport = modelMapper.map(createTransportAdminRequest, Transport.class);
        transport.setOwnerId(ownerAccount);
        return transportRepository.save(transport);
    }

    public Transport createTransportForUser(CreateTransportUserRequest createTransportUserRequest, Long ownerId){
        ETransportType.valueOf(createTransportUserRequest.getTransportType().toUpperCase());
        Transport transport = modelMapper.map(createTransportUserRequest, Transport.class);
        Account accountOwner = accountRepository.findById(ownerId).orElseThrow(() -> new AccountNotExistsException(String.format("аккаунт владельца с id %s не найден", ownerId)));
        transport.setOwnerId(accountOwner);
        return transportRepository.save(transport);
    }

    public Transport findById(Long id){
        return transportRepository.findById(id).orElseThrow(() -> new TransportNotExistsException("транспорт не найден"));
    }

    public Transport updateTransportForAdmin(Long id, CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("UPDATE TRANSPORT FOR ADMIN");
        ETransportType.valueOf(createTransportAdminRequest.getTransportType().toUpperCase());
        Account owner = accountRepository.findById(createTransportAdminRequest.getOwnerId()).orElseThrow(() -> new AccountNotExistsException("аккаунт владельца не найден"));
        Transport transport = modelMapper.map(createTransportAdminRequest, Transport.class);
        transport.setId(id);
        transport.setOwnerId(owner);
        return transportRepository.save(transport);
    }

    public Transport updateTransportUserReq(Long transportId, Long ownerId, CreateTransportUserRequest transportForUpdate){
        LOGGER.info("UPDATE TRANSPORT USER REQ");
        ETransportType.valueOf(transportForUpdate.getTransportType().toUpperCase());
        Transport transport = modelMapper.map(transportForUpdate, Transport.class);
        transport.setId(transportId);
        Account owner = accountRepository.findById(ownerId).get();
        transport.setOwnerId(owner);
        return transportRepository.save(transport);
    }

    public void deleteById(Long id){
        LOGGER.info("DELETE BY ID");
        transportRepository.deleteById(id);
    }


    public List<Transport> findTransports(int start, int count, String transportType){
        LOGGER.info("FIND TRANSPORTS");
        Pageable pageable = PageRequest.of(start, count);

       return transportRepository.findAllByTransportType(transportType.toUpperCase(),pageable);
    }

    public List<Transport> findAllTypeTransports(int start, int count){
        LOGGER.info("FIND ALL TYPES TRANSPORTS");
        Pageable pageable = PageRequest.of(start, count);
        return transportRepository.findAllTransports(pageable);
    }

    public void updateLongitudeAndLatitude(Long transportId, Double longitude, Double latitude){
        LOGGER.info("UPDATE LONGITUTE AND LATITUDE OF TRANSPORT");
        Transport transport = transportRepository.findById(transportId).orElseThrow(() -> new TransportNotExistsException(String.format("транспорт с id %s не найден", transportId)));
        transport.setLongitude(longitude);
        transport.setLatitude(latitude);
    }

    public List<Transport> findTransportByParams(Double lat, Double lng, Double radius){
        LOGGER.info("find transport by params");

        return transportRepository.findTransportByParams(lat,lng, radius);
    }

    public List<Transport>  findTransportByParamsFilterType(Double lat, Double lng, Double radius, String transportType){
        LOGGER.info("FIND TRANSPORTS BY PARAMS AND FILTER TRANSPORT TYPE");
        return transportRepository.findTransportByParamsFilterTransportType(lat,lng, radius, transportType);
    }

}
