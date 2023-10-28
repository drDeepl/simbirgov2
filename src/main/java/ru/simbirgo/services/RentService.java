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
import ru.simbirgo.payloads.EndRentRequest;
import ru.simbirgo.payloads.NewRentRequest;

import ru.simbirgo.repositories.RentRepository;


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
        transport.setCanBeRented(false);
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

    public Rent updateRent(Long rentId, CreateRentAdminRequest createRentAdminRequest){
        LOGGER.info("UPDATE RENT");

        EPriceType priceType = EPriceType.valueOf(createRentAdminRequest.getPriceType().toUpperCase());
        Rent rentToUpdate = rentRepository.findById(rentId).orElseThrow(() -> new RentNotExistsException(String.format("аренды с id %s не существует", rentId)));

        if(!rentToUpdate.getTransport().getId().equals(createRentAdminRequest.getTransportId())){
            Transport transport = transportService.findById(createRentAdminRequest.getTransportId());
            rentToUpdate.setTransport(transport);
        }

        if(!rentToUpdate.getAccount().getId().equals(createRentAdminRequest.getUserId())){
            Account account = accountService.getAccountById(createRentAdminRequest.getUserId());
            rentToUpdate.setAccount(account);
        }

        rentToUpdate.setTimeStart(Timestamp.valueOf(createRentAdminRequest.getTimeStart()));
        if(createRentAdminRequest.getTimeEnd() == null){
            rentToUpdate.setTimeEnd(null);
        }
        else{
            rentToUpdate.setTimeEnd(Timestamp.valueOf(createRentAdminRequest.getTimeEnd()));
        }

        rentToUpdate.setPriceOfType(priceType.name());
        rentToUpdate.setFinalPrice(createRentAdminRequest.getFinalPrice());

        return rentRepository.save(rentToUpdate);
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

    public Rent endRentById(Long rentId, EndRentRequest endRentRequest){
        LOGGER.info("END RENT BY ID");
        Timestamp endTimeRent = new Timestamp(System.currentTimeMillis());
        Rent rent = rentRepository.findById(rentId).orElseThrow(() -> new RentNotExistsException(String.format("аренды с id %s не существует", rentId)));
        Transport ts = rent.getTransport();
        ts.setLongitude(endRentRequest.getLng());
        ts.setLatitude(endRentRequest.getLat());
        ts.setCanBeRented(true);
        rent.setTimeEnd(endTimeRent);
        rentRepository.save(rent);
        return rent;
    }

    public void deleteById(Long id){
        LOGGER.info("DELETE BY ID");
        rentRepository.deleteById(id);
    }

    public List<Rent> finByAccountIdAll(Long accountId){
        LOGGER.info("FIND RENTS BY ACCOUNT ID");
        return rentRepository.findRentsByAccountId(accountId);

    }

    public void newRent(Long rentAccountId, Transport transport, NewRentRequest newRentRequest){
        LOGGER.info("NEW RENT");
        Timestamp startTimeRent = new Timestamp(System.currentTimeMillis());
        Account accountRent = accountService.getAccountById(rentAccountId);
        transport.setCanBeRented(false);
        Rent newRent = new Rent();
        newRent.setAccount(accountRent);
        newRent.setTransport(transport);
        newRent.setTimeStart(startTimeRent);
        newRent.setPriceOfType(newRentRequest.getRentType());
        newRent.setPriceOfUnit(newRentRequest.getPriceOfUnit());
        rentRepository.save(newRent);

    }


}
