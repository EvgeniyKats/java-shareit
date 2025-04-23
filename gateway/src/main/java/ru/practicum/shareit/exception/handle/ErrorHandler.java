package ru.practicum.shareit.exception.handle;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;

import ru.practicum.shareit.exception.custom.IncorrectParameterException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class})
public class ErrorHandler {

    @ExceptionHandler(IncorrectParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final IncorrectParameterException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final ConstraintViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Проверьте запрос");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }
}

