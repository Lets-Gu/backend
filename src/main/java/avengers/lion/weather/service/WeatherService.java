package avengers.lion.weather.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.weather.dto.OpenWeatherDto;
import avengers.lion.weather.dto.WeatherBasic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate rest;
    private final RedisTemplate<String, Object> redis;

    @Value("${openweather.api.base-url}")
    String baseUrl;
    @Value("${openweather.api.key}")
    String apiKey;
    @Value("${openweather.api.lang}")
    String lang;
    @Value("${openweather.api.units}")
    String units;
    @Value("${openweather.api.default-lat}")
    double lat;
    @Value("${openweather.api.default-lon}")
    double lon;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN);

    private String cacheKey() {
        ZonedDateTime hour = ZonedDateTime.now(KST).truncatedTo(ChronoUnit.HOURS);
        return "weather:gumi:basic:" +hour.toEpochSecond();
    }
    private Duration ttlUntilNextHour() {
        ZonedDateTime now = ZonedDateTime.now(KST);
        return Duration.between(now, now.truncatedTo(ChronoUnit.HOURS).plusHours(1)).plusSeconds(5);
    }

    public WeatherBasic oneCall() {
        // redis에서 데이터를 꺼낸다
        // redis에서 데이터가 없으면
        String key = cacheKey();
        Object hit = redis.opsForValue().get(key);
        if(hit instanceof WeatherBasic cached) {
            log.info("날씨 데이터 캐시");
            return cached;
        }
        log.info("날씨 데이터 캐시안됨");
        OpenWeatherDto dto =  requestOpenWeather();
        OpenWeatherDto.Current current = dto.current();
        List<OpenWeatherDto.Hourly> hourly = dto.hourly();
        List<OpenWeatherDto.Daily> daily = dto.daily();

        // 현재
        WeatherBasic.Now now = new WeatherBasic.Now(
                current.temp(),
                current.weather().getFirst().icon(),
                daily.getFirst().temp().max(),
                daily.getFirst().temp().min());

        // 6시간
        List<WeatherBasic.Hour> hourList = hourly.stream()
                .limit(6)
                .skip(1)
                .map(hour -> {
                    ZonedDateTime kstTime = Instant.ofEpochSecond(hour.dt())
                            .atZone(ZoneId.of("Asia/Seoul"));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h시", Locale.KOREAN);
                    String formattedTime = kstTime.format(formatter);
                    return new WeatherBasic.Hour(formattedTime, hour.temp(), hour.weather().getFirst().icon());
                })
                .toList();

        // 오늘 제외
        WeatherBasic view = new WeatherBasic(now, hourList);
        redis.opsForValue().set(key, view, ttlUntilNextHour());
        return view;
    }
    public OpenWeatherDto requestOpenWeather() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/data/3.0/onecall")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("lang", lang)
                .queryParam("units", units)
                .queryParam("exclude", "minutely,alerts")
                .build()
                .toUri();
        log.info("onecall uri = {}", uri);
        try{
            return rest.getForObject(uri, OpenWeatherDto.class);
        } catch(HttpClientErrorException | HttpServerErrorException | UnknownContentTypeException | ResourceAccessException e) {
            log.error("OpenWeather API 호출 실패: {}", e.getMessage());
            throw new BusinessException(ExceptionType.WEATHER_API_ERROR);
        }

    }
}