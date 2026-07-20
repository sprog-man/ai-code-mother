package com.xiaoliu.aiCodeMother.service.impl;

import com.xiaoliu.aiCodeMother.mapper.UserOperationLogMapper;
import com.xiaoliu.aiCodeMother.model.entity.UserOperationLog;
import com.xiaoliu.aiCodeMother.service.UserOperationLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserOperationLogServiceImpl implements UserOperationLogService {

    @Resource
    private UserOperationLogMapper userOperationLogMapper;

    @Override
    public void save(UserOperationLog log) {
        userOperationLogMapper.insert(log);
    }
}
