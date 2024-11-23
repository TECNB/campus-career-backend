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
 * @since 2024-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("special_group")
public class SpecialGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String studentId;

    private String mainProblem;

    private String coexistingProblem;

    private String problemDescription;

    private String supportContact;

    private String supportContactPhone;

    private String trackingRecord;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
