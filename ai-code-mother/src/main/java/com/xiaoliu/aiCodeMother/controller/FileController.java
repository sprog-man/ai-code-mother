package com.xiaoliu.aiCodeMother.controller;

import com.xiaoliu.aiCodeMother.annotation.AuthCheck;
import com.xiaoliu.aiCodeMother.annotation.OperationLog;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import com.xiaoliu.aiCodeMother.service.UserService;
import com.xiaoliu.aiCodeMother.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传控制器
 *
 * @author xiaoliu
 */
@RestController
@RequestMapping("/file")
@Tag(name = "文件接口", description = "文件上传")
@Slf4j
public class FileController {
    @Value("${file.upload-path}")  //从配置文件中获取上传路径
    private String uploadPath;

    @Value("${file.url-prefix}")  //从配置文件中获取url前缀
    private String urlPrefix;

    @Resource
    private UserService userService;

    @Resource
    private UserBaseService userBaseService;

    // 注入 Redis 模板
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 注入 Lua 脚本，这个是我们在redis配置类中注入的对象
    @Resource
    private DefaultRedisScript<Long> rateLimitScript;

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    @Operation(summary = "上传头像")
    @AuthCheck
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file,HttpServletRequest request){
        // ========== 【新增】Redis + Lua 限流逻辑 ==========
        // 1. 获取当前登录用户（用用户ID作为限流维度，比IP更精准）
        User loginUser = userService.getLoginUser(request);
        String limitKey="ratelimit:upload:" + loginUser.getId();

        // 2. 执行 Lua 脚本：10秒内最多允许上传 3 次
        Long result=stringRedisTemplate.execute(
                rateLimitScript,
                java.util.Collections.singletonList(limitKey), //传入key
                "10", //时间窗口 10s
                "3" //最大次数：3次   //"10", "3"：这些可变参数，对应 Lua 脚本里的 ARGV[1], ARGV[2]。
        );

        // 3. 如果返回 0，说明触发了限流，直接抛出异常
        if (result == null || result == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传太频繁了，请10秒后再试！");
        }
        // ========== 限流逻辑结束 ==========

        // 1. 校验文件
        if (file == null || file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }

        // 2. 校验文件类型
        String contentType = file.getContentType();
        if (!FileUtils.isValidFileType(contentType)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只支持上传图片（jpg、png、gif）");
        }

        // 3. 校验文件大小(10MB)
        long maxSize= 10 * 1024 * 1024;
        if (file.getSize() > maxSize){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        }

        // 4. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String newFilename = FileUtils.generateUniqueFileName(originalFilename);

        // 5. 创建上传目录
        FileUtils.createDirectory(uploadPath);

        // 6. 保存文件
        File destFile=new File(uploadPath+newFilename);
        try{
            file.transferTo(destFile);
            log.info("文件上传成功：{}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }

        // 7. 返回文件访问url
        String fileUrl=urlPrefix+"/"+newFilename;

        // 更新数据库为新头像 URL
        User updateUser = userService.getLoginUser(request);
        updateUser.setUserAvatar(fileUrl);
        userBaseService.updateUser(updateUser);

        return ResultUtils.success(fileUrl);
    }

    /**
     * 用户更新头像
     */
    @PostMapping("/update/avatar")
    @Operation(summary = "更新用户头像")
    @AuthCheck // 需要用户登录
    public BaseResponse<String> updateAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 1. 基础文件校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }
        String contentType = file.getContentType();
        if (!FileUtils.isValidFileType(contentType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只支持上传图片（jpg、png、gif）");
        }
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        }

        // 2. 【核心】从 HTTP 请求中提取出当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 3. 调用 Service 层，把文件丢进去，剩下的事一概不管！
        // 4. 返回新头像的 URL（这里简单拼接返回给前端）
        String newFileUrl = userService.updateUserAvatar(loginUser, file);
        return ResultUtils.success(newFileUrl);
    }
}
