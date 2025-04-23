package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.UtilTest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetCommentDtoTest {
    private final JacksonTester<GetCommentDto> json;

    @Test
    void jsonTest() throws IOException {
        LocalDateTime createdTime = LocalDateTime.now();
        String time = UtilTest.dateTimeFormatter.format(createdTime);

        GetCommentDto getCommentDto = new GetCommentDto();
        getCommentDto.setId(1L);
        getCommentDto.setItemId(2L);
        getCommentDto.setAuthorId(3L);
        getCommentDto.setAuthorName("AuthorName");
        getCommentDto.setText("Text");
        getCommentDto.setCreatedTime(createdTime);

        JsonContent<GetCommentDto> result = json.write(getCommentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(getCommentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(getCommentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time);
    }
}