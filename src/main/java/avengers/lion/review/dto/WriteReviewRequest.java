package avengers.lion.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "리뷰 작성 요청 DTO")
public record WriteReviewRequest(
        @Schema(description = "완료 미션 ID", example = "2")
        @NotNull
        Long completedMissionId,

        @Schema(description = "리뷰 내용", example = "정말 재밌었어요! 다음에 또 올게요 :)")
        @NotBlank(message = "content를 입력해주세요.")
        String content
) {}
