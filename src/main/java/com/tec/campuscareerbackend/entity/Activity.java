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
 * 活动信息表
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 活动内容
     */
    private String category;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    private String place;

    /**
     * 活动人数
     */
    private Integer participantCount;

    /**
     * 薪资待遇
     */
    private String money;

    /**
     * 公司性质
     */
    private String nature;

    /**
     * 工作地点
     */
    private String area;

    /**
     * 招聘岗位
     */
    private String jobPosition;

    /**
     * 网申链接
     */
    private String applicationLink;

    /**
     * 发送人群
     */
    private String targetAudience;

    /**
     * 活动图片链接
     */
    private String activityImage;

    /**
     * 活动详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
