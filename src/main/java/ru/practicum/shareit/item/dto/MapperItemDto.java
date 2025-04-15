package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperItemDto {

    Item createDtoToItem(CreateItemDto createItemDto);

    Item updateDtoToItem(UpdateItemDto updateItemDto);

    GetItemDto itemToGetDto(Item item);

    default GetCommentDto mapCommentToDto(Comment comment) {
        GetCommentDto getCommentDto = new GetCommentDto();

        if (comment.getId() != null) {
            getCommentDto.setId(comment.getId());
        }

        if (comment.getItem() != null && comment.getItem().getId() != null) {
            getCommentDto.setItemId(comment.getItem().getId());
        }

        if (comment.getText() != null) {
            getCommentDto.setText(comment.getText());
        }

        if (comment.getCreatedTime() != null) {
            getCommentDto.setCreatedTime(comment.getCreatedTime());
        }

        if (comment.getAuthor() != null) {
            if (comment.getAuthor().getId() != null) {
                getCommentDto.setAuthorId(comment.getAuthor().getId());
            }
            if (comment.getAuthor().getName() != null) {
                getCommentDto.setAuthorName(comment.getAuthor().getName());

            }
        }

        return getCommentDto;
    }
}
