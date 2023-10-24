package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.exceptions.AccountNotExistsException;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.CreateTransportAdminRequest;
import ru.simbirgo.payloads.FindTransportsRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.TransportRepository;
import ru.simbirgo.repositories.interfaces.TransportI;
import ru.simbirgo.services.AccountService;
import ru.simbirgo.services.TransportService;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Arrays;
import java.util.List;

@Tag(name="AdminTransportController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/transport")
public class AdminTransportController {
    private final Logger LOGGER = LoggerFactory.getLogger(AdminTransportController.class);


    @Autowired
    TransportRepository transportRepository;

    @Autowired
    TransportService transportService;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary="получение списка всех транспортных средств")
    @GetMapping("")
    public ResponseEntity<List<Transport>> findTransports(@RequestBody FindTransportsRequest findTransportsRequest){
        LOGGER.info("FIND TRANSPORTS");
        try{
        List<Transport> transports = transportService.findTransports(findTransportsRequest.getStart(), findTransportsRequest.getCount(), findTransportsRequest.getTransportType());
        return  ResponseEntity.ok(transports);
        }
        catch (IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary="получение информации о транспортном средстве по id")
    @GetMapping("/{id}")
    public ResponseEntity<Transport> getTransportById(@PathVariable("id") Long id){
        LOGGER.info("GET TRANSPORT BY ID");
        try{
            Transport transport = transportService.findById(id);
            return new ResponseEntity<>(transport, HttpStatus.OK);
        }
        catch (TransportNotExistsException t){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary="создание транспортного средства")
    @PostMapping("")
    public ResponseEntity createTransport(@RequestBody CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("CREATE TRANSPORT");
        try{
        Transport createdTransport = transportService.createTransport(createTransportAdminRequest);
        return new ResponseEntity<>(createdTransport, HttpStatus.OK);
        }
        catch (IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        }
    }



    @Operation(summary="изменение транспортного средства по id")
    @PutMapping("/{id}")
    public ResponseEntity updateTransportById(@PathVariable("id") Long id, @RequestBody CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("UPDATE TRANSPORT BY ID");
        try{
        transportService.updateTransportForAdmin(id, createTransportAdminRequest);

        return ResponseEntity.ok("транспортное средство изменено");
        }
        catch (java.lang.IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        }

        catch (AccountNotExistsException AEE){
            LOGGER.error(AEE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не найден аккаунт владельца транспорта"), HttpStatus.CONFLICT);
        }
    }

}
