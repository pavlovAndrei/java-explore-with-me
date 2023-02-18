package ru.practicum.ewm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.ewm.model.App;

public interface AppsRepository extends JpaRepository<App, Long> {

    Optional<App> findByApp(String name);
}
