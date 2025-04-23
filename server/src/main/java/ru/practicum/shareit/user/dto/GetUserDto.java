package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDto {
    private Long id;
    private String email;
    private String name;
}
