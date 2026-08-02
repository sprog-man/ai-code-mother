package com.xiaoliu.aiCodeMother.controller;


import cn.hutool.core.io.FileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * 静态资源预览 Controller
 * 提供生成文件的浏览器预览能力
 */
@RestController
@RequestMapping("/preview")
@Slf4j
@Tag(name = "静态资源预览", description = "提供生成文件的浏览器预览能力")
public class StaticResourceController {

    /**
     * 预览生成的页面
     *
     * @param dirName 目录名（从 /codegen/generate 返回的路径中获取）
     */
    @GetMapping("/{dirName}/**")
    public void preview(@PathVariable String dirName,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException{

        // 构建文件路径
        String basePath=System.getProperty("java.io.tmpdir")
                + File.separator + "code_output" + File.separator + dirName;

        // 获取请求的文件路径 （/** 通配符匹配的子路径）
        String requestPath=request.getRequestURI();
        // 构建出相对路径，说白了也就是拿到文件名
        String relativePath=requestPath.replace("/preview/"+dirName, "");

        // 构建文件绝对路径
        File file=new File(basePath,relativePath);

        if (!file.exists() || file.isDirectory()){
            // 如果请求的是目录本身，返回 index.html
            file=new File(basePath,"index.html");
        }

        if (!file.exists()){
            response.sendError(404,"文件不存在");
            return;
        }

        // 根据文件扩展名设置Content-Type
        String fileName=file.getName();
        if (fileName.endsWith(".html")){
            response.setContentType("text/html;charset=UTF-8");
        } else if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=UTF-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("application/javascript;charset=UTF-8");
        }

        //写入文件内容到通信管道中
        FileUtil.writeToStream(file, response.getOutputStream());

    }
}
