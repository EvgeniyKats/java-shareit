package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.MapperItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final MapperItemRequestDto mapperItemRequestDto;

    @Override
    public List<GetItemRequestDto> getUserItemRequests(Long userId, Integer from, Integer size) {
        log.trace("Получение запросов для пользователя userId = {}, from = {}, size = {}", userId, from, size);
        throwNotFoundIfUserAbsent(userId);
        Pageable page = PageRequest.of(from, size);
        List<ItemRequest> requests = itemRequestRepository.findByOwnerIdOrderByCreatedTimeDesc(userId, page);
        log.trace("Найдено {} запросов пользователя {}", requests.size(), userId);
        return requests.stream()
                .map(mapperItemRequestDto::mapModelToGetDto)
                .toList();
    }

    @Override
    public List<GetItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        log.trace("Получение всех запросов, для пользователя = {}, from = {}, size = {}", userId, from, size);
        throwNotFoundIfUserAbsent(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable page = PageRequest.of(from, size, sort);
        List<ItemRequest> requests = itemRequestRepository.findAll(page).getContent();
        log.trace("Найдено {} запросов", requests.size());
        return requests.stream()
                .map(mapperItemRequestDto::mapModelToGetDto)
                .toList();
    }

    @Override
    public GetItemRequestDto getItemRequestById(Long userId, Long requestId) {
        log.trace("Получение запроса от пользователя userId = {} , requestId = {}", userId, requestId);
        throwNotFoundIfUserAbsent(userId);
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);

        if (request.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("ItemRequest")
                    .setNameParameter("userId, requestId")
                    .setValueParameter(userId + ", " + requestId)
                    .build();
        }

        log.trace("ItemRequest найден для пользователя userId = {} , requestId = {}", userId, requestId);
        return mapperItemRequestDto.mapModelToGetDto(request.get());
    }

    @Transactional
    @Override
    public GetItemRequestDto createItemRequest(Long userId, CreateItemRequestDto createItemRequestDto) {
        log.trace("Создание запроса от пользователя userId = {} , createItemRequestDto = {}",
                userId, createItemRequestDto.toString());
        throwNotFoundIfUserAbsent(userId);

        ItemRequest request = mapperItemRequestDto.mapCreateToModel(createItemRequestDto);
        request.setOwnerId(userId);
        itemRequestRepository.save(request);
        log.trace("Запрос успешно сохранён, {}", request);
        return mapperItemRequestDto.mapModelToGetDto(request);
    }

    private void throwNotFoundIfUserAbsent(Long userId) {
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
    }
}
