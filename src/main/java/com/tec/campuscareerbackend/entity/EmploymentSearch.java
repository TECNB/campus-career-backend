package com.tec.campuscareerbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("employment_search")
public class EmploymentSearch implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 班级
     */
    private String className;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 联系方式
     */
    private String contactNumber;

    /**
     * 班主任
     */
    private String classTeacher;

    /**
     * 毕业设计导师
     */
    private String graduationTutor;

    /**
     * 毕业意向
     */
    private String futurePlan;

    /**
     * 薪资待遇
     */
    private String salary;

    /**
     * 公司性质
     */
    private String companyNature;

    /**
     * 工作地点
     */
    private String workLocation;

    /**
     * 就业情况
     */
    private String employmentStatus;

    /**
     * 实习/签约单位
     */
    private String companyName;

    private LocalDateTime createdAt;

    @TableField(exist = false)
    private UserDetail userDetail;
}
