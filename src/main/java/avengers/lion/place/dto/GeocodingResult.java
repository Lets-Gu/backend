package avengers.lion.place.dto;// 3) GeocodingResult.java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResult(
        String formatted_address,
        double lat,
        double lng
) {}
