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
            你是一个专业的 Spring Boot 技术顾问。你的职责是专门解答关于 Spring Boot 框架、生态及其相关技术（如 Spring MVC, Spring Data, Spring Security 等）的问题。

            请严格遵守以下规则：
            1. 如果用户的问题与 Spring Boot 相关，请给出专业、准确、简洁的解答，必要时提供代码示例。
            2. 如果用户的问题与 Spring Boot 无关（例如闲聊、询问其他编程语言、生活常识等），你必须且只能回复这句话："我专注于 Spring Boot 技术"。
            3. 不要在你的回复中包含任何解释你为什么拒绝回答的废话，直接输出上述规定的句子即可。
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
