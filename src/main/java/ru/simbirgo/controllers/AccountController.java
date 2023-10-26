package ru.simbirgo.controllers;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import ru.simbirgo.config.jwt.JwtUtils;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.dtos.JwtDTO;
import ru.simbirgo.dtos.MessageDTO;
import ru.simbirgo.dtos.TokenRefreshDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.exceptions.AppException;
import ru.simbirgo.exceptions.TokenRefreshException;
import ru.simbirgo.models.Account;
import ru.simbirgo.models.RefreshToken;
import ru.simbirgo.models.Rent;
import ru.simbirgo.payloads.SignInRequest;
import ru.simbirgo.payloads.SignUpRequest;
import ru.simbirgo.payloads.TokenRefreshRequest;
import ru.simbirgo.payloads.UpdateAccountRequest;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.services.AccountDetailsImpl;
import ru.simbirgo.services.AccountService;
import ru.simbirgo.services.RefreshTokenService;

@Tag(name="AccountController")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AccountService accountService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Operation(summary="вход в аккаунт")
    @PostMapping("/signin")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = JwtDTO.class))})
    public ResponseEntity<?> authenticateUser(@RequestBody SignInRequest signInRequest) {
        LOGGER.info("SIGNIN");
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(accountDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(accountDetails.getId());
        return ResponseEntity.ok(new JwtDTO(jwt, refreshToken.getToken()));
    }

    @Operation(summary="регистрация аккаунта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (accountRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageDTO("Пользователь с таким именем уже существует"));
        }


        Account account = new Account(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageDTO("Пользователь зарегистирован!"));
    }

    @Operation(summary="получение refresh token")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = TokenRefreshDTO.class))})
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getAccount)
                .map(account -> {
                    String token = jwtUtils.generateTokenFromUsername(account.getUsername(), account.getIsAdmin(), account.getId());
                    return ResponseEntity.ok(new TokenRefreshDTO(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token не найден"));
    }

    @Operation(summary="выход из аккаунта")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = MessageDTO.class))})
    @PostMapping("/signout")
    public ResponseEntity<?> logoutAccount() {
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long accountId = accountDetails.getId();
        refreshTokenService.deleteByUserId(accountId);
        return ResponseEntity.ok(new MessageDTO("Выход прошел успешно!"));
    }

    @Operation(summary = "получение данных о текущем аккаунте")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = AccountDTO.class))})
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAccountInfo(){
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = accountDetails.getUsername();
        Account account = accountRepository.findByUsername(username).get();
        if(account != null){
            AccountDTO accountDTO = new AccountDTO(account.getId(), account.getUsername(),account.getIsAdmin(), account.getBalance());
            return ResponseEntity.ok(accountDTO);
        }

        return new ResponseEntity<>(new AppException(HttpStatus.NOT_FOUND.value(), "данные не найдены"), HttpStatus.NOT_FOUND);
    }

    @Operation(summary="обновление данных об аккаунте")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema=@Schema(implementation = AppException.class))})
    @PutMapping("/update")
    public ResponseEntity<AppException> updateAccount(@RequestBody UpdateAccountRequest updateAccountRequest){
        LOGGER.info("UPDATE");
        AccountDetailsImpl accountDetails = (AccountDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        accountDetails.getUsername();

        try{
            Account account = accountService.updateAccount(accountDetails.getUsername(), updateAccountRequest.getUsername(), encoder.encode(updateAccountRequest.getPassword()));
            refreshTokenService.deleteByUserId(account.getId());
            LOGGER.info("REFRESH DELETED");
            refreshTokenService.createRefreshToken(account.getId());
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), "аккаунт успешно изменен"), HttpStatus.OK);
        }
        catch(AccountExistsException e){
            return new ResponseEntity<>(new AppException(HttpStatus.CONFLICT.value(), String.format("аккаунт с именем пользователя %s уже существует")), HttpStatus.CONFLICT);

        }

    }



}