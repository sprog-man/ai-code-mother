package com.xiaoliu.aiCodeMother.core;

import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.core.parser.CodeParserExecutor;
import com.xiaoliu.aiCodeMother.core.parser.HtmlCodeParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 代码解析器测试
 */
@SpringBootTest
public class CodeParserTest {

    @Test
    public void testCodeParser() {
        // 使用传统的字符串拼接，彻底杜绝文本块缩进问题！
        String aiResponse = "好的，这是为您生成的登录页面 HTML 代码：\n\n" +
                "```html\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>登录页面</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>登录</h1>\n" +
                "</body>\n" +
                "</html>\n" +
                "```\n\n" +
                "如果您需要修改任何部分，请告诉我！";

        HtmlCodeResult result = CodeParserExecutor.parseHtml(aiResponse);
        // 【加这一行】：直接打印原始字符串，看看换行符对不对
        System.out.println("=== 描述 ===");
        System.out.println(result.getDescription());
        System.out.println("=== 提取的 HTML 代码 ===");
        System.out.println(result.getHtmlCode());
    }

    @Test
    public void testFourBackticks() {
        // 测试4个反引号能否也正确截取出代码块
        String aiResponse = "这是代码：\n````html\n<h1>Hello</h1>\n````\n结束";
        HtmlCodeResult result = CodeParserExecutor.parseHtml(aiResponse);
        System.out.println("4个反引号 - 描述: " + result.getDescription());
        System.out.println("4个反引号 - 代码: " + result.getHtmlCode());
    }

    @Test
    public void testNoCodeBlock() {
        // 测试没有代码块，直接以 <!DOCTYPE html> 开头
        String aiResponse = "<!DOCTYPE html>\n<html>\n<body>\n<h1>直接输出</h1>\n</body>\n</html>";
        HtmlCodeResult result = new HtmlCodeParser().parseCode(aiResponse);
        System.out.println("无代码块 - 描述: [" + result.getDescription() + "]");
        System.out.println("无代码块 - 代码: " + result.getHtmlCode());
    }
}
