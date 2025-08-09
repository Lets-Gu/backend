package avengers.lion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @Schema(description = "이메일 주소", example = "kyoung1678@naver.com")
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식을 입력해주세요")
        String email,

        @Schema(description = "닉네임", example = "하이")
        @NotBlank(message = "닉네임은 필수입니다")
        String nickname,

        @Schema(description = "비밀번호", example = "password1234")
        @NotBlank(message = "비밀번호는 필수입니다")
        String password,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.png")
        String profileImageUrl
) {
}
