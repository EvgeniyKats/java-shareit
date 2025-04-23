package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStartEnd;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetItemDtoTest {
    private final JacksonTester<GetItemDto> json;

    @Test
    void jsonTest() throws IOException {
        GetItemDto itemDto = new GetItemDto();
        itemDto.setId(1L);
        itemDto.setOwnerId(2L);
        itemDto.setRequestId(3L);
        itemDto.setName("name");
        itemDto.setAvailable(true);
        itemDto.setDescription("description");
        itemDto.setLastBookingTime(new BookingStartEnd() {
            @Override
            public Long getItemId() {
                return 0L;
            }

            @Override
            public LocalDateTime getStartBookingTime() {
                return LocalDateTime.now();
            }

            @Override
            public LocalDateTime getEndBookingTime() {
                return LocalDateTime.now();
            }
        });
        itemDto.setNextBookingTime(null);

        GetCommentDto commentDto = new GetCommentDto();
        commentDto.setText("textC");

        itemDto.setComments(List.of(commentDto));

        JsonContent<GetItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.startBookingTime").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.endBookingTime").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo(commentDto.getText());
    }
}