package avengers.lion.auth.dto;

public record LoginResponse(
        Long memberId,
        String email,
        String nickname,
        String imageUrl
)
{ }
