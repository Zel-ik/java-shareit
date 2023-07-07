package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public CustomPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
    }

}
