package com.xiaoliu.aiCodeMother.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.core.CodeGeneratorFacade;
import com.xiaoliu.aiCodeMother.model.dto.codegen.CodeGenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    /**
     * 将下载文件修改为 zip 文件
     */
    @PostMapping("/download-zip")
    @Operation(summary = "生成并下载 ZIP 压缩包")
    @ApiResponse(responseCode = "200", description = "生成并下载 ZIP 压缩包成功",
            content = @Content(mediaType = "application/zip"))
    public void downloadZip(@RequestBody CodeGenRequest request,
                            HttpServletResponse response) throws IOException{
        String message=request.getMessage();
        if (message==null || message.isBlank()){
            response.sendError(400, "需求描述不能为空");
            return;
        }

        // 1. 生成代码并保存到目录
        File saveDir=codeGeneratorFacade.generateAndSaveHtml(message);

        // 2.创建临时zip文件
        File zipFile=File.createTempFile("codegen", ".zip");

        try {
            // 3.压缩目录包括所有子文件
            ZipUtil.zip(saveDir, true,   zipFile);

            // 【优化点 2】设置更友好的文件名（去掉特殊字符，防止乱码）
            String fileName = "project_" + System.currentTimeMillis() + ".zip";

            // 4.设置下载响应头
            response.setContentType("application/zip");
            // 注意：URLEncoder.encode 是为了防止中文文件名在某些浏览器乱码
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");

            // 5. 将 ZIP 文件写入响应流
            FileUtil.writeToStream(zipFile, response.getOutputStream());
            response.getOutputStream().flush();
        } finally {
            // 6. 无论成功失败，必须删除临时文件
            FileUtil.del(zipFile);
            // 注意：不删除 saveDir，由定时清理任务处理

        }

    }

}
