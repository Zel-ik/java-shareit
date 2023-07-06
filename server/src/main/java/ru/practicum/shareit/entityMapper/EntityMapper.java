package ru.practicum.shareit.entityMapper;

public interface EntityMapper<T, E> {
    T mapTo(E entity);

    E mapFrom(T entity);
}
