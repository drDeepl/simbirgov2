package ru.simbirgo.controllers;


import com.google.common.base.Throwables;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.ErrorMessageDTO;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.Rent;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.CreateTransportUserRequest;
import ru.simbirgo.repositories.TransportRepository;
import ru.simbirgo.services.TransportService;

import java.sql.SQLException;

@Tag(name="TransportController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transport")
public class TransportController {
    private final Logger LOGGER = LoggerFactory.getLogger(AdminTransportController.class);


    @Autowired
    TransportRepository transportRepository;

    @Autowired
    TransportService transportService;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @Operation(summary = "получение транспорта по его id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransportById(@PathVariable("id") Long id) {
        LOGGER.info("GET TRANSPORT BY ID");
        try {
            Transport transport = transportService.findById(id);
            return ResponseEntity.ok(transport);
        } catch (TransportNotExistsException e) {
            return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), String.format("транспорта с id %s не существует", id)), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "добавление нового транспорта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("")
    public ResponseEntity<?> addNewTransport(@RequestBody CreateTransportUserRequest createTransportUserRequest, HttpServletRequest httpServletRequest) {
        LOGGER.info("ADD NEW TRANSPORT");
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        Long ownerId = jwtUtils.getAccountIdFromJWT(token);
        try {
            Transport newTransport = transportService.createTransportForUser(createTransportUserRequest, ownerId);
            return new ResponseEntity<Transport>(newTransport, HttpStatus.OK);
        }
        catch (IllegalArgumentException IAE) {
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
        }
        catch (RuntimeException re) {
            Throwable rootCause = Throwables.getRootCause(re);
            if (rootCause instanceof SQLException) {
                if ("23502".equals(((SQLException) rootCause).getSQLState())) {
                    return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "пропущены обязательные поля"), HttpStatus.CONFLICT);
                }

            }
        }
        return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "что-то пошло не так"), HttpStatus.FORBIDDEN);
    }


    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @Operation(summary="изменение транспорта по его id")
    @PutMapping("/{id}")
    public ResponseEntity<?>  updateTransportById(@PathVariable("id") Long transportId, @RequestBody CreateTransportUserRequest updateTransportUserRequest, HttpServletRequest httpServletRequest){
        LOGGER.info("UPDATE TRANSPORT BY ID");
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
        Long ownerId = transportRepository.findOwnerIdByTransportId(transportId);

        if(currentAccountId.equals(ownerId)){
            try {
                Transport updatedTransport = transportService.updateTransportUserReq(transportId, ownerId, updateTransportUserRequest);
                return new ResponseEntity<>(updatedTransport, HttpStatus.OK);
            }
            catch (IllegalArgumentException IAE) {
                LOGGER.error(IAE.getMessage());
                return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный вид транспорта"), HttpStatus.CONFLICT);
            }
            catch (RuntimeException re) {
                Throwable rootCause = Throwables.getRootCause(re);
                if (rootCause instanceof SQLException) {
                    if ("23502".equals(((SQLException) rootCause).getSQLState())) {
                        return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "пропущены обязательные поля"), HttpStatus.CONFLICT);
                    }
                }
            }
        }
        return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "что-то пошло не так"), HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "удаление транспорта по id")
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransportByIdUserReq(@PathVariable("id") Long transportId, HttpServletRequest httpServletRequest){
        LOGGER.info("DELETE TRANSPORT BY ID");
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
        Long ownerId = transportRepository.findOwnerIdByTransportId(transportId);

        if(currentAccountId.equals(ownerId)) {
            transportService.deleteById(transportId);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "возможно удалить только свой транспорт"), HttpStatus.FORBIDDEN);
    }

}