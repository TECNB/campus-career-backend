package com.tec.campuscareerbackend.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ActivityTargetAudienceExcelDto {
    @ExcelProperty("序号")
    private Integer id;

    @ExcelProperty("年级")
    private String audienceLabel;

    @ExcelProperty("班级")
    private String audienceValue;

    @ExcelProperty("专业名称")
    private String major;
}
