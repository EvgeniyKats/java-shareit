package ru.practicum.shareit.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateException extends RuntimeException {
    public DuplicateException(String msg) {
        super(msg);
    }
}
