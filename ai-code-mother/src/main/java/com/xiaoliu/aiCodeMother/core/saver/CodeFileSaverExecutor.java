package com.xiaoliu.aiCodeMother.core.saver;

import com.xiaoliu.aiCodeMother.ai.model.HtmlCodeResult;
import com.xiaoliu.aiCodeMother.ai.model.MultiFileCodeResult;

import java.io.File;

/**
 * 代码文件保存器执行器
 */
public class CodeFileSaverExecutor {

    /**
     * 保存 HTML 代码
     */
    public static File saveHtml(HtmlCodeResult codeResult){
        CodeFileSaverTemplate<HtmlCodeResult> saver=new HtmlCodeFileSaverTemplate();
        return saver.saveCode(codeResult);
    }

    /**
     * 保存多结构HTML代码
     */
    public static File saveMultiFile(MultiFileCodeResult result){
        CodeFileSaverTemplate<MultiFileCodeResult> saver=new MultiFileCodeFileSaverTemplate();
        return saver.saveCode(result);
    }

}
