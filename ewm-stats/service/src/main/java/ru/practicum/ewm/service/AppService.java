package ru.practicum.ewm.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.repository.AppsRepository;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppsRepository appRepository;

    public App findByApp(String nameApp) {
        Optional<App> app = appRepository.findByApp(nameApp);
        return app.orElseGet(() -> appRepository.save(new App(nameApp)));
    }
}
