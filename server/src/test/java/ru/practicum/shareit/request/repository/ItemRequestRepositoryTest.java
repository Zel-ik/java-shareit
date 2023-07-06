package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void testFindAllByRequesterIdOrderByCreatedDesc() {
        User user = new User(null, "test@example.com", "Test User");
        entityManager.persistAndFlush(user);

        ItemRequest request1 = new ItemRequest(null, "Test request 1", LocalDateTime.now(),
                user, null);
        ItemRequest request2 = new ItemRequest(null, "Test request 2", LocalDateTime.now().minusDays(1),
                user, null);
        entityManager.persistAndFlush(request1);
        entityManager.persistAndFlush(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId());

        assertEquals(2, requests.size());
        assertEquals(request1.getId(), requests.get(0).getId());
        assertEquals(request2.getId(), requests.get(1).getId());
    }

    @Test
    void testFindAllByRequesterIdNot() {
        User user1 = new User(null, "test1@example.com", "Test User 1");
        entityManager.persistAndFlush(user1);
        User user2 = new User(null, "test2@example.com", "Test User 2");
        entityManager.persistAndFlush(user2);

        ItemRequest request1 = new ItemRequest(null, "Test request 1", LocalDateTime.now(),
                user1, null);
        ItemRequest request2 = new ItemRequest(null, "Test request 2", LocalDateTime.now().minusDays(1),
                user1, null);
        ItemRequest request3 = new ItemRequest(null, "Test request 3", LocalDateTime.now().plusDays(1),
                user2, null);
        entityManager.persistAndFlush(request1);
        entityManager.persistAndFlush(request2);
        entityManager.persistAndFlush(request3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(user1.getId(), pageable);

        assertEquals(1, requests.getTotalElements());
        assertEquals(request3.getId(), requests.getContent().get(0).getId());
    }
}