package com.xiaoliu.aiCodeMother.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface StreamAiChatService {

    /**
     * AI 对话（流式-无记忆）
     *
     * @param userMessage 用户消息
     * @return 流式 Flux，逐个发射 AI 回复的片段
     */
    @SystemMessage("""
            你是一个智能的 AI 编程助手，擅长回答编程问题、编写代码、解释技术概念。
            
            回答要求：
            1. 回答要简洁准确
            2. 如果涉及代码，要给出完整的可运行示例
            3. 如果不确定，要诚实地说不确定
            """)
    Flux<String> chatStream(String userMessage);

    /**
     * AI 对话（流式，有记忆）
     *
     * @param memoryId 记忆 ID
     * @param userMessage 用户消息
     * @return 流式回复
     */
    @SystemMessage("""
            你是一个智能的 AI 交流助手，擅长回答问题、进行对话。
            
            回答要求：
            1. 回答要简洁准确
            2. 如果涉及代码，要给出完整的可运行示例
            3. 如果不确定，要诚实地说不确定
            """)
    Flux<String> chatStreamWithMemory(@MemoryId String memoryId,@UserMessage String userMessage);
}
