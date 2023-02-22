package ru.practicum.ewm.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsCategoryByName(String name);
}
