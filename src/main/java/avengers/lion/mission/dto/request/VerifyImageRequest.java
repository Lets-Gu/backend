package avengers.lion.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "미션 인증 이미지 검증 요청 DTO")
public record VerifyImageRequest(

        @Schema(
                description = """
        업로드용 URL
        - 프론트에서 **Cloudinary Direct Upload** 시 사용하는 API 엔드포인트
        - POST 요청을 보내 원본 이미지를 업로드
        - 예시: "https://api.cloudinary.com/v1_1/{cloud_name}/image/upload"
        """,
                example = "https://api.cloudinary.com/v1_1/do5bj2fie/image/upload"
        )
        @NotBlank(message = "업로드 URL은 필수입니다")
        String uploadImageUrl,

        @Schema(
                description = """
        조회용 URL
        - 업로드 완료 후 생성된 **실제 이미지의 접근 경로**
        """,
                example = "https://res.cloudinary.com/do5bj2fie/image/upload/missions/temp/temp-16701488-2b54-4522-b9c7-17e090f169c0/25551a94-bae0-470f-a41f-ea057bbfbac5"
        )
        @NotBlank(message = "조회용 이미지 URL은 필수입니다")
        String imageUrl,

        @Schema(
                description = """
        Cloudinary 업로드 시 발급되는 `public_id`
        - 삭제, 관리, 변환 시 필요
        - 예시: "missions/temp/temp-16701488-2b54-4522-b9c7-17e090f169c0/25551a94-bae0-470f-a41f-ea057bbfbac5"
        """
        )
        @NotBlank(message = "업로드 키는 필수입니다")
        String uploadKey
) {}
