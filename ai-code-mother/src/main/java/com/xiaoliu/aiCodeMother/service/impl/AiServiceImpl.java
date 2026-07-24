package com.xiaoliu.aiCodeMother.service.impl;

import com.xiaoliu.aiCodeMother.service.AiService;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.xiaoliu.aiCodeMother.service.AiService;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
    @Resource
    private ChatModel chatModel;
    /**
     * 发送消息给 AI，获取回复
     *
     * @param message 用户消息
     * @return AI 的回复内容
     */
    @Override
    public String chat(String message) {
        // 直接调用 AI 模型
        return chatModel.chat(message);
    }
}
