package ru.simbirgo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.services.AccountDetailsImpl;
import ru.simbirgo.services.AccountService;

@Tag(name="PaymentController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(AdminAccountController.class);
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/hesoyam/{accountId}")
    public ResponseEntity<?> addedBalanceToAccount(@PathVariable long accountId){
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = jwtUtils.getIsAdminFromAccountDetails(accountDetails);
        if(isAdmin){
            accountService.hesoyamBalance(accountId); // gta sa edition mode
        }
        else if(accountDetails.getId() == accountId){

                accountService.hesoyamBalance(accountId);
        }
        else{
            return new ResponseEntity<>(new AppException(HttpStatus.FORBIDDEN.value(), "недостаточно прав  для операции"), HttpStatus.FORBIDDEN);

        }

        return  ResponseEntity.ok(new MessageDTO("Баланс пополнен!"));

    }
}
