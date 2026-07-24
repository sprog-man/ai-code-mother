package com.xiaoliu.aiCodeMother;

import com.xiaoliu.aiCodeMother.service.StreamAiChatService;
import com.xiaoliu.aiCodeMother.service.SyncAiChatService;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeMotherApplicationTests {
    @Resource
    private ChatModel chatModel;

    @Resource
    private SyncAiChatService syncAiChatService;

    @Resource
    private StreamAiChatService streamAiChatService;

    @Test
    void contextLoads() {
        String answer = chatModel.chat("请用一句话介绍你自己");
        System.out.println("=== AI 回复 ===");
        System.out.println(answer);
        System.out.println("==============");
    }

    @Test
    public void testDeepSeekCapability(){
        String answer = chatModel.chat("""
                请用 Java 写一个冒泡排序算法，并加上注释
                """);
        System.out.println("=== AI 回复 ===");
        System.out.println(answer);
        System.out.println("==============");
    }

    @Test
    public void testMemory(){
        String memoryId = "test_user_001";

        // 第一轮对话
        String reply1= syncAiChatService.chatWithMemory(memoryId, "你好，我叫张三");
        System.out.println("=== AI 回复 ===");
        System.out.println(reply1);
        System.out.println("==============");

        // 第二轮对话
        String reply2= syncAiChatService.chatWithMemory(memoryId, "我叫什么名字？");
        System.out.println("=== AI 回复 ===");
        System.out.println(reply2);
        System.out.println("==============");

        // 第三轮对话
        String reply3= syncAiChatService.chatWithMemory(memoryId, "我刚才说了什么？");
        System.out.println("=== AI 回复 ===");
        System.out.println(reply3);
        System.out.println("==============");
    }

}
