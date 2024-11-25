package com.tec.campuscareerbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author TECNB
 * @since 2024-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机
     */
    private String phone;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 年级
     */
    private String grade;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 班级职务
     */
    private String classRole;

    /**
     * 专业方向
     */
    private String specialization;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 入学日期
     */
    private LocalDate admissionDate;

    /**
     * 预计毕业时间
     */
    private LocalDate expectedGraduation;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 生源地
     */
    private String sourcePlace;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 户口所在地
     */
    private String residence;

    /**
     * 家庭住址
     */
    private String homeAddress;

    /**
     * 辅导员姓名
     */
    private String counselor;

    /**
     * 辅导员手机号
     */
    private String counselorPhone;

    /**
     * 班主任姓名
     */
    private String classTeacher;

    /**
     * 班主任手机号
     */
    private String classTeacherPhone;

    /**
     * 毕设导师姓名
     */
    private String graduationTutor;

    /**
     * 毕设导师手机号
     */
    private String graduationTutorPhone;

    /**
     * 寝室号
     */
    private String dormitoryNumber;

    /**
     * 红旗网络
     */
    private String networkStatus;

    /**
     * 寝室成员名单
     */
    private String dormitoryMembers;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 入党进度
     */
    private String partyProgress;

    /**
     * 入党培训进度
     */
    private String partyTrainingProgress;

    /**
     * 所在支部
     */
    private String branchName;

    /**
     * 入党申请时间
     */
    private LocalDate applicationDate;

    /**
     * 入党积极分子时间
     */
    private LocalDate activistDate;

    /**
     * 发展对象时间
     */
    private LocalDate developmentDate;

    /**
     * 预备党员时间
     */
    private LocalDate probationaryDate;

    /**
     * 党员转正时间
     */
    private LocalDate fullMemberDate;

    /**
     * 党建工时
     */
    private Integer partyHours;

    /**
     * 党支部书记姓名
     */
    private String branchSecretary;

    /**
     * 党支部副书记姓名
     */
    private String branchDeputySecretary;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
