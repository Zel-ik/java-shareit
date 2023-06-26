package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select * from items where name ilike concat('%', ?1, '%') or description ilike concat('%', ?1, '%')",
            nativeQuery = true)
    List<Item> findItem(String request);

    List<Item> findAllByOwnerIdOrderByIdAsc(long userId);
}