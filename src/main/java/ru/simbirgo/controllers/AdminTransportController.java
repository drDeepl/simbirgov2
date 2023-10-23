package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.payloads.FindTransportsRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.TransportRepository;
import ru.simbirgo.services.AccountService;
import ru.simbirgo.services.TransportService;

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

    @GetMapping("")
    public ResponseEntity<?> findTransports(@RequestBody FindTransportsRequest findTransportsRequest){
        LOGGER.info("FIND TRANSPORTS");
        transportService.findTransports(findTransportsRequest.getStart(), findTransportsRequest.getCount(), findTransportsRequest.getTransportType().name());
        return null;
    }

}
