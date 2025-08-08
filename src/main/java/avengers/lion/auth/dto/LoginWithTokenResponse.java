package avengers.lion.auth.dto;

public record LoginWithTokenResponse(
        LoginResponse loginResponse,
        String accessToken
) {
}