package com.xiaoliu.aiCodeMother.core.saver;


import cn.hutool.core.io.FileUtil;
import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;

import java.io.File;

/**
 * HTML 代码文件保存器
 * 将 HtmlCodeResult 保存为 index.html 文件
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected void saveFiles(HtmlCodeResult codeResult, File dir){
        String htmlCode=codeResult.getHtmlCode();

        if (htmlCode==null || htmlCode.isBlank()){
            throw new IllegalArgumentException("HTML 代码不能为空");
        }

        // 写入 index.html
        File htmlFile=new File(dir, "index.html");
        FileUtil.writeString(htmlCode, htmlFile, "utf-8");
    }

}
