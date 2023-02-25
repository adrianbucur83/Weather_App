package com.example.weather.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecast {
    private String day;
    private String temperature;
    private String wind;

}