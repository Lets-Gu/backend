package avengers.lion.weather.api;

import avengers.lion.global.config.swagger.SwaggerApiFailedResponse;
import avengers.lion.global.config.swagger.SwaggerApiResponses;
import avengers.lion.global.config.swagger.SwaggerApiSuccessResponse;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.global.response.ResponseBody;
import avengers.lion.weather.dto.WeatherBasic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@Tag(name = "날씨 API", description = "OpenWeather One Call 기반 구미 지역(기본 좌표) 날씨 조회")
public interface WeatherApi {

    @Operation(
            summary = "날씨 기본 정보 조회",
            description = """
            Redis 캐시(KST 매 정시)에 저장된 날씨 데이터를 반환합니다.
            응답 구조:
            - data.current: 현재 기온/아이콘/오늘 최고·최저
            - data.hourly48: 48시간 (KST '오전/오후 h시' 포맷), 시간별 기온/아이콘
            - data.next5Days: 오늘 제외 5일, 요일, 일최고/일최저/강수확률/아이콘
            
            아이콘 사용 방법:
            - 각 기온 데이터에는 `icon` 코드가 포함됩니다.
            - 해당 코드를 아래 URL에 치환하여 이미지로 사용합니다:
            https://openweathermap.org/img/wn/{icon}@2x.png
            (예: icon 값이 `10d`라면 → `https://openweathermap.org/img/wn/10d@2x.png`)
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    schema = @Schema(implementation = WeatherBasic.class)
                    )
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = WeatherBasic.class,
                    description = "날씨 데이터 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(
                            value = ExceptionType.WEATHER_API_ERROR,
                            description = "OpenWeather API 호출 실패 또는 응답 파싱 오류"
                    ),
            }
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    ResponseEntity<ResponseBody<WeatherBasic>> getWeather();
}
