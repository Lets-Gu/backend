package avengers.lion.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VerifyImageResponse", description = "이미지 인증(비동기 분석) 시작 결과")
public record VerifyImageResponse(

        @Schema(
                description = "분석 작업 식별자. SSE(/analyze/{jobId}/events) 구독에 사용",
                example = "977d9425-f91b-4d0f-a44a-ce51c8add685"
        )
        String jobId
) {}
