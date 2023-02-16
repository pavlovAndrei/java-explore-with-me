package ru.practicum.ewm;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatsClient extends BaseClient {

    @Value("emw-service")
    private String appName;

    @Autowired
    public StatsClient(@Value("${ewm-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public void addHit(HttpServletRequest request) {
        String uri = request.getRequestURI();
        log.debug("Send uri: {} to ewm-stats-server", uri);
        post("/hit", EndpointHitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()));
    }
}
