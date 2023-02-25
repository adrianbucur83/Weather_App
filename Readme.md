# Weather App

This is a simple Weather App that retrieves weather data from an external API and creates a CSV file from the data. The application is built with Spring Boot and uses reactive programming with Project Reactor to handle asynchronous operations.

### Requirements

To run the application, you need to have the following installed on your machine:

* Java 11 or higher
* Gradle
* Git

### Installation
To install the application, follow these steps:

Clone the repository:

`git clone https://github.com/adrianbucur83/Weather_App.git`

Build the application:

`mvn clean package`

### Usage

To use the application, follow these steps:

Run the application:

`java -jar target/weather-app-0.0.1-SNAPSHOT.jar`

The application will retrieve weather data for a list of cities from an external API and create a CSV file with the data in the project directory.

### Configuration

The application uses the following configuration properties:

weatherUri: The URL of the weather API. 
The default value is https://goweather.herokuapp.com.
