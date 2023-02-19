package ru.practicum.ewm.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    public CustomPageRequest(Integer offset, Integer size, Sort sort) {
        super(offset / size, size, sort);
    }

    public static CustomPageRequest of(int from, int size) {
        return new CustomPageRequest(from, size, Sort.unsorted());
    }

    public static CustomPageRequest of(int from, int size,
                                       Sort.Direction direction, String sortField) {
        return new CustomPageRequest(from, size, Sort.by(direction, sortField));
    }
}
