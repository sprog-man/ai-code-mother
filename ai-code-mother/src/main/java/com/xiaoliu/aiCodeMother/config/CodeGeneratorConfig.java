package com.xiaoliu.aiCodeMother.config;


import com.xiaoliu.aiCodeMother.service.CodeGeneratorService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码生成器配置
 */
@Configuration
public class CodeGeneratorConfig {
    @Resource
    private ChatModel chatModel;


    @Bean
    public CodeGeneratorService codeGeneratorService() {
        return AiServices.builder(CodeGeneratorService.class)
                .chatModel(chatModel)
                .build();
    }

}
