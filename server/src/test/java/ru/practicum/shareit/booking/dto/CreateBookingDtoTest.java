package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UtilTest.dateTimeFormatter;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateBookingDtoTest {
    private final ObjectMapper mapper;

    @Test
    void jsonTest() throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();

        final long id = 1;

        final LocalDateTime startTime = LocalDateTime.now();
        final String start = dateTimeFormatter.format(startTime);

        final LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        final String end = dateTimeFormatter.format(endTime);

        final StatusBookingDto failedStatus = StatusBookingDto.APPROVED;
        final StatusBookingDto successStatus = StatusBookingDto.WAITING;

        rootNode.put("itemId", id);
        rootNode.put("start", start);
        rootNode.put("end", end);
        rootNode.put("status", failedStatus.name());

        final String inputJson = mapper.writeValueAsString(rootNode);

        CreateBookingDto dto = mapper.readValue(inputJson, CreateBookingDto.class);

        assertEquals(id, dto.getItemId());
        assertEquals(startTime, dto.getStartBookingTime());
        assertEquals(endTime, dto.getEndBookingTime());
        assertNotEquals(failedStatus, dto.getStatus());
        assertEquals(successStatus, dto.getStatus());
        dto.setItemId(2L);
        assertEquals(2, dto.getItemId());
    }
}