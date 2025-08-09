package avengers.lion.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableRedisRepositories
@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;

    @Bean
    // Redis 서버 연결을 관리하는 객체
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    // Redis에 데이터를 저장/조회하는 클래스 -> 키 : String, 값 : Object
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        // 새로운 RedisTemplate 인스턴스 생성
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        // 키 직렬화 방식 -> Redis는 네트워크로 데이터를 주고 받을 때 바이트로 처리하기 때문에 String을 바이트 배열로 변환하는 객체 사용
        t.setKeySerializer(new StringRedisSerializer());
        // 값 직렬화 방식
        t.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return t;
    }
}
