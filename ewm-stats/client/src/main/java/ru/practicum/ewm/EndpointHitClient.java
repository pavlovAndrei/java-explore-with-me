package ru.practicum.ewm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EndpointHitClient extends BaseClient {

    public EndpointHitClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> postHit(EndpointHitPost hitDto) {
        return post("", hitDto);
    }
}
