package com.xiaoliu.aiCodeMother.core.saver;


import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.util.UUID;

/**
 * 代码文件保存器模板
 *
 * @param <T> 代码结果类型
 */
@Slf4j
public abstract class CodeFileSaverTemplate<T> {

    /**
     * 保存代码文件（模板方法）
     *
     * @param codeResult 代码结果对象
     * @return 保存的文件
     */
    public File saveCode(T codeResult){
        // 1. 校验参数(固定)
        validate(codeResult);

        // 2. 创建唯一目录(固定)
        String dirName="html_"+ UUID.randomUUID().toString().substring(0,8);
        File dir= FileUtil.mkdir(getBasePath()+File.separator+dirName);
        log.info("创建代码目录：{}", dir.getAbsolutePath());

        // 3. 写入文件（由子类实现）
        saveFiles(codeResult,dir);

        // 4. 返回目录(固定)
        log.info("代码保存完成，目录：{}", dir.getAbsolutePath());
        return dir;
    }

    /**
     * 获取基础保存路径
     */
    protected String getBasePath(){
        // 保存到系统临时目录下的 code_output 文件夹
        return System.getProperty("java.io.tmpdir") + File.separator + "code_output";
    }


    /**
     * 校验参数（子类可重写）
     */
    protected void validate(T codeResult) {
        if (codeResult == null){
            throw new IllegalArgumentException("代码结果对象不能为空");
        }
    }

    /**
     * 保存具体文件（由子类实现）
     */
    protected abstract void saveFiles(T codeResult, File dir);
}
