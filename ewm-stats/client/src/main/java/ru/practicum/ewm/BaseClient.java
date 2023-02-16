package ru.practicum.ewm;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> void post(String path, T body) {
        makeAndSendRequest(path, body);
    }

    private <T> void makeAndSendRequest(String path, @Nullable T body) {
        HttpEntity<T> requestEntity = null;
        ResponseEntity<Object> response;

        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        }

        try {
            response = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            return;
        }

        prepareGatewayResponse(response);
    }

    private static void prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            responseBuilder.body(response.getBody());
            return;
        }

        responseBuilder.build();
    }
}
