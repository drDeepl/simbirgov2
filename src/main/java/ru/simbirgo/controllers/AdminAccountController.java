package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.models.Account;
import ru.simbirgo.payloads.SignUpRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.interfaces.AccountI;
import ru.simbirgo.services.AccountDetailsImpl;
import ru.simbirgo.services.AccountService;

import java.security.Principal;
import java.util.List;

@Tag(name="Admin")
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
    public ResponseEntity<?> getAccounts( @RequestHeader (name="Authorization") String token){
        LOGGER.info("GET ACCOUNTS");
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accountRole = accountDetails.getAuthorities().toArray()[0].toString();
        try{
            boolean isAdmin = StringUtils.equals(accountRole, "ROLE_ADMIN");
            if(isAdmin){
                List<AccountI> accounts = accountService.getAccounts();
                return new ResponseEntity<>(accounts, HttpStatus.OK);
            }
            else{
                return  new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "недостаточно прав для доступа"), HttpStatus.FORBIDDEN);
            }

        }
        catch (Exception e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "что-то пошло не так"), HttpStatus.CONFLICT);
        }

    }

    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody SignUpRequest signUpRequest){
        LOGGER.info("CREATE ACCOUNT");

        return null;
    }

}
