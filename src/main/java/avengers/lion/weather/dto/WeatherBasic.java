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
                        description = "48시간 시계열 (KST '오전/오후 h시' 포맷)",
                        example = """
                        [
                          { "time": "오후 3시", "temp": 25.6, "icon": "04d" },
                          { "time": "오후 4시", "temp": 25.8, "icon": "10d" },
                          { "time": "오후 5시", "temp": 25.48, "icon": "10d" }
                        ]
                        """
                )
        )
        List<Hour> hourly48,

        @ArraySchema(
                schema = @Schema(implementation = Day.class),
                arraySchema = @Schema(
                        description = "오늘 제외 5일 요약",
                        example = """
                        [
                          { "dayOfWeek": "일", "tempMax": 31.53, "tempMin": 20.42, "rainProbability": 0.0, "icon": "04d" },
                          { "dayOfWeek": "월", "tempMax": 29.57, "tempMin": 20.49, "rainProbability": 0.0, "icon": "04d" },
                          { "dayOfWeek": "화", "tempMax": 24.43, "tempMin": 22.5, "rainProbability": 1.0, "icon": "10d" },
                          { "dayOfWeek": "수", "tempMax": 23.74, "tempMin": 22.79, "rainProbability": 1.0, "icon": "10d" },
                          { "dayOfWeek": "목", "tempMax": 29.86, "tempMin": 23.14, "rainProbability": 0.36, "icon": "10d" }
                        ]
                        """
                )
        )
        List<Day> next5Days

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

    @Schema(description = "일자별 요약")
    public record Day(
            @Schema(description = "요일 (짧은 형식)", example = "일")
            String dayOfWeek,
            @Schema(description = "일 최고 기온(°C)", example = "31.53")
            Double tempMax,
            @Schema(description = "일 최저 기온(°C)", example = "20.42")
            Double tempMin,
            @Schema(description = "강수 확률(0.0~1.0)", example = "0.0")
            Double rainProbability,
            @Schema(description = "OpenWeather 아이콘 코드", example = "04d")
            String icon
    ) {}
}
