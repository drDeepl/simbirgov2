package ru.simbirgo.controllers;

import com.google.common.base.Throwables;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
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
import ru.simbirgo.dtos.ErrorMessageDTO;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.exceptions.*;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Rent;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.CreateTransportAdminRequest;
import ru.simbirgo.payloads.FindTransportsRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.TransportRepository;
import ru.simbirgo.repositories.interfaces.TransportI;
import ru.simbirgo.services.AccountService;
import ru.simbirgo.services.TransportService;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
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
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = TransportDTO.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("")
    public ResponseEntity<List<Transport>> findTransports(@RequestBody FindTransportsRequest findTransportsRequest){
        LOGGER.info("FIND TRANSPORTS");
        try{
            if(StringUtils.equals(findTransportsRequest.getTransportType().toLowerCase(),"all")){
                List<Transport> transportsAllTypes = transportService.findAllTypeTransports(findTransportsRequest.getStart(), findTransportsRequest.getCount());
                return ResponseEntity.ok(transportsAllTypes);
            }
            List<Transport> transports = transportService.findTransports(findTransportsRequest.getStart(), findTransportsRequest.getCount(), findTransportsRequest.getTransportType());
            return  ResponseEntity.ok(transports);

        }
        catch (IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary="получение информации о транспортном средстве по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = TransportDTO.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
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
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("")
    public ResponseEntity<?> createTransport(@RequestBody CreateTransportAdminRequest createTransportAdminRequest) {
        LOGGER.info("CREATE TRANSPORT");
        try {
            Transport createdTransport = transportService.createTransportForAdmin(createTransportAdminRequest);
            return new ResponseEntity<Transport>(createdTransport, HttpStatus.OK);
        } catch (IllegalArgumentException IAE) {
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        } catch (RuntimeException re) {
            Throwable rootCause = Throwables.getRootCause(re);
            if (rootCause instanceof SQLException) {
                if ("23502".equals(((SQLException) rootCause).getSQLState())) {
                    return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "пропущены обязательные поля"), HttpStatus.CONFLICT);
                }

            }
        }

        return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "что-то пошло не так"), HttpStatus.CONFLICT);
    }




    @Operation(summary="изменение транспортного средства по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransportById(@PathVariable("id") Long id, @RequestBody CreateTransportAdminRequest createTransportAdminRequest){
        LOGGER.info("UPDATE TRANSPORT BY ID");
        try{
        Transport updatedTransport = transportService.updateTransportForAdmin(id, createTransportAdminRequest);
        return new ResponseEntity<Transport>(updatedTransport, HttpStatus.OK);
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

    @Operation(summary = "удаление транспортного средства")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ResponseEntity.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteTransportById(@PathVariable("id") Long id){
        LOGGER.info("DELETE TRANSPORT BY ID");
        transportService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
