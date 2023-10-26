package ru.simbirgo.services;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.simbirgo.exceptions.AccountNotExistsException;
import ru.simbirgo.exceptions.RentNotExistsException;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.*;
import ru.simbirgo.payloads.CreateRentAdminRequest;
import ru.simbirgo.payloads.EndRentAdminRequest;
import ru.simbirgo.payloads.UpdateRentAdminRequest;

import ru.simbirgo.repositories.RentRepository;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Service
public class RentService {
    private final Logger LOGGER = LoggerFactory.getLogger(RentService.class);
    @Autowired
    private TransportService transportService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RentRepository rentRepository;


    @Autowired
    private ModelMapper modelMapper;

    public Rent createRent(CreateRentAdminRequest createRentAdminRequest) throws IllegalArgumentException, AccountNotExistsException, TransportNotExistsException {
        LOGGER.info("CREATE RENT");

        EPriceType priceType = EPriceType.valueOf(createRentAdminRequest.getPriceType().toUpperCase());
        Rent rent = new Rent();
        Transport transport = transportService.findById(createRentAdminRequest.getTransportId());
        Account account = accountService.getAccountById(createRentAdminRequest.getUserId());
        rent.setAccount(account);
        rent.setTransport(transport);
        rent.setTimeStart(Timestamp.valueOf(createRentAdminRequest.getTimeStart()));
        if(createRentAdminRequest.getTimeEnd() == null){
            rent.setTimeEnd(null);
        }
        else{
            rent.setTimeEnd(Timestamp.valueOf(createRentAdminRequest.getTimeEnd()));
        }
        rent.setPriceOfUnit(createRentAdminRequest.getPriceOfUnit());
        rent.setPriceOfType(priceType.name());
        rent.setFinalPrice(createRentAdminRequest.getFinalPrice());

        return rentRepository.save(rent);
    }

    public Rent updateRent(UpdateRentAdminRequest updateRentAdminRequest){
        LOGGER.info("UPDATE RENT");

        EPriceType.valueOf(updateRentAdminRequest.getPriceType().toUpperCase());
        Rent rent = modelMapper.map(updateRentAdminRequest, Rent.class);
        return null;
    }

    public Rent findById(Long id) throws RentNotExistsException{
        LOGGER.info("FIND RENT BY ID");
        return rentRepository.findById(id).orElseThrow(() -> new RentNotExistsException(String.format("аренды с id %s не существует", id)));
    }

    public List<Rent> findRentsByAccountId(Long accountId) throws AccountNotExistsException{
        LOGGER.info("FIND RENT HISTORY BY ACCOUNT ID");
        List<Rent> rents = rentRepository.findRentsByAccountId(accountId);
        return rents;
    }

    public List<Rent> findRentsByTransportId(Long transportId){
        LOGGER.info("FIND RENT BY TRANSPORT ID");
        List<Rent> rentsByTransportId = rentRepository.findRentsByTransportId(transportId);
        return rentsByTransportId;
    }

    public Rent endRentById(Long rentId, EndRentAdminRequest endRentAdminRequest){
        LOGGER.info("END RENT BY ID");
        // INFO: Как грамотно обновить координаты транспорта и окончание аренды?
        Timestamp endTimeRent = new Timestamp(System.currentTimeMillis());
        Rent rent = rentRepository.findById(rentId).orElseThrow(() -> new RentNotExistsException(String.format("аренды с id %s не существует", rentId)));
        Transport ts = rent.getTransport();
        ts.setLongitude(endRentAdminRequest.getLng());
        ts.setLatitude(endRentAdminRequest.getLat());
//        transportService.updateLongitudeAndLatitude(rent.getTransport().getId(), endRentAdminRequest.getLng(), endRentAdminRequest.getLat());
        rent.setTimeEnd(endTimeRent);
        rentRepository.save(rent);
        return rent;



    }


}
