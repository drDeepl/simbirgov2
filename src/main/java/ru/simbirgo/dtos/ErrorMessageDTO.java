package ru.simbirgo.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorMessageDTO {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}
