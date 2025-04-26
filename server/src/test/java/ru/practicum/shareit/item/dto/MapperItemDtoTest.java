package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.UtilTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MapperItemDtoTest {
    private final MapperItemDto mapperItemDto;

    @Test
    void mapCommentToGetCommentFull() {
        LocalDateTime created = UtilTest.CREATE_TIME_BASE;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");

        User user = new User();
        user.setId(2L);
        user.setEmail("em");
        user.setName("na");

        comment.setAuthor(user);
        comment.setCreatedTime(created);
        comment.setItemId(3L);

        GetCommentDto dto = mapperItemDto.mapCommentToDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getItemId(), dto.getItemId());
        assertEquals(comment.getCreatedTime(), dto.getCreatedTime());

        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getAuthor().getId(), dto.getAuthorId());
    }

    @Test
    void mapCommentToGetCommentEmpty() {
        Comment comment = new Comment();
        User user = new User();

        GetCommentDto dto = mapperItemDto.mapCommentToDto(comment);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getItemId(), dto.getItemId());
        assertEquals(comment.getCreatedTime(), dto.getCreatedTime());

        comment.setAuthor(user);
        dto = mapperItemDto.mapCommentToDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getItemId(), dto.getItemId());
        assertEquals(comment.getCreatedTime(), dto.getCreatedTime());

        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getAuthor().getId(), dto.getAuthorId());
    }
}