package com.xiaoliu.aiCodeMother.service.impl;

import com.xiaoliu.aiCodeMother.annotation.AutoFill;
import com.xiaoliu.aiCodeMother.annotation.OperationType;
import com.xiaoliu.aiCodeMother.mapper.AppMapper;
import com.xiaoliu.aiCodeMother.model.entity.App;
import com.xiaoliu.aiCodeMother.service.AppService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    /* 新增用户 */
    @AutoFill(OperationType.INSERT)
    @Override
    public int insertApp(App app) {
        return appMapper.insert(app);
    }

    /* 更新应用 */
    @AutoFill(OperationType.UPDATE)
    @Override
    public int updateApp(App app) {
        return appMapper.update(app);
    }


}
