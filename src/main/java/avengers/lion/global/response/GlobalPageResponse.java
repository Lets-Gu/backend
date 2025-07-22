package avengers.lion.global.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "페이지네이션 응답 공통 포맷")
public class GlobalPageResponse<T> {

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private final int pageNumber;

    @Schema(description = "페이지 당 항목 수", example = "12")
    private final int pageSize;

    @Schema(description = "전체 항목 수", example = "100")
    private final long totalElements;

    @Schema(description = "전체 페이지 수", example = "9")
    private final int totalPages;

    @Schema(description = "정렬 정보", example = "companyName: ASC")
    private final String pageSort;

    @ArraySchema(
            arraySchema = @Schema(description = "페이지 내 컨텐츠 목록"),
            schema = @Schema(description = "요소는 호출 API에 따라 달라집니다 (예: CompanyResponseDto)")
    )
    private final List<T> pageContents;

    public GlobalPageResponse(
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            String pageSort,
            List<T> pageContents
    ) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageSort = pageSort;
        this.pageContents = pageContents;
    }

    public static <T> GlobalPageResponse<T> create(Page<T> pageDto) {
        return new GlobalPageResponse<>(
                pageDto.getNumber(),
                pageDto.getSize(),
                pageDto.getTotalElements(),
                pageDto.getTotalPages(),
                pageDto.getSort().toString(),
                pageDto.getContent());
    }
}
