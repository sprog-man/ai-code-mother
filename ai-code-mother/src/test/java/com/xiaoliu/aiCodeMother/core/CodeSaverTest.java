package com.xiaoliu.aiCodeMother.core;

import cn.hutool.core.io.FileUtil;
import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.core.parser.CodeParser;
import com.xiaoliu.aiCodeMother.core.parser.CodeParserExecutor;
import com.xiaoliu.aiCodeMother.core.parser.HtmlCodeParser;
import com.xiaoliu.aiCodeMother.core.saver.CodeFileSaverExecutor;
import com.xiaoliu.aiCodeMother.service.CodeGeneratorService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * 代码保存器测试
 */
@SpringBootTest
public class CodeSaverTest {
    @Resource
    private CodeGeneratorService codeGeneratorService;

    @Test
    public void testParseHtmlCodeBlock() throws Exception{
        String prompt="帮我生成一个登陆界面";
        HtmlCodeResult result=codeGeneratorService.generateHtmlCode(prompt);

        System.out.println("======== ai回复 ========");
        System.out.println(result);

        // 【加这一行】：看看 AI 返回的原始 JSON 到底长什么样
        System.out.println("=== AI 返回的 JSON ===");
        System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result));

        // 保存到文件
        File dir= CodeFileSaverExecutor.saveHtml(result);
        System.out.println("文件保存到：" + dir.getAbsolutePath());

        // 验证文件是否存在
        File htmlFile = new File(dir, "index.html");
        assert htmlFile.exists() : "index.html 文件应该存在";
        assert htmlFile.length() > 0 : "文件内容不能为空";

        // 打印保存的内容
        String content = FileUtil.readUtf8String(htmlFile);
        System.out.println("=== 保存的内容 ===");
        System.out.println(content);




    }

}
