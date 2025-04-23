package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public HttpEntity<Object> getUserItemRequests(long userId) {
        String path = "";
        return get(path, userId);
    }

    public HttpEntity<Object> getAllItemRequests(long userId) {
        String path = "/all";
        return get(path, userId);
    }

    public HttpEntity<Object> getItemRequestById(long userId, long requestId) {
        String path = "/" + requestId;
        return get(path, userId);
    }

    public HttpEntity<Object> createItemRequest(long userId, CreateItemRequestDto createItemRequestDto) {
        String path = "";
        return post(path, userId, createItemRequestDto);
    }
}
