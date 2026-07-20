package com.xiaoliu.aiCodeMother.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 配置静态资源访问
 * @author xiaoliu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.url-prefix}")
    private String urlPrefix;

    /**
     * 配置静态资源映射
     * 将 /api/file/avatar/** 映射到本地文件目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(urlPrefix + "/**")
                .addResourceLocations("file:" + uploadPath);
    }

}
