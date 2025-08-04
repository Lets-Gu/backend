package avengers.lion.place.dto;// 2) GeocodingResponse.java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResponse(
        String status,
        List<GeocodingResult> results,
        String error_message
) {}
