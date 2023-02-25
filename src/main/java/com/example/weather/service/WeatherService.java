package com.example.weather.service;

import com.example.weather.client.WeatherApiClient;
import com.example.weather.model.City;
import com.example.weather.model.WeatherForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the implementation of the WeatherService class,
 * which provides methods to retrieve weather data for a list of
 * cities and generate a CSV file with the retrieved data.
 * The class uses the WeatherApiClient to retrieve weather data and creates a
 * Flux of City objects from a list of city names. It also provides methods
 * to create a CSV string representation of the retrieved weather data and write it to a file.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherApiClient weatherApiClient;

    public Flux<City> getCityFlux(List<String> cityList) {
        return Flux.fromIterable(cityList)
                .flatMap(c -> weatherApiClient.getWeatherData(c.trim())
                        .map(wd -> {
                            double temperatureSum = Double.parseDouble(wd.getTemperature().split(" ")[0]);
                            double windSum = Double.parseDouble(wd.getWind().split(" ")[0]);
                            int forecastCount = wd.getForecast().size();
                            for (WeatherForecast forecast : wd.getForecast()) {
                                temperatureSum += Double.parseDouble(forecast.getTemperature().split(" ")[0]);
                                windSum += Double.parseDouble(forecast.getWind().split(" ")[0]);
                            }
                            double temperatureAvg = temperatureSum / (forecastCount + 1);
                            double windAvg = windSum / (forecastCount + 1);
                            return new City(c.trim(), temperatureAvg, windAvg);
                        })
                        .onErrorResume(e -> {
                            log.info("Error getting weather data for city %s: %s".formatted(c.trim(), e.getMessage()));
                            return Mono.just(new City(c.trim(), Double.NaN, Double.NaN));
                        })
                );
    }

    public String createCsv(List<City> cities) {
        cities.sort(Comparator.comparing(City::getName));
        return cities.stream()
                .map(city1 -> String.format("%s,%s,%s", city1.getName(), city1.getAverageTemperature(), city1.getAverageWindSpeed()))
                .collect(Collectors.joining("\n"));
    }

    public void writeCsvFile(List<City> cities) {
        String csv = createCsv(cities);

        try (FileWriter csvWriter = new FileWriter("weather.csv")) {
            csvWriter.write("Name,temperature,wind\n");
            csvWriter.write(csv);
            csvWriter.flush();
        } catch (IOException e) {
            log.error("Error writing CSV file: {}", e.getMessage());
        }
    }
}