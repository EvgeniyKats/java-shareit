package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }


    public HttpEntity<Object> getItemById(long itemId, long userId) {
        String path = "/" + itemId;
        return get(path, userId);
    }

    public HttpEntity<Object> getItemsByUserId(long userId, int from, int size) {
        String path = "?from={from}&size={size}";

        Map<String, Object> parameters = Map.of("from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public HttpEntity<Object> getItemsByText(String text, long userId, int from, int size) {
        String path = "/search?text={text}&from={from}&size={size}";

        Map<String, Object> parameters = Map.of("text", text,
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public HttpEntity<Object> createItem(CreateItemDto createItemDto, long userId) {
        String path = "";
        return post(path, userId, createItemDto);
    }

    public HttpEntity<Object> commentItem(CreateCommentDto createCommentDto, long userId, long itemId) {
        String path = "/" + itemId + "/comment";
        return post(path, userId, createCommentDto);
    }

    public HttpEntity<Object> updateItem(UpdateItemDto updateItemDto, long itemId, long userId) {
        String path = "/" + itemId;
        return patch(path, userId, updateItemDto);
    }

    public void deleteItemById(long itemId, long userId) {
        String path = "/" + itemId;
        delete(path, userId);
    }
}
