package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequester_Id(Long userId);

    Page<ItemRequest> findAllByRequester_IdNot(Long userId, Pageable pageable);

    Collection<ItemRequest> findAllByRequester_IdNot(Long userId);
}