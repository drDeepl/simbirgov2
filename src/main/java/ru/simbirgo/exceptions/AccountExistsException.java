package ru.simbirgo.exceptions;

import java.io.IOException;

public class AccountExistsException extends RuntimeException {
    public AccountExistsException(String message){
        super(message);

    }
}
