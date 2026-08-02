package com.xiaoliu.aiCodeMother.core;


import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.ai.model.MultiFileCodeResult;
import com.xiaoliu.aiCodeMother.core.saver.CodeFileSaverExecutor;
import com.xiaoliu.aiCodeMother.service.CodeGeneratorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 代码生成门面（Facade）
 * 统一协调：AI 调用 → 代码解析 → 文件保存
 */
@Service
@Slf4j
public class CodeGeneratorFacade {
    @Resource
    private CodeGeneratorService codeGeneratorService;

    /**
     * 生成并保存 HTML 代码
     *
     * @param userPrompt 用户的需求描述
     * @return 保存的目录
     */
    public File generateAndSaveHtml(String userPrompt){
        log.info("开始生成 HTML 代码，用户需求：{}", userPrompt);

        // 1. AI调用→ 直接得到结构化结果（第 2 天已实现）
        long startTime=System.currentTimeMillis();
        HtmlCodeResult result = codeGeneratorService.generateHtmlCode(userPrompt);
        long aiDuration=System.currentTimeMillis()-startTime;
        log.info("AI 调用完成，耗时：{}ms", aiDuration);

        // 2. 保存文件
        File savedFile = CodeFileSaverExecutor.saveHtml(result);
        log.info("文件保存完成，保存路径：{}", savedFile.getAbsolutePath());

        return savedFile;
    }

    /**
     * 生成并保存多文件代码
     */
    public File generateAndSaveMultiFile(String userPrompt){
        log.info("开始生成多文件代码，用户需求：{}", userPrompt);

        long startTime=System.currentTimeMillis();
        MultiFileCodeResult result=codeGeneratorService.generateMultiFileCode(userPrompt);
        long aiDuration=System.currentTimeMillis()-startTime;
        log.info("AI 调用完成，耗时：{}ms", aiDuration);
        return CodeFileSaverExecutor.saveMultiFile(result);
    }


}
