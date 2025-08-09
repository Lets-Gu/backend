package avengers.lion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record LoginResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "이메일", example = "qwer1234@kumoh.ac.kr")
        String email,

        @Schema(description = "닉네임", example = "하이")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "dkasjdaj")
        String imageUrl
) {
}
