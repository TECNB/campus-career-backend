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
 * 
 * </p>
 *
 * @author TECNB
 * @since 2024-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_detail")
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
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
     * 创建时间
     */
    private LocalDateTime createdAt;


}
