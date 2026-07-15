package com.xiaoliu.aiCodeMother.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.mapper.AppMapper;
import com.xiaoliu.aiCodeMother.model.entity.App;
import com.xiaoliu.aiCodeMother.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.xiaoliu.aiCodeMother.model.entity.table.AppTableDef.APP;

@RestController
@RequestMapping("/app")
@Tag(name = "应用接口", description = "应用接口")
public class AppController {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppService appService;

    /* 查询所有yingy */
    @GetMapping("/list")
    @Operation(summary = "查询所有应用")
    public BaseResponse<List<App>> listApps(){
        List<App> apps=appMapper.selectAll();
        return ResultUtils.success(apps);
    }

    /* 根据ID查询应用 */
    @PostMapping("/get/{id}")
    @Operation(summary = "根据ID查询应用")
    public BaseResponse<App> getAppById(@PathVariable  Long id){
        App app=appMapper.selectOneById(id);
        return ResultUtils.success(app);
    }

    /* 新增应用 */
    @PostMapping("/add")
    @Operation(summary = "新增应用")
    public BaseResponse<Long> addApp(@RequestBody App app){
        int id=appService.insertApp(app);
        return ResultUtils.success(app.getId());
    }

    /* 更新应用 */
    @PostMapping("/update")
    @Operation(summary = "更新应用")
    public BaseResponse<Boolean> updateApp(@RequestBody App app){
        int rows=appService.updateApp(app);
        return ResultUtils.success(rows > 0);
    }

    /* 删除应用 */
    @PostMapping("/delete/{id}")
    @Operation(summary = "删除应用")
    public BaseResponse<Boolean> deleteApp(@PathVariable Long id){
        int rows=appMapper.deleteById(id);
        return ResultUtils.success(rows > 0);
    }

    /* 根据用户ID查询该用户的应用 */
    @GetMapping("/listByUserId/{userId}")
    @Operation(summary = "根据用户ID查询该用户的应用")
    public BaseResponse<List<App>> listAppsByUserId(@PathVariable Long userId){
        QueryWrapper query=QueryWrapper.create()
                .where(APP.USER_ID.eq(userId))
                .and(APP.IS_DELETE.eq(0));
        List<App> apps=appMapper.selectListByQuery(query);
        return ResultUtils.success(apps);
    }

}
