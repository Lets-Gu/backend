package avengers.lion.auth.dto;

public record RegisterResponse(
        Long memberId,
        String email,
        String nickname
) {
}