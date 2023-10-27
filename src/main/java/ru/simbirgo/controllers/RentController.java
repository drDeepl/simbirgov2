package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.models.ETransportType;
import ru.simbirgo.models.Rent;
import ru.simbirgo.models.Transport;
import ru.simbirgo.payloads.RentTransportsParamsRequest;
import ru.simbirgo.repositories.RentRepository;
import ru.simbirgo.services.RentService;
import ru.simbirgo.services.TransportService;

import java.util.List;

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
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = Transport.class)))})
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

}
