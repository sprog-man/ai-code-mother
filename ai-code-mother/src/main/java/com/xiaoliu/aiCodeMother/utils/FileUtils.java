package com.xiaoliu.aiCodeMother.utils;

import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author xiaoliu
 */
@Slf4j
public class FileUtils {
    /**
     * 允许上传的图片类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES ={"image/jpeg", "image/jpg", "image/png", "image/gif"};

    /**
     * 校验文件类型
     *
     * @param contentType 文件类型
     * @return 是否合法
     */
    public static boolean isValidFileType(String contentType) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 新文件名
     */
    public static String generateUniqueFileName(String originalFilename){
        // 获取文件扩展名
        String extension="";
        if (originalFilename != null && originalFilename.contains(".")){
            extension=originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名：UUID + 扩展名
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     */
    public static void createDirectory(String dirPath) {
        // TODO: 创建目录的逻辑
        File dir=new File(dirPath);
        if (!dir.exists()){
            boolean created=dir.mkdirs();
            if (!created){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建目录失败");
            }
        }
    }

    /**
     * 删除本地文件
     *
     * @param filePath 文件的绝对路径（例如：E:/upload/avatar/xxx.jpg）
     */
    public static void deleteFile(String filePath){
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("文件删除成功：{}", filePath);
            } else {
                // 删除失败通常是因为文件被占用或权限不足，记录个警告即可，不要抛异常打断主流程
                log.warn("文件删除失败（可能被占用或无权限）：{}", filePath);
            }
        }
    }


}
