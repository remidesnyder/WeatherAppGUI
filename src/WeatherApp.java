import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Created by Desnyder Rémi
 * Date: 09/12/2023
 */

public class WeatherApp {

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlApi = "https://api.open-meteo.com/v1/forecast?latitude=" +
                latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try {

            HttpURLConnection connection = fetchApiResponse(urlApi);

            if (connection.getResponseCode() == 200) {

                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while (scanner.hasNextLine()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();
                try (StringReader stringReader = new StringReader(resultJson.toString())) {
                    JSONObject resultJsonObject = (JSONObject) parser.parse(stringReader);

                    JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");

                    JSONArray time = (JSONArray) hourly.get("time");
                    int index = findIndexOfCurrentTime(time);

                    JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                    double temperature = (double) temperatureData.get(index);

                    JSONArray weatherCodeData = (JSONArray) hourly.get("weather_code");
                    String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));

                    JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
                    long humidity = (long) humidityData.get(index);

                    JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
                    double windSpeed = (double) windSpeedData.get(index);

                    JSONObject weatherData = new JSONObject();
                    weatherData.put("temperature", temperature);
                    weatherData.put("weatherCondition", weatherCondition);
                    weatherData.put("humidity", humidity);
                    weatherData.put("windSpeed", windSpeed);

                    return weatherData;
                }


            } else {
                System.out.println("Error: " + connection.getResponseCode());
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replace(" ", "+");
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=fr&format=json";

        try {
            HttpURLConnection connection = fetchApiResponse(url);

            if (connection.getResponseCode() == 200) {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while (scanner.hasNextLine()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();
                try (StringReader stringReader = new StringReader(resultJson.toString())) {
                    JSONObject resultJsonObject = (JSONObject) parser.parse(stringReader);

                    JSONArray locationData = (JSONArray) resultJsonObject.get("results");

                    return locationData;
                }
            } else {
                System.out.println("Error: " + connection.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();
            return connection;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);

            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = now.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";

        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            weatherCondition = "Cloudy";
        } else if (weatherCode >= 51L && weatherCode <= 67L ||
                (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;

    }

}
