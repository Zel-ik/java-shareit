package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class ItemRequestServiceImplDBIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void testGetItemRequests() {
        User user = new User();
        user.setEmail("Us@mail.ru");
        user.setName("Us");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest(1L, "Test", LocalDateTime.now(),
                userRepository.findByEmail("Us@mail.ru").get(), new ArrayList<>());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequests(savedUser.getId());

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.get(0).getId()).isEqualTo(savedItemRequest.getId());
        assertThat(itemRequests.get(0).getDescription()).isEqualTo(savedItemRequest.getDescription());
        assertThat(itemRequests.get(0).getItems()).isEmpty();
    }
}
