package ru.simbirgo.controllers;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.dtos.ErrorMessageDTO;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.models.Account;
import ru.simbirgo.models.Rent;
import ru.simbirgo.payloads.SignUpAdminRequest;
import ru.simbirgo.payloads.UpdateAccountAdminRequest;
import ru.simbirgo.payloads.UpdateAccountRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.interfaces.AccountI;
import ru.simbirgo.services.AccountDetailsImpl;
import ru.simbirgo.services.AccountService;
import ru.simbirgo.services.RefreshTokenService;

import java.util.List;

@Tag(name="AdminAccountController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminAccountController.class);
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;


    @Autowired
    RefreshTokenService refreshTokenService;


    @Operation(summary="получение списка аккаунтов")

    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array=@ArraySchema(schema=@Schema(implementation = Account.class)))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @GetMapping("")
    public ResponseEntity<List<Account>> getAccounts(){
        LOGGER.info("GET ACCOUNTS");
        List<Account> accounts = accountService.getAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary="создание аккаунта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody SignUpAdminRequest signUpAdminRequest){
        LOGGER.info("CREATE ACCOUNT");

        try{
            if(accountRepository.existsByUsername(signUpAdminRequest.getUsername())){
                return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("пользователь с именем %s уже существует", signUpAdminRequest.getUsername())), HttpStatus.CONFLICT);
            }
            else{
                Account account = new Account(signUpAdminRequest.getUsername(), encoder.encode(signUpAdminRequest.getPassword()), signUpAdminRequest.getBalance(), signUpAdminRequest.getIsAdmin());
                accountRepository.save(account);

                return ResponseEntity.ok(new MessageDTO("Пользователь зарегистирован!"));
            }

        }
        catch (Exception e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "что-то пошло не так"), HttpStatus.CONFLICT);
        }
    }


    @Operation(summary = "обновление данных аккаунта по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @PutMapping("/{id}")
    public ResponseEntity <?> updateAccountForAdmin(@PathVariable long id, @RequestBody UpdateAccountAdminRequest updateAccountAdminRequest ){
        LOGGER.info("UPDATE");
        Account account = accountRepository.findByUsername(updateAccountAdminRequest.getUsername()).orElse(null) ;
        System.out.println(account);

        if(!accountRepository.existsById(id)){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("аккаунта с id %s не существует", id)), HttpStatus.CONFLICT);
        }

        if(account != null){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("аккаунт с именем %s уже существует", updateAccountAdminRequest.getUsername())), HttpStatus.CONFLICT);
        }

        try{
            accountService.updateAccountById(
                    id,
                    updateAccountAdminRequest.getUsername(),
                    encoder.encode(updateAccountAdminRequest.getPassword()),
                    updateAccountAdminRequest.getBalance(), updateAccountAdminRequest.getIsAdmin());

            return ResponseEntity.ok(new MessageDTO("аккаунт успешно обновлён"));
        }
        catch(AccountExistsException e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("что-то пошло не так")), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "удаление аккаунта по id")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @ApiResponse(responseCode = "401", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorMessageDTO.class))})
    @DeleteMapping("/{id}")
    public ResponseEntity <?> deleteAccount(@PathVariable long id) {
        if(!accountRepository.existsById(id)){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("аккаунта с id %s не существует", id)), HttpStatus.CONFLICT);
        }

        try{
            refreshTokenService.deleteByUserId(id);
            accountRepository.deleteById(id);
            return ResponseEntity.ok(new MessageDTO("аккаунт успешно удалён"));
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("что-то пошло не так")), HttpStatus.CONFLICT);
        }
    }

}
