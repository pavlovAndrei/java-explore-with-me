package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ViewStatsClient extends BaseClient {

    public ViewStatsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);

        if (nonNull(uris)) {
            parameters.put("uris", uris);
            return get("?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            return get("?start={start}&end={end}&unique={unique}", parameters);
        }
    }
}
