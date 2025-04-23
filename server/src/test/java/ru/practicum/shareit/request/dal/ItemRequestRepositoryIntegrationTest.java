package ru.practicum.shareit.request.dal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UtilTest.CREATE_TIME_BASE;
import static ru.practicum.shareit.UtilTest.DESCRIPTION_BASE;

@DataJpaTest
class ItemRequestRepositoryIntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void saveRequest() {
        ItemRequest request = createItemRequests(1).getFirst();

        itemRequestRepository.save(request);
        assertNotNull(request.getId());
        assertEquals(1L, request.getOwnerId());
        assertEquals(CREATE_TIME_BASE, request.getCreatedTime());
        assertEquals(DESCRIPTION_BASE + "1", request.getDescription());
    }

    @Test
    void shouldBeTwoRequests() {
        List<ItemRequest> requests = createItemRequests(2);

        long targetOwner = 1L;

        requests.forEach(itemRequest -> {
            itemRequest.setOwnerId(targetOwner);
            itemRequestRepository.save(itemRequest);
        });

        List<ItemRequest> fromRep = itemRequestRepository.findByOwnerIdOrderByCreatedTimeDesc(targetOwner);
        assertEquals(2, fromRep.size());
    }

    private List<ItemRequest> createItemRequests(int count) {
        List<ItemRequest> list = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            ItemRequest request = new ItemRequest();
            request.setCreatedTime(CREATE_TIME_BASE);
            request.setDescription(DESCRIPTION_BASE + i);
            request.setOwnerId((long) i);
            list.add(request);
        }

        return list;
    }
}