package com.tec.campuscareerbackend.dto;

import lombok.Data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserDetailExcelDto {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("所在班级")
    private String className;

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("手机号码")
    private String contactNumber;

    @ExcelProperty("班主任")
    private String classTeacher;

    @ExcelProperty("毕业设计导师")
    private String graduationTutor;
}
