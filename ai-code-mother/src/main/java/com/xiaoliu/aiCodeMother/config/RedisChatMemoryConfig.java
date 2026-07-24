package com.xiaoliu.aiCodeMother.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 对话记忆存储配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryConfig {
    private String host;

    private int port;

    private String password;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore(){
        return RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .password(password != null ? password : "")
                .build();
    }
}
