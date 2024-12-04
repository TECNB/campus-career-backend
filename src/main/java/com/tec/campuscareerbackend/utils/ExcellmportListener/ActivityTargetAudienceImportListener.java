package com.tec.campuscareerbackend.utils.ExcellmportListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tec.campuscareerbackend.dto.ActivityTargetAudienceExcelDto;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ActivityTargetAudienceImportListener extends AnalysisEventListener<ActivityTargetAudienceExcelDto> {

    private final List<ActivityTargetAudienceExcelDto> audienceList;
    private final List<Map<Integer, String>> errorDataList;

    public ActivityTargetAudienceImportListener(List<ActivityTargetAudienceExcelDto> audienceList, List<Map<Integer, String>> errorDataList) {
        this.audienceList = audienceList;
        this.errorDataList = errorDataList;
    }

    @Override
    public void invoke(ActivityTargetAudienceExcelDto dto, AnalysisContext context) {
        Map<Integer, String> errors = new HashMap<>();

        // 校验第 2 列: 年级必填且只能为数字
        if (dto.getAudienceLabel() == null || dto.getAudienceLabel().isEmpty()) {
            errors.put(1, "年级不能为空");
        } else {
            try {
                Integer.parseInt(dto.getAudienceLabel());
            } catch (NumberFormatException e) {
                errors.put(1, "年级只能为数字");
            }
        }

        // 校验第 3 列: 班级必填
        if (dto.getAudienceValue() == null || dto.getAudienceValue().isEmpty()) {
            errors.put(2, "班级不能为空");
        }

        // 校验第 4 列: 专业名称必填且不能包含“专升本”
        if (dto.getMajor() == null || dto.getMajor().isEmpty()) {
            errors.put(3, "专业名称不能为空");
        } else if (dto.getMajor().contains("专升本")) {
            errors.put(3, "专业名称注意不要区分专升本，统一使用一个专业名称");
        }

        // 保存错误信息到 dto
        dto.setErrorMessages(errors);

        // 将 dto 和错误信息添加到列表
        audienceList.add(dto);
        errorDataList.add(errors.isEmpty() ? new HashMap<>() : errors); // 确保每行都有一个 Map
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("ActivityTargetAudience 数据解析完成, 总条数: {}", audienceList.size());
    }
}
