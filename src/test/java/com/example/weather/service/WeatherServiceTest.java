package com.example.weather.service;

import com.example.weather.client.WeatherApiClient;
import com.example.weather.model.City;
import com.example.weather.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {
    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void testGetCityFluxSuccess() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature("23 degree celsius");
        weatherData.setWind("10 km/hr");

        when(weatherApiClient.getWeatherData("New York")).thenReturn(Mono.just(weatherData));
        when(weatherApiClient.getWeatherData("London")).thenReturn(Mono.just(weatherData));
        when(weatherApiClient.getWeatherData("Paris")).thenReturn(Mono.just(weatherData));

        WeatherService weatherService = new WeatherService(weatherApiClient);

        List<String> cityList = List.of("New York", "London", "Paris");
        Flux<City> cityFlux = weatherService.getCityFlux(cityList);

        List<City> cities = cityFlux.collectList().block();
        assertNotNull(cities);
        assertEquals(3, cities.size());
        assertEquals("New York", cities.get(0).getName());
        assertEquals(23, cities.get(0).getTemperature());
        assertEquals(10, cities.get(0).getWind());

        assertEquals("London", cities.get(1).getName());
        assertEquals(23, cities.get(1).getTemperature());
        assertEquals(10, cities.get(1).getWind());

        assertEquals("Paris", cities.get(2).getName());
        assertEquals(23, cities.get(2).getTemperature());
        assertEquals(10, cities.get(2).getWind());

        // Verify that getWeatherData was called 3 times
        verify(weatherApiClient, times(3)).getWeatherData(anyString());
    }

    @Test
    public void testGetCityFluxFailure() {
        when(weatherApiClient.getWeatherData("New York")).thenReturn(Mono.error(new RuntimeException("Error getting weather data")));
        when(weatherApiClient.getWeatherData("London")).thenReturn(Mono.error(new RuntimeException("Error getting weather data")));
        when(weatherApiClient.getWeatherData("Paris")).thenReturn(Mono.error(new RuntimeException("Error getting weather data")));

        WeatherService weatherService = new WeatherService(weatherApiClient);

        List<String> cityList = List.of("New York", "London", "Paris");
        Flux<City> cityFlux = weatherService.getCityFlux(cityList);

        List<City> cities = cityFlux.collectList().block();
        assertNotNull(cities);
        assertEquals(3, cities.size());

        assertEquals("New York", cities.get(0).getName());
        assertEquals(Double.NaN, cities.get(0).getTemperature());
        assertEquals(Double.NaN, cities.get(0).getWind());

        assertEquals("London", cities.get(1).getName());
        assertEquals(Double.NaN, cities.get(1).getTemperature());
        assertEquals(Double.NaN, cities.get(1).getWind());

        assertEquals("Paris", cities.get(2).getName());
        assertEquals(Double.NaN, cities.get(2).getTemperature());
        assertEquals(Double.NaN, cities.get(2).getWind());

        // Verify that getWeatherData was called 1 time
        verify(weatherApiClient, times(3)).getWeatherData(anyString());
    }

    @Test
    void testCreateCsv() {
        List<City> cities = Arrays.asList(
                new City("New York", 70.0, 4.0),
                new City("London", 50.0, 6.0));

        String expectedCsv = "London,50.0,6.0\nNew York,70.0,4.0";

        String csv = weatherService.createCsv(cities);

        assertEquals(csv, expectedCsv);
    }
}

