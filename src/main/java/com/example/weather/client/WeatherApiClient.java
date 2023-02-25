package com.example.weather.client;

import com.example.weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 This class is a client for retrieving weather data from an external API.
 Api url can be configured in the properties file.
 */
@Component
public class WeatherApiClient {
    @Value("${weatherUri}")
    private String weatherUri;
    private final WebClient webClient;

    public WeatherApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<WeatherData> getWeatherData(String cityName) {
        return webClient
                .get()
                .uri(weatherUri, cityName)
                .retrieve()
                .bodyToMono(WeatherData.class);
    }

}
