package avengers.lion.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "PageResult", description = "커서 기반 무한 스크롤 페이지 결과(제네릭). data 타입은 API별 아이템 타입을 따릅니다.")
public record PageResult<T>(
        @Schema(description = "현재 페이지 데이터 배열(예: UnWrittenReviewResponse[] 또는 WrittenReviewResponse[])")
        List<T> data,

        @Schema(description = "다음 페이지 존재 여부", example = "false")
        boolean hasNext,

        @Schema(description = "다음 페이지 조회 시작 기준 시간 (LocalDateTime ISO-8601)", example = "2025-08-09T12:20:10")
        LocalDateTime nextCreatedAt,

        @Schema(description = "다음 페이지 조회 시작 기준 ID", example = "2")
        Long nextId
) {}
