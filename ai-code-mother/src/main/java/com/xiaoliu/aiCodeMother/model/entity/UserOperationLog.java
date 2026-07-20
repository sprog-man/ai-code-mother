package com.xiaoliu.aiCodeMother.model.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("user_operation_log") // 对应数据库表名
public class UserOperationLog {

    private Long id;
    private Long operatorId;      // 操作人ID
    private String operatorName;  // 操作人昵称
    private String operationDesc; // 操作描述
    private String methodName;    // 调用的方法名
    private String requestParams; // 请求参数
    private LocalDateTime operateTime; // 操作时间
    private Long costTime;        // 执行耗时(ms)
}
