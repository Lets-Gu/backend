package avengers.lion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record LoginResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "프로필 이미지 URL", example = "dkasjdaj")
        String imageUrl
) {
}
