package com.xiaoliu.aiCodeMother.core.cleaner;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * 代码文件定时清理任务
 */
@Component
@Slf4j
public class CodeFileCleanTask {

    // 假设你的生成文件都保存在这个根目录下，请根据实际情况修改！
    private static final String CODE_OUTPUT_ROOT = System.getProperty("java.io.tmpdir") +  File.separator +"code_output";

    /**
     * 每天凌晨 3 点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredFiles(){
        log.info("开始清理 24 小时前的代码生成文件...");

        File rootDir=new File(CODE_OUTPUT_ROOT);
        if (!rootDir.exists() || !rootDir.isDirectory()){
            log.warn("代码输出目录不存在：{}", CODE_OUTPUT_ROOT);
            return;
        }

        //计算24小时的时间戳
        long expireTime= DateUtil.offsetHour(new Date(),-24).getTime();
        int cleanCount=0;

        //遍历根目录下的所有子目录（每个生成任务一个目录）
        File[] subDirs=rootDir.listFiles(File::isDirectory);
        if (subDirs == null) return;;

        for (File dir : subDirs){
            // 判断目录的最后修改时间是否早于 24 小时前
            if (dir.lastModified()<expireTime){
                try {
                    FileUtil.del(dir);
                    cleanCount++;
                    log.info("已清理过期目录：{}", dir.getAbsolutePath());
                } catch (Exception e) {
                    log.error("清理目录失败：{}", dir.getAbsolutePath(), e);
                }
            }
        }
        log.info("清理完成，共删除 {} 个过期目录", cleanCount);

    }


}
