package avengers.lion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "이메일", example = "qwer1234@kumoh.ac.kr")
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식을 입력해주세요")
        String email,

        @Schema(description = "비밀번호", example = "qwer123456!")
        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {
}