package com.xiaoliu.aiCodeMother.controller;


/*
* 测试控制器
*
* */

import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Tag(name = "测试接口",description = "用于测试系统功能的接口")
public class TestController {

    @GetMapping("/success")
    @Operation(summary = "测试成功响应", description = "返回成功的统一响应格式")
    public BaseResponse<String> success(){
        return ResultUtils.success("操作成功");
    }

    @GetMapping("/error")
    @Operation(summary = "测试失败响应", description = "返回失败的统一响应格式")
    public BaseResponse<String> error(){
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    @GetMapping("/time")
    public String time(){
        return "当前时间:"+new Date();
    }

    /*创建 `/test/info` 返回项目信息（JSON格式）*/
    @GetMapping("/info")
    public BaseResponse<Map<String,String>> getInfo(){
        Map<String,String> info = new HashMap<>();
        info.put("name","AI代码生成平台");
        info.put("version","1.0.0");
        info.put("author","xiaoliu");
        return ResultUtils.success(info);
    }

    /*测试异常处理*/
    @GetMapping("/business-error")
    @Operation(summary = "测试业务异常", description = "模拟业务异常被全局拦截")
    public BaseResponse<String> testBusinessError(){
        //模拟业务异常：用户未登录
        throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
    }

    @GetMapping("/system-error")
    public BaseResponse<String> testSystemError(){
        //模拟系统异常:空指针
        String str=null;
        str.length(); // 这里会抛出 NullPointerException
        return ResultUtils.success("不会执行到这里");
    }

    @GetMapping("/divide")
    @Operation(summary = "测试除法运算", description = "计算两个数的商")
    public BaseResponse<Integer> divide(@Parameter(description = "被除数", required = true) @RequestParam Integer a,
                                        @Parameter(description = "除数", required = true) @RequestParam Integer b ){
        if (b == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(a / b);
    }

    /*前端教程作业：新增接口进行前后端联调*/
    @GetMapping("/hello")
    public BaseResponse<String> helloTest(@RequestParam String name){
        return ResultUtils.success("hello"+name);
    }

}
