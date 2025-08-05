package avengers.lion.place.service;

import avengers.lion.global.exception.BusinessException;
import avengers.lion.global.exception.ExceptionType;
import avengers.lion.place.dto.GeocodeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.places.api-key}")
    private String apiKey;

    @Value("${google.places.textsearch-url}")
    private String textSearchUrl;

    @Value("${google.geocoding.url}")  // 예: https://maps.googleapis.com/maps/api/geocode/json
    private String geocodeUrl;


    public GeocodeInfo geocode(String placeName) {
        return tryGooglePlacesTextSearch(placeName);
    }


    private GeocodeInfo tryGooglePlacesTextSearch(String placeName) {
        // 요청 바디 생성
        String requestBody = """
                   {
                       "textQuery": "%s",
                       "languageCode": "ko"
                   }
                   """.formatted(placeName + " 경상북도 구미시");

            // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "places.displayName,places.location,places.id");


        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://places.googleapis.com/v1/places:searchText",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String json = response.getBody();
            log.debug("✅ Google Places v1 응답:\n{}", json);

            // GeocodeInfo 파싱 로직 추가 가능
            // 예: JSON 파싱하여 lat/lng 추출 후 return new GeocodeInfo(lat, lng);
            JsonNode root = objectMapper.readTree(json);
            JsonNode places = root.path("places");
            JsonNode first = places.get(0);
            double lat = first.path("location").path("latitude").asDouble();
            double lng = first.path("location").path("longitude").asDouble();

            return new GeocodeInfo(lat, lng);
            } catch (Exception e) {
                throw new BusinessException(ExceptionType.GOOGLE_API_ERROR);
            }
    }
}
