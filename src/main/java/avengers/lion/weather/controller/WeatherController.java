package avengers.lion.weather.controller;

import avengers.lion.global.response.ResponseBody;
import avengers.lion.global.response.ResponseUtil;
import avengers.lion.weather.api.WeatherApi;
import avengers.lion.weather.dto.WeatherBasic;
import avengers.lion.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController implements WeatherApi {
    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<ResponseBody<WeatherBasic>> getWeather(){
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(weatherService.oneCall()));
    }
}
