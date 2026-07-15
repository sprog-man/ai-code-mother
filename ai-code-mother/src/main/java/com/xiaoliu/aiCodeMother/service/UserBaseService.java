package com.xiaoliu.aiCodeMother.service;

import com.xiaoliu.aiCodeMother.model.entity.User;

public interface UserBaseService {
    int insertUser(User user);
    int updateUser(User user);
}
