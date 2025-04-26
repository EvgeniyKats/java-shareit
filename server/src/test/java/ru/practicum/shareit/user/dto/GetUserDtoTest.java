package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetUserDtoTest {
    private final JacksonTester<GetUserDto> json;

    @Test
    void jsonTest() throws IOException {
        GetUserDto getUserDto = new GetUserDto();
        getUserDto.setId(1L);
        getUserDto.setName("name");
        getUserDto.setEmail("email");

        JsonContent<GetUserDto> jsonContent = json.write(getUserDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(getUserDto.getName());
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo(getUserDto.getEmail());
    }
}