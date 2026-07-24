package com.xiaoliu.aiCodeMother.service;

import dev.langchain4j.service.SystemMessage;

/**
 * AI 对话服务接口
 */
public interface AiService {
    /**
     * 发送消息给 AI，获取回复
     *
     * @param message 用户消息
     * @return AI 的回复内容
     */
    String chat(String message);
}
