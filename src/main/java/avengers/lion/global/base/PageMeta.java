package avengers.lion.global.base;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "OverviewResponse.PageMeta", description = "다음 페이지 조회를 위한 커서 메타")
public record PageMeta(
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        Boolean hasNext,
        @Schema(description = "다음 페이지 시작 기준 시간 (LocalDateTime ISO-8601)", example = "2025-08-09T12:22:10")
        LocalDateTime nextAt,
        @Schema(description = "다음 페이지 시작 기준 ID", example = "5")
        Long nextId
) { }