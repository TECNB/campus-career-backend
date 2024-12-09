package com.tec.campuscareerbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生求职意向表
 * </p>
 *
 * @author TECNB
 * @since 2024-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_intention")
public class StudentIntention implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 学生联系方式
     */
    private String phone;

    /**
     * 学生学号
     */
    private String studentId;

    /**
     * 公司ID
     */
    private Integer companyId;

    /**
     * 心仪企业名称
     */
    private String companyName;

    /**
     * 企业岗位名称
     */
    private String positionName;

    /**
     * 企业联系方式
     */
    private String hrPhone;

    /**
     * 薪资待遇
     */
    private String money;

    /**
     * 工作地点
     */
    private String area;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;


}
