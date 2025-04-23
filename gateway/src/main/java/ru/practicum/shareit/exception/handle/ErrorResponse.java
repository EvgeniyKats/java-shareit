package ru.practicum.shareit.exception.handle;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(@JsonProperty("error") String message) {
}
