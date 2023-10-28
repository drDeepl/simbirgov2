package ru.simbirgo.controllers;

import com.google.common.base.Throwables;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.ErrorMessageDTO;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.exceptions.AccountNotExistsException;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.exceptions.RentNotExistsException;
import ru.simbirgo.exceptions.TransportNotExistsException;
import ru.simbirgo.models.Rent;
import ru.simbirgo.payloads.CreateRentAdminRequest;
import ru.simbirgo.payloads.EndRentRequest;
import ru.simbirgo.repositories.RentRepository;
import ru.simbirgo.services.RentService;
import ru.simbirgo.services.TransportService;

import java.sql.SQLException;
import java.util.List;

@Tag(name="AdminRentController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin")
public class AdminRentController {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminRentController.class);

    @Autowired
    RentRepository rentRepository;

    @Autowired
    TransportService transportService;

    @Autowired
    RentService rentService;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary="создание новой аренды")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = Rent.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("/rent")
    public ResponseEntity<?> createNewRent(@RequestBody CreateRentAdminRequest createRentAdminRequest){
        LOGGER.info("CREATE NEW RENT");
        LOGGER.error(createRentAdminRequest.getTransportId().toString());
        try {
            Rent createdRent = rentService.createRent(createRentAdminRequest);
            return new ResponseEntity<>(createdRent, HttpStatus.OK);
        }
        catch (IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "не действительный priceType"), HttpStatus.CONFLICT);
        }
        catch (AccountNotExistsException ANE){
            LOGGER.error(ANE.getMessage());
            return new ResponseEntity(new AppException(HttpStatus.NOT_FOUND.value(), ANE.getMessage()), HttpStatus.NOT_FOUND);

        }

        catch (TransportNotExistsException TNE){
            LOGGER.error(TNE.getMessage());
            return new ResponseEntity(new AppException(HttpStatus.NOT_FOUND.value(), TNE.getMessage()), HttpStatus.NOT_FOUND);

        }

        catch (RuntimeException re) {
            Throwable rootCause = Throwables.getRootCause(re);
            if (rootCause instanceof SQLException) {
                if ("23502".equals(((SQLException) rootCause).getSQLState())) {
                    return new ResponseEntity(new AppException(HttpStatus.CONFLICT.value(), "пропущены обязательные поля"), HttpStatus.CONFLICT);
                }

            }
        }

        return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "что-то пошло не так"), HttpStatus.CONFLICT);
    }

    @Operation(summary = "получение информации по аренде по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = Rent.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/rent/{id}")
    public ResponseEntity<?> getInfoRent(@PathVariable("id") Long id){
        LOGGER.info("GET INFO RENT");
        try {
            return new ResponseEntity<Rent>(rentService.findById(id), HttpStatus.OK);
        }
        catch (RentNotExistsException RNE){
            return new ResponseEntity<AppException>(new AppException(HttpStatus.NOT_FOUND.value(), RNE.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "получение истории аренд пользователя с id={userId}")
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/user_history/{userId}")
    public ResponseEntity<List<Rent>> getInfoRentHistoryOfUser(@PathVariable("userId") Long id){
        LOGGER.info("GET INFO RENT HISTORY OF USER");
        List<Rent> rents = rentService.findRentsByAccountId(id);
        return new ResponseEntity<List<Rent>>(rents, HttpStatus.OK);
    }

    @Operation(summary = "получение истории аренд транспорта с id={transportId}")
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/transport_history/{transportId}")
    public ResponseEntity<List<Rent>> getInfoRentHistoryOfTransport(@PathVariable("transportId") Long id){
        LOGGER.info("GET INFO RENT HISTORY OF USER");
        List<Rent> rents = rentService.findRentsByTransportId(id);
        return new ResponseEntity<List<Rent>>(rents, HttpStatus.OK);
    }

    @Operation(summary="завершение аренды транспорта по id аренды")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = Rent.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("/rent/end/{rentId}")
    public ResponseEntity<?> endRentById(@PathVariable("rentId") Long rentId, @RequestBody EndRentRequest endRentRequest){
        LOGGER.info("TO END RENT BY ID");
        try {
            Rent updatedRent = rentService.endRentById(rentId, endRentRequest);

            return new ResponseEntity<>(updatedRent, HttpStatus.OK);
        }
        catch (RentNotExistsException RNE){
            LOGGER.error(RNE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), RNE.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary="изменение записи, об аренде транспорта, по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = Rent.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PutMapping("/rent/{id}")
    public ResponseEntity<?> updateRentById(@PathVariable("id") Long rentId, @RequestBody CreateRentAdminRequest createRentAdminRequest){
        LOGGER.info("UPDATE RENT BY ID");
        try {
            Rent updatedRent = rentService.updateRent(rentId, createRentAdminRequest);
            return new ResponseEntity<Rent>(updatedRent, HttpStatus.OK);
        }
        catch (IllegalArgumentException IAE){
            LOGGER.error(IAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), String.format("Тип оплаты %s неверный", createRentAdminRequest.getPriceType())), HttpStatus.NOT_FOUND);
        }
        catch (AccountNotExistsException ANE){
            return new ResponseEntity<AppException>(new AppException(HttpStatus.NOT_FOUND.value(), ANE.getMessage()), HttpStatus.NOT_FOUND);
        }
        catch (RentNotExistsException RNE){
            return new ResponseEntity<AppException>(new AppException(HttpStatus.NOT_FOUND.value(), RNE.getMessage()), HttpStatus.NOT_FOUND);
        }
        catch (RuntimeException RE){
            LOGGER.error(RE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "что-то пошло не так"), HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary="удаление записи, об аренде транспорта, по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @DeleteMapping("/rent/{id}")
    public ResponseEntity<?> deleteRentById(@PathVariable("id") Long id){
        LOGGER.info("DELETE RENT BY ID");
        rentService.deleteById(id);
        return new ResponseEntity<MessageDTO>( new MessageDTO(String.format("аренда с id %s удалена успешно!", id)), HttpStatus.OK);
    }
}
