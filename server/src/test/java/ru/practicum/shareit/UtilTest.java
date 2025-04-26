package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilTest {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    public static final String DESCRIPTION_BASE = "description";
    public static final String NAME_BASE = "name";
    public static final String TEXT_BASE = "text";
    public static final LocalDateTime CREATE_TIME_BASE = LocalDateTime.now();

    public static List<User> createUsers(int count) {
        List<User> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setName("name" + i);
            user.setEmail("address" + i + "@example.com");
            list.add(user);
        }

        return list;
    }


    public static List<CreateItemRequestDto> createItemRequestsDtos(int count) {
        List<CreateItemRequestDto> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            CreateItemRequestDto requestDto = new CreateItemRequestDto();
            requestDto.setDescription(DESCRIPTION_BASE + i);
            list.add(requestDto);
        }

        return list;
    }

    public static List<CreateBookingDto> createBookingDtos(int count) {
        List<CreateBookingDto> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            CreateBookingDto bookingDto = new CreateBookingDto();
            bookingDto.setStartBookingTime(CREATE_TIME_BASE.plusDays(1));
            bookingDto.setEndBookingTime(CREATE_TIME_BASE.plusDays(2));
            bookingDto.setItemId((long) i);
            list.add(bookingDto);
        }

        return list;
    }

    public static List<CreateItemDto> createItemDtos(int count) {
        List<CreateItemDto> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            CreateItemDto itemDto = new CreateItemDto();
            itemDto.setName(NAME_BASE + i);
            itemDto.setDescription(DESCRIPTION_BASE + i);
            itemDto.setAvailable(true);
            list.add(itemDto);
        }

        return list;
    }

    public static List<Item> createItems(int count) {
        List<Item> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            Item item = new Item();
            item.setName(NAME_BASE + i);
            item.setDescription(DESCRIPTION_BASE + i);
            item.setAvailable(true);
            list.add(item);
        }

        return list;
    }

    public static List<CreateCommentDto> createCommentDtos(int count) {
        List<CreateCommentDto> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            CreateCommentDto commentDto = new CreateCommentDto();
            commentDto.setText(TEXT_BASE + i);
            list.add(commentDto);
        }

        return list;
    }

    public static List<CreateUserDto> createUserDtos(int count) {
        List<CreateUserDto> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            CreateUserDto userDto = new CreateUserDto();
            userDto.setName("name" + i);
            userDto.setEmail("address" + i + "@example.com");
            list.add(userDto);
        }

        return list;
    }
}
