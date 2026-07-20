package com.xiaoliu.aiCodeMother.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;
//@ExcelProperty：指定这一列在 Excel 里显示什么名字。
//@ColumnWidth：指定这一列的宽度，避免内容挤在一起。
@Data
public class UserExcelVO {
    @ExcelProperty(value = "用户ID",index = 0)
    @ColumnWidth(10)
    private Long userId;

    @ExcelProperty(value = "用户账号",index = 1)
    @ColumnWidth(15)
    private String userAccount;

    @ExcelProperty(value = "用户昵称", index = 2)
    @ColumnWidth(15)
    private String userName;

    @ExcelProperty(value = "用户角色", index = 3)
    @ColumnWidth(10)
    private String userRole;

    @ExcelProperty(value = "创建时间", index = 4)
    @ColumnWidth(20)
    private Date createTime;
}
