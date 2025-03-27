package ru.practicum.shareit.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String message) {
        super(message);
    }
}
