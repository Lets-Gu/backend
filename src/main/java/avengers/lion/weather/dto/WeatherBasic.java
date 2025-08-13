package avengers.lion.weather.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "날씨 기본 응답 뷰 (현재/48시간/향후 5일)")
public record WeatherBasic(

        @Schema(
                description = "현재 날씨",
                example = """
                { "temp": 25.8, "icon": "10d", "todayMax": 26.64, "todayMin": 22.64 }
                """
        )
        Now current,

        @ArraySchema(
                schema = @Schema(implementation = Hour.class),
                arraySchema = @Schema(
                        description = "6시간 시계열 (KST '오전/오후 h시' 포맷)",
                        example = """
                        [
                          { "time": "오후 3시", "temp": 25.6, "icon": "04d" },
                          { "time": "오후 4시", "temp": 25.8, "icon": "10d" },
                          { "time": "오후 5시", "temp": 25.48, "icon": "10d" }
                        ]
                        """
                )
        )
        List<Hour> hourly6
) {

    @Schema(description = "현재 날씨")
    public record Now(
            @Schema(description = "현재 기온(°C)", example = "25.8")
            Double temp,
            @Schema(description = "OpenWeather 아이콘 코드", example = "10d")
            String icon,
            @Schema(description = "오늘 최고 기온(°C)", example = "26.64")
            Double todayMax,
            @Schema(description = "오늘 최저 기온(°C)", example = "22.64")
            Double todayMin
    ) {}

    @Schema(description = "시간대별 날씨")
    public record Hour(
            @Schema(description = "시각 (KST, '오전/오후 h시')", example = "오후 3시")
            String time,
            @Schema(description = "기온(°C)", example = "25.6")
            Double temp,
            @Schema(description = "OpenWeather 아이콘 코드", example = "04d")
            String icon
    ) {
        // (옵션) 필요 시 시간대 최고/최저 확장용
        public record Temp(
                @Schema(example = "26.6") Double max,
                @Schema(example = "22.6") Double min
        ) {}
    }
}
