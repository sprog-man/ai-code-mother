package com.xiaoliu.aiCodeMother.model.dto.chat;

import lombok.Data;

/**
 * AI 聊天请求
 */
@Data
public class ChatRequest {
    /**
     * 用户发送的消息
     */
    private String message;
}
