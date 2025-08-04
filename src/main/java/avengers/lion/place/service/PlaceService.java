package avengers.lion.place.service;

import avengers.lion.place.dto.GeocodeInfo;
import avengers.lion.place.dto.GeocodingResponse;
import avengers.lion.place.dto.GeocodingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final RestTemplate restTemplate;
    @Value("${google.geocoding.api-key}")
    private String apiKey;

    @Value("${google.geocoding.url}")
    private String baseUrl;

    public GeocodeInfo geocode(String query) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("address", query)
                .queryParam("key", apiKey)
                .encode()
                .toUriString();
        GeocodingResponse resp = restTemplate.getForObject(uri, GeocodingResponse.class);

        GeocodingResult result = resp.results()
                .stream()
                .findFirst()
                .orElseThrow();
        String formattedAddress = result.formatted_address();
        double lat = result.lat();
        double lng = result.lng();
        return new GeocodeInfo(formattedAddress, lat, lng);
    }
}
