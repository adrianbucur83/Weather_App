package com.example.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class City {
    private String name;
    private double temperature;
    private double wind;

}
