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
 * 谈话记录表
 * </p>
 *
 * @author TECNB
 * @since 2024-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("conversation_records")
public class ConversationRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 谈话时间
     */
    private LocalDateTime conversationTime;

    /**
     * 院校
     */
    private String university;

    /**
     * 谈话对象
     */
    private String conversationTarget;

    /**
     * 谈话人数
     */
    private Integer participantCount;

    /**
     * 其他谈话主题
     */
    private String otherTopics;

    /**
     * 谈话主题
     */
    private String conversationTopic;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 谈话类型
     */
    private String conversationType;

    /**
     * 联系家长
     */
    private String parentContact;

    /**
     * 院系
     */
    private String department;

    /**
     * 谈话教师
     */
    private String conversationTeacher;

    /**
     * 谈话地点
     */
    private String conversationLocation;

    /**
     * 谈话内容
     */
    private String conversationContent;

    /**
     * 状态
     */
    private String status;

    /**
     * 关注等级
     */
    private String attentionLevel;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;


}
