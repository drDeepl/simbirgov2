package ru.simbirgo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.simbirgo.controllers.AdminAccountController;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.models.Account;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.interfaces.AccountI;

import java.io.IOException;
import java.util.List;

@Service
public class AccountService {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminAccountController.class);

    @Autowired
    private AccountRepository accountRepository;



    public Account updateAccount(String currentUsername, String updateUsername, String password) throws AccountExistsException{
        if(accountRepository.existsByUsername(updateUsername)){
            new AccountExistsException("аккаунт с таким именем пользователя уже существует");
        }
        Account account = accountRepository.findByUsername(currentUsername).get();
        account.setUsername(updateUsername);
        account.setPassword(password);
        return accountRepository.save(account);
    }

    public List<AccountI> getAccounts(){
        LOGGER.info("GET ACCOUNTS");
        List<AccountI> accounts = accountRepository.findAccounts();
        LOGGER.info("GETTING ACCOUNTS");

        return accounts;
    }
}
