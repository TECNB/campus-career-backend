package com.tec.campuscareerbackend.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ActivityTargetAudienceExcelDto {
    @ExcelProperty("年级")
    private String audienceLabel;

    @ExcelProperty("班级")
    private String audienceValue;
}
