package avengers.lion.mission.service;

import avengers.lion.mission.dto.VerificationEvent;
import avengers.lion.mission.dto.VerificationEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationEventService {
    
    private final ObjectMapper objectMapper;
    // jobId -> SseEmitter를 보관
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /*
    새로운 SSE 연결 객체를 만들고, 콜백 등록 후 맵에 넣는다.
     */
    public SseEmitter createEventStream(String jobId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃

        // SSE가 정상적으로 complete 호출되어 끝났을 때 실행
        emitter.onCompletion(() -> {
            log.debug("SSE connection completed for jobId: {}", jobId);
            emitters.remove(jobId);
        });

        // 타임아웃동안 아무런 이벤트도 보내지 않으면 호출
        emitter.onTimeout(() -> {
            log.debug("SSE connection timeout for jobId: {}", jobId);
            emitters.remove(jobId);
        });

        // 네트워크 에러나 예외가 터졌을 때 실행
        emitter.onError((e) -> {
            log.error("SSE connection error for jobId: {}", jobId, e);
            emitters.remove(jobId);
        });

        // SSeEmitter를 jobId에 보관
        emitters.put(jobId, emitter);
        
        // 초기 연결 확인 이벤트 전송
        sendEvent(jobId, VerificationEvent.started(jobId));
        
        return emitter;
    }

    /*
    특정 jobId의 SseEmitter를 조회해서 이벤트를 전송
    jobId : 어떤 작업에 대한 이벤트인지, VerificationEvent : 전송할 이벤트 데이터
     */
    public void sendEvent(String jobId, VerificationEvent event) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter == null) {
            log.warn("No SSE emitter found for jobId: {}", jobId);
            return;
        }
        
        try {
            //VerificationEvent 객체를 JSON 문자열로 직렬화
            String eventData = objectMapper.writeValueAsString(event);
            // 이벤트 전송
            emitter.send(SseEmitter.event()
                .name("verification")
                .data(eventData)
                .id(System.currentTimeMillis() + ""));
            
            // COMPLETED, FAILED, ERROR 이벤트 시 연결 종료
            if (isTerminalEvent(event.eventType())) {
                // SSE 연결 정상 종료
                emitter.complete();
                // 메모리에서 emitter 제거
                emitters.remove(jobId);
            }
            
        } catch (IOException e) {
            log.error("Failed to send SSE event for jobId: {}", jobId, e);
            emitter.completeWithError(e);
            emitters.remove(jobId);
        }
    }

    private boolean isTerminalEvent(VerificationEventType eventType) {
        return switch (eventType) {
            case COMPLETED, FAILED, ERROR -> true;
            case STARTED, PROGRESS -> false;
        };
    }
}