package ru.simbirgo.exceptions;

public class TransportNotExistsException extends RuntimeException{
    public TransportNotExistsException(String message){
        super(message);
    }

}
