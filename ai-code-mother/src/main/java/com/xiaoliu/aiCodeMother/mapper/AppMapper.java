package com.xiaoliu.aiCodeMother.mapper;

import com.mybatisflex.core.BaseMapper;
import com.xiaoliu.aiCodeMother.model.entity.App;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用Mapper
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {
    // 继承 BaseMapper，自动拥有 CRUD 方法
    // 无需手写 SQL
}
