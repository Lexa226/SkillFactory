import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private static final String API_URL = "https://api.weather.yandex.ru/v2/forecast";

    public static String getWeatherData(double lat, double lon) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("YANDEX_API_KEY");

        try {
            String urlString = API_URL + "?lat=" + lat + "&lon=" + lon + "&hours=false";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Yandex-API-Key", apiKey);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printTemperature(double lat, double lon) {
        try {
            JSONObject jsonResponse = new JSONObject(getWeatherData(lat, lon));
            JSONObject fact = jsonResponse.getJSONObject("fact");
            double temperature = fact.getDouble("temp");
            System.out.println("\nТекущая температура: " + temperature + "°C");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Не удалось получить температуру.");
        }
    }

    public static double calculateAverageTemperature(double lat, double lon, int limit) {
        try {
            JSONObject jsonResponse = new JSONObject(getWeatherData(lat, lon));
            JSONArray forecasts = jsonResponse.getJSONArray("forecasts");

            if (forecasts.length() < limit) {
                System.out.println("Недостаточно данных для расчета. Использовано всех доступных прогнозов.");
                limit = forecasts.length();
            }

            double sumTemperature = 0;
            for (int i = 0; i < limit; i++) {
                JSONObject forecast = forecasts.getJSONObject(i);
                JSONObject parts = forecast.getJSONObject("parts");
                JSONObject day = parts.getJSONObject("day");

                double tempMax = day.getDouble("temp_max");
                double tempMin = day.getDouble("temp_min");

                double averageTempForDay = (tempMax + tempMin) / 2;
                sumTemperature += averageTempForDay;
            }

            return sumTemperature / limit;
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public static void printAverageTemperature(double lat, double lon, int limit) {
        double averageTemp = calculateAverageTemperature(lat, lon, limit);
        if (!Double.isNaN(averageTemp)) {
            System.out.println("Средняя температура за " + limit + " дней: " + averageTemp + "°C");
        } else {
            System.out.println("Не удалось рассчитать среднюю температуру.");
        }
    }
}