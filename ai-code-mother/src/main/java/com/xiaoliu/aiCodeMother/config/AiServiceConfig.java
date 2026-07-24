package com.xiaoliu.aiCodeMother.config;

import com.xiaoliu.aiCodeMother.service.StreamAiChatService;
import com.xiaoliu.aiCodeMother.service.SyncAiChatService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI Service 配置
 */
@Configuration
public class AiServiceConfig {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    // 【关键修改 1】：把 ChatMemoryProvider 显式注册为 Spring Bean
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }

    @Bean
    public SyncAiChatService syncaiChatService(){
        return AiServices.builder(SyncAiChatService.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    public StreamAiChatService streamAiChatService(){
        return AiServices.builder(StreamAiChatService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                // 【关键修改 2】：直接注入刚才声明的 Bean
                .chatMemoryProvider(chatMemoryProvider())
                .build();
    }
}
