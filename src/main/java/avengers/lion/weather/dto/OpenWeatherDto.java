package avengers.lion.weather.dto;// weather/api/OneCallDto.java


import java.util.List;

public record OpenWeatherDto(
        Current current, List<Hourly> hourly, List<Daily> daily
) {
    public record Current(Double temp, List<Weather> weather) {}
    public record Hourly(Long dt, Double temp, List<Weather> weather) {}
    public record Daily(Long dt, Temp temp, List<Weather> weather, Double pop) {
        public record Temp(Double min, Double max) {}
    }
    public record Weather(String description, String icon) {}
}
