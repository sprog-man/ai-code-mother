package com.xiaoliu.aiCodeMother.mapper;

import com.mybatisflex.core.BaseMapper;
import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import com.xiaoliu.aiCodeMother.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper，自动拥有 CRUD 方法
    // 无需手写 SQL

}
