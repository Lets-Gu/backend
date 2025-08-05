package avengers.lion.mission.service;

import avengers.lion.mission.domain.Mission;
import avengers.lion.mission.domain.MissionBatches;
import avengers.lion.place.domain.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionGenerateService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    public List<Mission> generateMissionsFromPlaces(List<Place> places, MissionBatches batch) {
        List<Mission> missions = new ArrayList<>();

        for (Place place : places) {
            try {
                log.info(">> Generating mission for place: {} (category={})",
                        place.getName(), place.getCategory());
                MissionContent content = generateMissionForPlace(place);
                log.info("<< Received mission for {}: title='{}', description='{}'",
                        place.getName(), content.title(), content.description());

                Mission mission = Mission.createFromPlace(
                        place, content.title(), content.description(), batch);
                missions.add(mission);
            } catch (Exception e) {
                log.error("❌ Failed to generate mission for place: {}", place.getName(), e);
            }
        }

        return missions;
    }

    private MissionContent generateMissionForPlace(Place place) {
        String prompt = createMissionPrompt(place);
        log.debug("---- Prompt >>>\n{}", prompt);

        String gptResponse = callOpenAI(prompt);
        log.debug("---- GPT Raw Response >>>\n{}", gptResponse);

        return parseMissionResponse(gptResponse);
    }

    private String createMissionPrompt(Place place) {
        return String.format("""
            경상북도 구미시의 "%s"에 대한 위치 기반 미션을 생성해주세요.
            장소 카테고리: %s

            다음 조건을 만족하는 미션을 만들어주세요:
            1. 해당 장소를 실제로 방문해야 완료할 수 있는 미션
            2. 사진 촬영이나 특정 행동을 포함하는 미션
            3. 지역 문화나 특색을 반영하는 미션
            4. 간단하면서도 흥미로운 미션

            응답 형식 (JSON):
            {
                "title": "미션 제목 (20자 이내)",
                "description": "미션 설명 (20자 이내)"
            }
            """,
                place.getName(),
                place.getCategory().getName()
        );
    }

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        String requestBody = String.format("""
            {
                "model": "gpt-4o-mini",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ],
                "max_tokens": 250,
                "temperature": 0.8
            }
            """, prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        log.debug("---- Request Body >>>\n{}", requestBody);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    openaiApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String body = response.getBody();
            log.debug("---- HTTP Status: {}, Headers: {}", response.getStatusCode(), response.getHeaders());
            log.debug("---- Response Body >>>\n{}", body);

            JsonNode root = objectMapper.readTree(body);
            JsonNode choices = root.path("choices");
            if (choices.isEmpty()) {
                log.warn("No choices in OpenAI response, using fallback.");
                return generateFallbackMission();
            }

            return choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생:", e);
            return generateFallbackMission();
        }
    }

    private String generateFallbackMission() {
        return """
            {
                "title": "장소 탐방 미션",
                "description": "해당 장소를 방문하여 인증 사진을 촬영해주세요."
            }
            """;
    }

    private MissionContent parseMissionResponse(String gptResponse) {
        try {
            String jsonPart = extractJsonFromResponse(gptResponse);
            log.debug("---- Extracted JSON for parsing >>>\n{}", jsonPart);

            JsonNode json = objectMapper.readTree(jsonPart);
            String title = json.path("title").asText();
            String desc  = json.path("description").asText();
            log.debug("---- Parsed title='{}', description='{}'", title, desc);

            return new MissionContent(title, desc);
        } catch (Exception e) {
            log.error("파싱 중 예외 발생:", e);
            return new MissionContent(
                    "장소 탐방 미션",
                    "해당 장소를 방문하여 인증 사진을 촬영해주세요."
            );
        }
    }

    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        return response;
    }

    private record MissionContent(String title, String description) {}
}
