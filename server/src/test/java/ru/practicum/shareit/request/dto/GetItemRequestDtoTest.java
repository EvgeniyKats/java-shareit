package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.UtilTest;
import ru.practicum.shareit.item.model.Item;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetItemRequestDtoTest {
    private final JacksonTester<GetItemRequestDto> json;

    @Test
    void jsonTest() throws IOException {
        LocalDateTime createdTime = LocalDateTime.now();
        String time = UtilTest.dateTimeFormatter.format(createdTime);

        GetItemRequestDto getItemRequestDto = new GetItemRequestDto();
        getItemRequestDto.setId(1L);
        getItemRequestDto.setOwnerId(2L);
        getItemRequestDto.setDescription("desc");
        getItemRequestDto.setCreatedTime(createdTime);
        getItemRequestDto.setItems(new ArrayList<>());

        Item item = new Item();
        item.setName("itemName");
        getItemRequestDto.getItems().add(item);

        JsonContent<GetItemRequestDto> jsonContent = json.write(getItemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(getItemRequestDto.getDescription());
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(time);
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").isNotNull();
        assertThat(jsonContent).extractingJsonPathStringValue("$.items.[0].name").isEqualTo(item.getName());
    }
}