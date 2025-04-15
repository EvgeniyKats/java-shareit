package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperCommentDto {
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "author.id", target = "authorId")
    GetCommentDto commentToGetDto(Comment comment);

    Comment createDtoToComment(CreateCommentDto commentDto);
}
