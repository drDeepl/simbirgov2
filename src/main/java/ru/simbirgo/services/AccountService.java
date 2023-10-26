package ru.simbirgo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.simbirgo.controllers.AdminAccountController;
import ru.simbirgo.dtos.AccountDTO;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.exceptions.AccountNotExistsException;
import ru.simbirgo.models.Account;
import ru.simbirgo.repositories.AccountRepository;
import ru.simbirgo.repositories.interfaces.AccountI;

import java.io.IOException;
import java.util.List;

@Service
public class AccountService {

    private final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

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

    public Account getAccountById(Long id){
        LOGGER.info("GET ACCOUNT BY ID");
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotExistsException(String.format("аккаунта с id %s не существует", id)));
    }

    public List<AccountI> getAccounts(){
        LOGGER.info("GET ACCOUNTS");
        List<AccountI> accounts = accountRepository.findAccounts();
        LOGGER.info("GETTING ACCOUNTS");

        return accounts;
    }

    public void updateAccountById(long accountId, String username, String password, Double balance, boolean isAdmin){
        Account account = accountRepository.findById(accountId).get();
        account.setUsername(username);
        account.setPassword(password);
        account.setBalance(balance);
        account.setIsAdmin(isAdmin);
        accountRepository.save(account);

    }

    public void hesoyamBalance(long id){
        Account account = accountRepository.findById(id).get();
        double currentBalance = account.getBalance();
        account.setBalance(currentBalance + 250000);
        accountRepository.save(account);
    }
}
