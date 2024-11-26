package com.tec.campuscareerbackend.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "student_id")
    private String studentId;

    private String username;

    private String passwordHash;

    private String userType;

    private String avatarUrl;

    private String token;

    private String salt;

    private String lastLogin;

    private String phone;

    private String createdAt;

    private String updatedAt;


}
