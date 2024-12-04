package com.tec.campuscareerbackend.utils.ExcellmportListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tec.campuscareerbackend.dto.ConversationRecordsExcelDto;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConversationRecordsImportListener extends AnalysisEventListener<ConversationRecordsExcelDto> {

    private final List<ConversationRecordsExcelDto> recordList;
    private final List<Map<Integer, String>> errorDataList;

    public ConversationRecordsImportListener(List<ConversationRecordsExcelDto> recordList, List<Map<Integer, String>> errorDataList) {
        this.recordList = recordList;
        this.errorDataList = errorDataList;
    }

    @Override
    public void invoke(ConversationRecordsExcelDto dto, AnalysisContext context) {
        Map<Integer, String> errors = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");

        // 校验第 2 列: 谈话时间
        if (dto.getConversationTime() == null || dto.getConversationTime().isEmpty()) {
            errors.put(1, "谈话时间不能为空");
        } else {
            try {
                dateFormat.parse(dto.getConversationTime());
            } catch (ParseException e) {
                errors.put(1, "谈话时间格式错误，需为 yyyy/M/d");
            }
        }

        // 校验第 3 列: 院校
        if (dto.getUniversity() == null || dto.getUniversity().isEmpty()) {
            errors.put(2, "院校不能为空");
        }

        // 校验第 4 列: 谈话对象
        if (dto.getConversationTarget() == null || dto.getConversationTarget().isEmpty()) {
            errors.put(3, "谈话对象不能为空");
        }

        // 校验第 5 列: 谈话人数
        if (dto.getParticipantCount() == null) {
            errors.put(4, "谈话人数不能为空");
        } else if (dto.getParticipantCount() == 0) {
            errors.put(4, "谈话人数必须为正整数");
        }

        // 校验第 8 列: 学号
        if (dto.getStudentId() == null || dto.getStudentId().isEmpty()) {
            errors.put(7, "学号不能为空");
        }

        // 校验第 9 列: 谈话类型
        if (dto.getConversationType() == null || dto.getConversationType().isEmpty()) {
            errors.put(8, "谈话类型不能为空");
        }

        // 校验第 11 列: 院系
        if (dto.getDepartment() == null || dto.getDepartment().isEmpty()) {
            errors.put(10, "院系不能为空");
        }

        // 校验第 12 列: 谈话教师
        if (dto.getConversationTeacher() == null || dto.getConversationTeacher().isEmpty()) {
            errors.put(11, "谈话教师不能为空");
        }

        // 校验第 13 列: 谈话地点
        if (dto.getConversationLocation() == null || dto.getConversationLocation().isEmpty()) {
            errors.put(12, "谈话地点不能为空");
        }

        // 校验时间列: 创建时间、最后更新时间
        validateDateField(dto.getCreatedAt(), 15, "创建时间", dateFormat, errors);
        validateDateField(dto.getUpdatedAt(), 16, "最后更新时间", dateFormat, errors);

        // 保存错误信息到 dto
        dto.setErrorMessages(errors);

        // 将 dto 和错误信息添加到列表
        recordList.add(dto);
        errorDataList.add(errors.isEmpty() ? new HashMap<>() : errors);
    }

    private void validateDateField(String dateField, int columnIndex, String columnName, SimpleDateFormat dateFormat, Map<Integer, String> errors) {
        if (dateField != null && !dateField.isEmpty()) {
            try {
                dateFormat.parse(dateField);
            } catch (ParseException e) {
                errors.put(columnIndex, columnName + "格式错误，需为 yyyy/M/d");
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 全部解析完成后的处理
        log.info("所有数据解析完成，共处理 {} 条记录", recordList.size());
    }
}
