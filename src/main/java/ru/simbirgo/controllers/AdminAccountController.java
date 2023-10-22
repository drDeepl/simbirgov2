package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.models.Account;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.interfaces.AccountI;
import ru.simbirgo.services.AccountService;

import java.util.List;

@Tag(name="Account")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminAccountController.class);
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AccountService accountService;

    @GetMapping("")
    public ResponseEntity<?> getAccounts(){
        LOGGER.info("GET ACCOUNTS");
        try{
            List<AccountI> accounts = accountService.getAccounts();
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "что-то пошло не так"), HttpStatus.CONFLICT);
        }


    }

}
