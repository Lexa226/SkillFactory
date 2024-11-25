public class App {
    public static void main(String[] args) {
        // Здесь можно было сделать ввод пользователя, но я сделал так, чтобы просто проверить работу
        // программы.
        double lat = 55.75;
        double lon = 37.62;
        byte limit = 3;

        String weatherData = WeatherService.getWeatherData(lat, lon);
        if (weatherData != null) {
            System.out.println("Получены все данные о погоде:");
            System.out.println(weatherData);
        } else {
            System.out.println("Не удалось получить данные о погоде.");
        }

        WeatherService.printTemperature(lat, lon);
        WeatherService.printAverageTemperature(lat, lon, limit);
    }
}
