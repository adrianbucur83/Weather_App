package com.example.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherForecast {
    private String day;
    private String temperature;
    private String wind;

}