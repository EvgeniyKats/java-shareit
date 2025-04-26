package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId,
                                              BookingController.StateParam state,
                                              boolean isOwner,
                                              Integer from,
                                              Integer size) {
        String path;
        if (isOwner) {
            path = "/owner?state={state}&from={from}&size={size}";
        } else {
            path = "?state={state}&from={from}&size={size}";
        }

        Map<String, Object> parameters = Map.of("state", state.name(),
                "from", from,
                "size", size);

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        String path = "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> createBooking(long userId, CreateBookingDto requestDto) {
        String path = "";
        return post(path, userId, requestDto);
    }

    public ResponseEntity<Object> makeDecisionForBooking(long bookingId, long userId, boolean approved) {
        String path = "/" + bookingId + "?approved={approved}";
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(path, userId, parameters);
    }
}
