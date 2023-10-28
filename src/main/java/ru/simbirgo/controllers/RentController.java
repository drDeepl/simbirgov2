package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.ErrorMessageDTO;
import ru.simbirgo.dtos.TransportDTO;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.exceptions.RentNotExistsException;
import ru.simbirgo.models.EPriceType;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Rent;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.EndRentRequest;
import ru.simbirgo.payloads.NewRentRequest;
import ru.simbirgo.payloads.RentTransportsParamsRequest;
import ru.simbirgo.repositories.RentRepository;
import ru.simbirgo.services.RentService;
import ru.simbirgo.services.TransportService;

@Tag(name="RentController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rent")
public class RentController {

    private final Logger LOGGER = LoggerFactory.getLogger(RentController.class);

    @Autowired
    RentRepository rentRepository;

    @Autowired
    RentService rentService;

    @Autowired
    TransportService transportService;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "получение транспорта доступного для аренды по параметрам")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = TransportDTO.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/transport")
    public ResponseEntity<?> getTransportByParams(@RequestBody RentTransportsParamsRequest rentTransportsParamsRequest){
        LOGGER.info("GET TRANSPORT BY PARAMS");
        // radius указывается в КМ
        try{

        if(rentTransportsParamsRequest.getType().toUpperCase().equals("ALL")){
            return new ResponseEntity<>(transportService.findTransportByParams(rentTransportsParamsRequest.getLat(), rentTransportsParamsRequest.getLng(), rentTransportsParamsRequest.getRadius()), HttpStatus.OK);
        }
        else{
            String transportType = ETransportType.valueOf(rentTransportsParamsRequest.getType().toUpperCase()).name();
            return new ResponseEntity<>(transportService.findTransportByParamsFilterType(rentTransportsParamsRequest.getLat(), rentTransportsParamsRequest.getLng(), rentTransportsParamsRequest.getRadius(), transportType), HttpStatus.OK);
        }

        }
        catch(IllegalArgumentException IAE){
            return new ResponseEntity<AppException>(new AppException(HttpStatus.FORBIDDEN.value(), "не корректный тип транспорта"), HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary="получение информации об аренде по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TransportDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransportById(@PathVariable("id") Long rentId, HttpServletRequest httpServletRequest){
        LOGGER.info("GET TRANSPORT BY ID");
        try{
        Rent rent = rentService.findById(rentId);
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
        if(rent.getAccount().getId().equals(currentAccountId) || rent.getTransport().getOwnerId().getId().equals(currentAccountId)){
            return new ResponseEntity<Rent>(rent, HttpStatus.OK);
        }
        return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "информацию об аренде может просматрировать арендатор или владелец"), HttpStatus.FORBIDDEN);
    }catch(RentNotExistsException RNE){
            return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), String.format("аренда с id %s не найдена", rentId)), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary="получение истории аренд текущего аккаунта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema( schema=@Schema(implementation = Rent.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/my_history")
    public ResponseEntity<?>getRentsHistoryCurrentAccount(HttpServletRequest httpServletRequest){
        LOGGER.info("GET RENTS HISTORY CURRENT ACCOUNT");
        try{
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
            return new ResponseEntity<>(rentService.finByAccountIdAll(currentAccountId), HttpStatus.OK);
        }
        catch (RuntimeException e){
            LOGGER.info(e.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), "что-то пошло не так"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary="получение истории аренд транспорта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema( schema=@Schema(implementation = Rent.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("/transport_history/{transportId}")
    public ResponseEntity<?> getRentHistoryByTransport(@PathVariable("transportId") Long transportId, HttpServletRequest httpServletRequest){
        LOGGER.info("GET RENT HISTORY BY TRANSPORT");
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
            Transport transport = transportService.findTransportByOwnerIdAndTransportId(currentAccountId, transportId);
            if (transport == null) {
                return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "только владелец транспорта может просматривать историю аренды"), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(rentService.findRentsByTransportId(transportId), HttpStatus.OK);
        }
        catch(RuntimeException e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "что-то пошло не так"), HttpStatus.FORBIDDEN);

        }
    }

    @Operation(summary="аренда транспортного средства в личное пользование")
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = AppException.class))})
    @PostMapping("/new/{transportId}")
    public ResponseEntity<?> getTransportInRent(@PathVariable("transportId") Long transportId, @RequestBody NewRentRequest newRentDTO, HttpServletRequest httpServletRequest){
        LOGGER.info("GET TRANSPORT IN RENT");
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
            Transport transportForRent = transportService.findById(transportId);
            if(!transportForRent.getCanBeRented()){
                return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "данный транспорт уже взят в аренду"), HttpStatus.FORBIDDEN);

            }
            if (transportForRent.getOwnerId().getId().equals(currentAccountId)) {
                return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "нельзя брать в аренду собственный транспорт"), HttpStatus.FORBIDDEN);
            }

            EPriceType rentType = EPriceType.valueOf(newRentDTO.getRentType().toUpperCase());
            rentService.newRent(currentAccountId, transportForRent, newRentDTO);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        catch(IllegalArgumentException EAE){
            LOGGER.info(EAE.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "не действительный тип аренды"), HttpStatus.CONFLICT);
        }
        catch(RuntimeException E){
            LOGGER.info(E.getMessage());
            return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "что-то пошло не так"), HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary="завершение аренды транспорта по id аренды")
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("/end/{rentId}")
    public ResponseEntity<AppException> endRentById(@PathVariable("rentId") Long rentId, @RequestBody EndRentRequest endRentRequest, HttpServletRequest httpServletRequest){
        LOGGER.info("END RENT BY RENT ID");
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        Long currentAccountId = jwtUtils.getAccountIdFromJWT(token);
        Rent currentRent = rentService.findById(rentId);
        if(currentRent.getAccount().getId().equals(currentAccountId)) {
            rentService.endRentById(rentId, endRentRequest);
            return new ResponseEntity<AppException>(new AppException(HttpStatus.OK.value(), "аренда транспорта завершена"), HttpStatus.OK);
        }
        return new ResponseEntity<AppException>(new AppException(HttpStatus.FORBIDDEN.value(), "завершить аренду может только арендатор"), HttpStatus.FORBIDDEN);
    }

}
