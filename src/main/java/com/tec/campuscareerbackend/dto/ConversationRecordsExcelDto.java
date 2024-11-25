package com.tec.campuscareerbackend.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConversationRecordsExcelDto {
    @ExcelProperty("序号")
    private Integer id;

    @ExcelProperty("谈话时间")
    private String conversationTime;

    @ExcelProperty("院校")
    private String university;

    @ExcelProperty("谈话对象")
    private String conversationTarget;

    @ExcelProperty("谈话人数")
    private Integer participantCount;

    @ExcelProperty("其他谈话主题")
    private String otherTopics;

    @ExcelProperty("谈话主题")
    private String conversationTopic;

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("谈话类型")
    private String conversationType;

    @ExcelProperty("联系家长")
    private String parentContact;

    @ExcelProperty("院系")
    private String department;

    @ExcelProperty("谈话教师")
    private String conversationTeacher;

    @ExcelProperty("谈话地点")
    private String conversationLocation;

    @ExcelProperty("谈话内容")
    private String conversationContent;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("关注等级")
    private String attentionLevel;

    @ExcelProperty("创建时间")
    private String createdAt;

    @ExcelProperty("最后更新时间")
    private String updatedAt;
}
