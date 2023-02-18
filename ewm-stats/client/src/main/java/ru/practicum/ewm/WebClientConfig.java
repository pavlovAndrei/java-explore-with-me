package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

    private static final String HIT_API_PREFIX = "/hit";
    private static final String STATS_API_PREFIX = "/stats";

    @Value("${stats-server.url}")
    private String serviceUrl;

    @Bean
    public EndpointHitClient endpointHitClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = getBaseRestTemplate(builder, HIT_API_PREFIX);
        return new EndpointHitClient(restTemplate);
    }

    @Bean
    public ViewStatsClient viewStatsClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = getBaseRestTemplate(builder, STATS_API_PREFIX);
        return new ViewStatsClient(restTemplate);
    }

    private RestTemplate getBaseRestTemplate(RestTemplateBuilder builder, String apiPrefix) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + apiPrefix))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }
}
