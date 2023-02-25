package com.example.weather.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class WeatherData {
    private String temperature;
    private String wind;
    private String description;
    private List<WeatherForecast> forecast;

}