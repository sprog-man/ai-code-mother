package com.xiaoliu.aiCodeMother.service;

import com.xiaoliu.aiCodeMother.model.entity.App;
import org.springframework.stereotype.Service;


public interface AppService {
    int insertApp(App app);

    int updateApp(App app);
}
