package com.xiaoliu.aiCodeMother.service;

import com.xiaoliu.aiCodeMother.model.entity.UserOperationLog;

public interface UserOperationLogService {
    // 保存操作日志
    void save(UserOperationLog log);
}
