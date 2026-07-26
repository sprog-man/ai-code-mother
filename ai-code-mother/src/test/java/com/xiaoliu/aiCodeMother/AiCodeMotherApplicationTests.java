package com.xiaoliu.aiCodeMother;

import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.service.CodeGeneratorService;
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

    @Resource
    private CodeGeneratorService codeGeneratorService;
    @Test  //提示词版本B的测试
    public void testGenerateHtml(){
        String prompt="生成一个登录页面";

        HtmlCodeResult result = codeGeneratorService.generateHtmlCode(prompt);
        System.out.println("=== AI 回复 ===");
        System.out.println(result);
        System.out.println("===============");
    }

    @Test  //提示词版本C的测试
    public void testGenerateHtmlC(){
        String prompt="生成一个登录页面";

        String result = codeGeneratorService.generateHtmlCodeC(prompt);
        System.out.println("=== AI 回复 ===");
        System.out.println(result);
        System.out.println("===============");
    }

    @Test  //提示词版本A的测试
    public void testGenerateHtmlA(){
        String prompt="生成一个登录页面";

        String result = codeGeneratorService.generateHtmlCodeA(prompt);
        System.out.println("=== AI 回复 ===");
        System.out.println(result);
        System.out.println("===============");
    }

    @Test
    public void testGenerateHtmlCode(){
        String prompt="生成一个登录页面";

        HtmlCodeResult result = codeGeneratorService.generateHtmlCode(prompt);
        System.out.println("=== AI 回复 ===");
        System.out.println("=== 1. 打印整个对象 ===");
        System.out.println(result); // 看看 Lombok @Data 生成的 toString()
        System.out.println("=== 2. 提取的 HTML 代码 ===");
        System.out.println(result.getHtmlCode());
        System.out.println("=== 3. AI 给出的说明 ===");
        System.out.println(result.getDescription()); // 这里会有 AI 对这段代码的自然语言解释
        System.out.println("===============");
    }


}
