package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStartEnd;
import ru.practicum.shareit.exception.custom.BadRequestException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.MapperCommentDto;
import ru.practicum.shareit.item.dto.MapperItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImplement implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final MapperItemDto mapperItemDto;
    private final MapperCommentDto mapperCommentDto;

    @Override
    public GetItemDto getItemById(Long itemId, Long userId) {
        log.trace("Попытка получить предмет с itemId = {}, от пользователя userId = {}", itemId, userId);
        getUserOrThrowNotFound(userId);
        Item item = getItemByIdOrThrowNotFound(itemId);

        if (userId.equals(item.getOwnerId())) {
            appendLastAndNextBookingForOwner(List.of(item));
        } else {
            log.trace("Запрос был совершён арендатором");
        }

        return mapperItemDto.itemToGetDto(item);
    }

    @Override
    public List<GetItemDto> getItemsByUserId(Long userId) {
        log.trace("Попытка получить предметы пользователя userId = {}", userId);
        getUserOrThrowNotFound(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);

        if (!items.isEmpty() && items.getFirst().getOwnerId().equals(userId)) {
            appendLastAndNextBookingForOwner(items);
        }

        return items.stream()
                .map(mapperItemDto::itemToGetDto)
                .toList();
    }

    @Override
    public List<GetItemDto> getItemsByText(String text, Long userId) {
        log.trace("Попытка получить предметы через поиск \"{}\" от пользователя userId = {}", text, userId);
        getUserOrThrowNotFound(userId);
        if (text.isBlank()) return List.of();
        List<Item> items = itemRepository.findByAvailableTrueAndNameContainingIgnoreCase(text);
        return items.stream()
                .map(mapperItemDto::itemToGetDto)
                .toList();
    }

    @Override
    @Transactional
    public GetItemDto createItem(CreateItemDto createItemDto, Long userId) {
        log.trace("Попытка создать предмет от пользователя userId = {}", userId);
        getUserOrThrowNotFound(userId);
        Item item = mapperItemDto.createDtoToItem(createItemDto);
        item.setOwnerId(userId);
        itemRepository.save(item);
        log.trace("Предмет успешно создан пользователем userId = {}, его itemId = {}", userId, item.getId());
        return mapperItemDto.itemToGetDto(item);
    }

    @Override
    @Transactional
    public GetItemDto updateItem(UpdateItemDto updateItemDto, Long itemId, Long userId) {
        log.trace("Попытка создать предмет itemId = {}, от пользователя userId = {}", itemId, userId);
        getUserOrThrowNotFound(userId);
        Item updatedItem = mapperItemDto.updateDtoToItem(updateItemDto);

        Item currentItem = getItemByIdOrThrowNotFound(itemId);
        throwBadRequestIfUserNotOwnerOfItem(currentItem, userId);

        currentItem.updateFromAnotherItem(updatedItem);
        log.trace("Предмет с itemId = {}, успешно обновлен", itemId);
        return mapperItemDto.itemToGetDto(currentItem);
    }

    @Override
    @Transactional
    public void deleteItemById(Long itemId, Long userId) {
        log.trace("Попытка удалить предмет itemId = {}, от пользователя userId = {}", itemId, userId);
        getUserOrThrowNotFound(userId);
        Item currentItem = getItemByIdOrThrowNotFound(itemId);
        throwBadRequestIfUserNotOwnerOfItem(currentItem, userId);
        itemRepository.deleteById(itemId);
        log.trace("Успешно удален предмет с itemId = {}", itemId);
    }

    @Override
    @Transactional
    public GetCommentDto commentItem(CreateCommentDto createCommentDto, Long userId, Long itemId) {
        log.trace("Попытка прокомментировать предмет {}, пользователем {}, текст {}",
                itemId, userId, createCommentDto.getText());
        getUserOrThrowNotFound(userId);

        Sort sortAsc = Sort.by(Sort.Direction.ASC, "startBookingTime");
        Optional<Booking> booking = bookingRepository.findByBookerIdAndItemId(userId, itemId, sortAsc);

        if (booking.isEmpty() || booking.get().getEndBookingTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Оставить отзыв можно только после окончания аренды");
        }

        Comment comment = mapperCommentDto.createDtoToComment(createCommentDto);
        User author = getUserOrThrowNotFound(userId);
        getItemByIdOrThrowNotFound(itemId);
        comment.setItemId(itemId);
        comment.setAuthor(author);
        commentRepository.save(comment);
        log.trace("Комментарий успешно сохранён предмет {}, пользователь {}, текст {}",
                itemId, userId, createCommentDto.getText());
        return mapperCommentDto.commentToGetDto(comment);
    }

    private void appendLastAndNextBookingForOwner(List<Item> items) {
        log.trace("Запрос был совершён владельцем");
        Long userId = items.getFirst().getOwnerId();
        for (Item item : items) {
            log.trace("Заполняем ближайший букинг");
            Optional<BookingStartEnd> nextBookingTime = bookingRepository.findNextBookingTime(userId, item.getId());
            if (nextBookingTime.isPresent()) {
                log.trace("Ближайший букинг найден");
                item.setNextBookingTime(nextBookingTime.get());
            } else {
                log.trace("Ближайший букинг не обнаружен");
            }

            log.trace("Заполняем последний букинг");
            Optional<BookingStartEnd> lastBookingTime = bookingRepository.findLastBookingTime(userId, item.getId());
            if (lastBookingTime.isPresent()) {
                log.trace("Последний букинг найден");
                item.setLastBookingTime(lastBookingTime.get());
            } else {
                log.trace("Последний букинг не обнаружен");
            }
        }
    }

    private Item getItemByIdOrThrowNotFound(Long itemId) {
        log.trace("Проверка предмета с itemId = {} в хранилище", itemId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Предмет")
                    .setNameParameter("itemId")
                    .setValueParameter(itemId)
                    .build();
        }
        log.trace("Предмет с itemId = {}, найден", itemId);
        return optionalItem.get();
    }

    private User getUserOrThrowNotFound(Long userId) {
        log.trace("Проверка на существования пользователя с userId = {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Пользователь")
                    .setNameParameter("userId")
                    .setValueParameter(userId)
                    .build();
        }
        log.trace("Пользователь с userId = {}, найден", userId);
        return user.get();
    }

    private void throwBadRequestIfUserNotOwnerOfItem(Item item, Long userId) {
        log.trace("Проверка на соответствие владельца предмета itemId = {} и пользователя userId = {}",
                item.getId(),
                userId);
        if (!item.getOwnerId().equals(userId)) {
            throw new BadRequestException("Пользователь с userId = " + userId
                    + " не владеет предметом itemId = " + item.getId());
        }
        log.trace("Владение предметом itemId = {} подтверждено", item.getId());
    }
}
