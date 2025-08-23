package avengers.lion.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UploadUrlResponse", description = "클라이언트가 Cloudinary에 직접 업로드할 수 있도록 제공되는 사전 서명 정보")
public record UploadUrlResponse(

        @Schema(
                description = "Cloudinary 이미지 업로드 엔드포인트(직접 업로드 대상 URL)",
                example = "https://api.cloudinary.com/v1_1/do5bj2fie/image/upload"
        )
        String uploadUrl,

        @Schema(
                description = "업로드 식별자(Cloudinary publicId). 이후 검증 및 실패 시 정리 용도로 사용",
                example = "missions/temp/temp-68b37541-51bc-4528-8cc5-8d5ddab65dc3/dff590b8-0413-480f-a862-2eba4519ab90"
        )
        String uploadKey,

        @Schema(
                description = "Cloudinary unsigned 업로드 preset. 업로드 폼 필드 'upload_preset'으로 함께 전송",
                example = "LetsGGu"
        )
        String uploadPreset,

        @Schema(
                description = "미션 사진 가이드",
                example = "https://res.cloudinary.com/do5bj2fie/image/upload/v1755961155/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C_13_bk4q4r.jpg"
        )
        String imageUrl
) {}
