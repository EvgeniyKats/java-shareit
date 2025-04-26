package ru.practicum.shareit.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public static <T> NotFoundExceptionBuilder<T> builder() {
        return new NotFoundExceptionBuilder<>();
    }

    private <T> NotFoundException(String nameObject, String nameParameter, T valueParameter) {
        super(nameObject + " с " + nameParameter + " = " + valueParameter + " не найден");
    }

    public static class NotFoundExceptionBuilder<T> {
        private String nameObject;
        private String nameParameter;
        private T valueParameter;

        public NotFoundExceptionBuilder<T> setNameObject(String nameObject) {
            this.nameObject = nameObject;
            return this;
        }

        public NotFoundExceptionBuilder<T> setNameParameter(String nameParameter) {
            this.nameParameter = nameParameter;
            return this;
        }

        public NotFoundExceptionBuilder<T> setValueParameter(T valueParameter) {
            this.valueParameter = valueParameter;
            return this;
        }

        public NotFoundException build() {
            return new NotFoundException(nameObject, nameParameter, valueParameter);
        }
    }
}
