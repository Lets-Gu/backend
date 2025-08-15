package avengers.lion.member.dto;


import jakarta.validation.constraints.Pattern;

public record UpdateNicknameRequest(
        @Pattern(regexp = "^[a-z0-9]{1,10}$",
                message = "닉네임은 영어 소문자와 숫자만 사용하며, 최대 10자까지 가능합니다.")
        String nickname
) {
}
