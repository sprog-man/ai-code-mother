package com.xiaoliu.aiCodeMother.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import com.xiaoliu.aiCodeMother.mapper.UserMapper;
import com.xiaoliu.aiCodeMother.model.entity.User;
import com.xiaoliu.aiCodeMother.service.UserBaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserBaseServiceImpl implements UserBaseService {

    @Resource
    private UserMapper userMapper;

    @AutoFill(OperationType.INSERT)
    @Override
    public int insertUser(User user) {
        return userMapper.insert(user);
    }

    @AutoFill(OperationType.UPDATE)
    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public int removeUser(QueryWrapper queryWrapper) {
        return userMapper.deleteByQuery(queryWrapper);
    }
}
