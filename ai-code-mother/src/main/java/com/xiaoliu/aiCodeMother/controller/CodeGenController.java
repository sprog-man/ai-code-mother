package com.xiaoliu.aiCodeMother.controller;


import cn.hutool.core.io.FileUtil;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.core.CodeGeneratorFacade;
import com.xiaoliu.aiCodeMother.model.dto.codegen.CodeGenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * 代码生成 Controller
 */
@RestController
@RequestMapping("/codegen")
@Slf4j
@Tag(name = "AI代码生成接口")
public class CodeGenController {

    @Resource
    private CodeGeneratorFacade codeGeneratorFacade;

    /**
     * 生成并返回 HTML 内容
     */
    @PostMapping("/generate")
    @Operation(summary = "生成并返回 HTML 内容")
    public BaseResponse<String> generate(@RequestBody CodeGenRequest request){
        String message= request.getMessage();
        if (message == null || message.isBlank()){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "需求描述不能为空");
        }

        // 生成并保存
        File saveDir = codeGeneratorFacade.generateAndSaveHtml(message);

        // 读取生成的HTML内容返回
        File htmlFile=new File(saveDir, "index.html");
        String htmlContent = FileUtil.readUtf8String(htmlFile);
        return ResultUtils.success(htmlContent);
    }

    /**
     * 生成并下载 HTML 文件
     */
    @PostMapping("/download")
    @Operation(summary = "生成并下载 HTML 文件")
    public void download(@RequestBody CodeGenRequest request,
                         HttpServletResponse response) throws IOException {
        String message= request.getMessage();
        if (message == null || message.isBlank()){
            response.sendError(400, "需求描述不能为空");
            return;
        }

        // 生成并保存为文件
        File saveDir=codeGeneratorFacade.generateAndSaveHtml(message);
        File htmlFile=new File(saveDir, "index.html");

        //设置下载响应头
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"index.html\"");

        // 写入响应
        FileUtil.writeToStream(htmlFile, response.getOutputStream());
    }
}
