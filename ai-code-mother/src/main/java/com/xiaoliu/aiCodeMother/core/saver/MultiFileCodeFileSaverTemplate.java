package com.xiaoliu.aiCodeMother.core.saver;


import cn.hutool.core.io.FileUtil;
import com.xiaoliu.aiCodeMother.ai.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 多文件代码保存器
 * 将 MultiFileCodeResult 保存为 index.html + style.css + script.js
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {

    @Override
    protected void saveFiles(MultiFileCodeResult codeResult, File dir) {
        saveIfNotBlank(dir, "index.html", codeResult.getHtmlCode());
        saveIfNotBlank(dir, "style.css", codeResult.getCssCode());
        saveIfNotBlank(dir, "script.js", codeResult.getJsCode());
    }

    private void saveIfNotBlank(File dir,String fileName,String content){
        if (content!=null&& !content.isBlank()){
            File file=new File(dir, fileName);
            FileUtil.writeString(content, file, StandardCharsets.UTF_8);
        }
    }


}
