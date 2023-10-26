package ru.simbirgo.exceptions;

public class RentNotExistsException extends RuntimeException{
    public RentNotExistsException(String message){
        super(message);
    }
}
