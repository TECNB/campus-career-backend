package com.tec.campuscareerbackend.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobSearchExcelDto {
    @ExcelProperty("序号")
    private Integer id;

    @ExcelProperty("展位号")
    private String displayId;

    @ExcelProperty("招聘企业")
    private String companyName;

    @ExcelProperty("招聘岗位")
    private String positionName;

    @ExcelProperty("HR名称")
    private String hrName;

    @ExcelProperty("联系电话")
    private String hrPhone;

    @ExcelProperty("所需专业")
    private String majorRequirement;

    @ExcelProperty("招聘人数")
    private Integer participantCount;

    @ExcelProperty("薪资待遇")
    private String money;

    @ExcelProperty("地区")
    private String area;

    @ExcelProperty("网申链接")
    private String applicationLink;

    @ExcelProperty("其他要求")
    private String additionalRequirements;

    @ExcelProperty("企业简介")
    private String companyDescription;
}
