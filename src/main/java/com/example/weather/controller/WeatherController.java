package com.example.weather.controller;

import com.example.weather.model.City;
import com.example.weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public Mono<ResponseEntity<Map<String, List<City>>>> getWeather(@RequestParam String city) {
        List<String> cityList = Arrays.asList(city.split(","));
        Flux<City> cityFlux = weatherService.getCityFlux(cityList);
        return createResponse(cityFlux);
    }

    private Mono<ResponseEntity<Map<String, List<City>>>> createResponse(Flux<City> cityFlux) {
        return cityFlux.collectList()
                .publishOn(Schedulers.boundedElastic())
                .map(cities -> {
                    weatherService.writeCsvFile(cities);
                    return ResponseEntity.ok(Map.of("result", cities));
                });
    }
}
