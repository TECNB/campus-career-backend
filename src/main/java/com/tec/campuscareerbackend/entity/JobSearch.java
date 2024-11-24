package com.tec.campuscareerbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 岗位发布详情表
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("job_search")
public class JobSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * HR名称
     */
    private String hrName;

    /**
     * 联系电话
     */
    private String hrPhone;

    /**
     * 专业要求
     */
    private String majorRequirement;

    /**
     * 招聘人数
     */
    private Integer participantCount;

    /**
     * 薪资待遇
     */
    private String money;

    /**
     * 工作地点
     */
    private String area;

    /**
     * 网申链接
     */
    private String applicationLink;

    /**
     * 其他要求
     */
    private String additionalRequirements;

    /**
     * 企业简介
     */
    private String companyDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 匹配程度
     */
    @TableField(exist = false)
    private String matchLevel;

}
