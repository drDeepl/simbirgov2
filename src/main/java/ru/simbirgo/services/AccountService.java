package ru.simbirgo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.simbirgo.exceptions.AccountExistsException;
import ru.simbirgo.models.Account;
import ru.simbirgo.repositories.AccountRepository;

import java.io.IOException;

@Service
public class AccountService {
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
}
