package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
     List<Item> findByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item i " +
            "where lower(i.name) like lower(concat('%', :text, '%')) " +
            "or lower(i.description) like lower(concat('%', :text, '%')) " +
            "and i.available = true")
    List<Item> searchItem(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestId(Long id);
}
