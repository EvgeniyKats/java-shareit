package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.UtilTest.dateTimeFormatter;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetBookingDtoTest {
    private final JacksonTester<GetBookingDto> json;

    @Test
    void jsonTest() throws IOException {
        GetBookingDto getBookingDto = new GetBookingDto();
        getBookingDto.setId(1L);

        GetUserDto userDto = new GetUserDto();
        userDto.setName("userName");

        getBookingDto.setBooker(userDto);

        GetItemDto itemDto = new GetItemDto();
        itemDto.setName("itemName");

        getBookingDto.setItem(itemDto);
        getBookingDto.setStatus(StatusBookingDto.APPROVED);
        getBookingDto.setStartBookingTime(LocalDateTime.now());
        getBookingDto.setEndBookingTime(LocalDateTime.now().plusDays(1));

        JsonContent<GetBookingDto> result = json.write(getBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(getBookingDto.getStatus().name());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dateTimeFormatter.format(getBookingDto.getStartBookingTime()));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dateTimeFormatter.format(getBookingDto.getEndBookingTime()));

    }
}