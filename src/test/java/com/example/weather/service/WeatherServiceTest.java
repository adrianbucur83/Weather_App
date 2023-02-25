package com.example.weather.service;

import com.example.weather.client.WeatherApiClient;
import com.example.weather.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

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
    void testGetCityFluxSuccess() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature("23 degree celsius");
        weatherData.setWind("10 km/hr");
        weatherData.setDescription("Sunny");
        List<WeatherForecast> forecastList = new ArrayList<>();
        WeatherForecast forecast1 = new WeatherForecast();
        forecast1.setTemperature("25 degree celsius");
        forecast1.setWind("12 km/hr");
        forecastList.add(forecast1);
        WeatherForecast forecast2 = new WeatherForecast();
        forecast2.setTemperature("22 degree celsius");
        forecast2.setWind("9 km/hr");
        forecastList.add(forecast2);
        weatherData.setForecast(forecastList);

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
        assertEquals(23.33, cities.get(0).getAverageTemperature(), 0.1);
        assertEquals(10.33, cities.get(0).getAverageWindSpeed(), 0.1);


        verify(weatherApiClient, times(3)).getWeatherData(anyString());
    }

    @Test
    void testGetCityFluxFailure() {
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
        assertEquals(Double.NaN, cities.get(0).getAverageTemperature());
        assertEquals(Double.NaN, cities.get(0).getAverageWindSpeed());

        assertEquals("London", cities.get(1).getName());
        assertEquals(Double.NaN, cities.get(1).getAverageTemperature());
        assertEquals(Double.NaN, cities.get(1).getAverageWindSpeed());

        assertEquals("Paris", cities.get(2).getName());
        assertEquals(Double.NaN, cities.get(2).getAverageTemperature());
        assertEquals(Double.NaN, cities.get(2).getAverageWindSpeed());

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

