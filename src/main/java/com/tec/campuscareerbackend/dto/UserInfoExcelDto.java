package com.tec.campuscareerbackend.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.tec.campuscareerbackend.utils.CustomIntegerConverter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UserInfoExcelDto {

    @ExcelProperty("序号")
    private Integer id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("手机号码")
    private String phone;

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("年级")
    private String grade;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("班级职务")
    private String classRole;

    @ExcelProperty("专业方向")
    private String specialization;

    @ExcelProperty("出生日期")
    private String birthDate;

    @ExcelProperty("入学日期")
    private String admissionDate;

    @ExcelProperty("预计毕业时间")
    private String expectedGraduation;

    @ExcelProperty("籍贯")
    private String nativePlace;

    @ExcelProperty("生源地")
    private String sourcePlace;

    @ExcelProperty("民族")
    private String ethnicity;

    @ExcelProperty("户口所在地")
    private String residence;

    @ExcelProperty("家庭地址")
    private String homeAddress;

    @ExcelProperty("辅导员")
    private String counselor;

    @ExcelProperty("辅导员手机号")
    private String counselorPhone;

    @ExcelProperty("班主任")
    private String classTeacher;

    @ExcelProperty("班主任手机号")
    private String classTeacherPhone;

    @ExcelProperty("毕设导师")
    private String graduationTutor;

    @ExcelProperty("毕设导师手机号")
    private String graduationTutorPhone;

    @ExcelProperty("寝室号")
    private String dormitoryNumber;

    @ExcelProperty("红旗网格")
    private String networkStatus;

    @ExcelProperty("寝室成员名单")
    private String dormitoryMembers;

    @ExcelProperty("政治面貌")
    private String politicalStatus;

    @ExcelProperty("入党进度")
    private String partyProgress;

    @ExcelProperty("入党培训进度")
    private String partyTrainingProgress;

    @ExcelProperty("所在支部")
    private String branchName;

    @ExcelProperty("入党申请时间")
    private String applicationDate;

    @ExcelProperty("入党积极分子时间")
    private String activistDate;

    @ExcelProperty("发展对象时间")
    private String developmentDate;

    @ExcelProperty("预备党员时间")
    private String probationaryDate;

    @ExcelProperty("党员转正时间")
    private String fullMemberDate;

    @ExcelProperty(value = "党建工时", converter = CustomIntegerConverter.class)
    private Integer partyHours;

    @ExcelProperty("党支部书记姓名")
    private String branchSecretary;

    @ExcelProperty("党支部副书记姓名")
    private String branchDeputySecretary;

    @ExcelIgnore
    private Map<Integer, String> errorMessages = new HashMap<>();
}
