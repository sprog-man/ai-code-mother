package com.xiaoliu.aiCodeMother.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface SyncAiChatService {
    /**
     * AI 对话 (无记忆)
     *
     * @param userMessage 用户消息
     * @return AI 回复
     */
    @SystemMessage("""
            你是一个专业的社交高手
            """)
    String chat(String userMessage);

    /**
     * AI 对话（同步，有记忆）
     *
     * @param memoryId 记忆 ID（通常是用户 ID）
     * @param userMessage 用户消息
     * @return AI 回复
     */
    @SystemMessage("你是一个智能的 AI 编程助手")
    String chatWithMemory(@MemoryId String memoryId, @UserMessage String userMessage);


}
