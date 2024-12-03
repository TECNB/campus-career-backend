package com.tec.campuscareerbackend.utils.ExcellmportListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tec.campuscareerbackend.dto.JobSearchExcelDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JobSearchExcelImportListener extends AnalysisEventListener<JobSearchExcelDto> {

    private final List<JobSearchExcelDto> jobList;
    private final List<Map<Integer, String>> errorDataList;

    public JobSearchExcelImportListener(List<JobSearchExcelDto> jobList, List<Map<Integer, String>> errorDataList) {
        this.jobList = jobList;
        this.errorDataList = errorDataList;
    }

    @Override
    public void invoke(JobSearchExcelDto dto, AnalysisContext context) {
        Map<Integer, String> errors = new HashMap<>();

        // 校验第 3 列: 招聘企业
        if (dto.getCompanyName() == null || dto.getCompanyName().isEmpty()) {
            errors.put(2, "招聘企业不能为空");
        }

        // 校验第 4 列: 招聘岗位
        if (dto.getPositionName() == null || dto.getPositionName().isEmpty()) {
            errors.put(3, "招聘岗位不能为空");
        }

        // 校验第 7 列: 所需专业
        if (dto.getMajorRequirement() == null || dto.getMajorRequirement().isEmpty()) {
            errors.put(6, "所需专业不能为空");
        } else if (!Pattern.matches("^[\\w/\\u4e00-\\u9fa5]+$", dto.getMajorRequirement())) {
            errors.put(6, "所需专业必须使用“/”分割");
        }

        // 校验第 8 列: 招聘人数
        if (dto.getParticipantCount() == null) {
            errors.put(7, "招聘人数不能为空");
        }

        // 校验第 9 列: 薪资待遇
        if (dto.getMoney() == null || dto.getMoney().isEmpty()) {
            errors.put(8, "薪资待遇不能为空");
        } else {
            List<String> validSalaries = Arrays.asList("2000-5000", "5000-8000", "8000-15000", "15000以上","面议");
            if (!validSalaries.contains(dto.getMoney())) {
                errors.put(8, "薪资待遇格式错误，仅支持以下格式: 2000-5000, 5000-8000, 8000-15000, 15000以上, 面议");
            }
        }

        // 校验第 10 列: 地区
        if (dto.getArea() == null || dto.getArea().isEmpty()) {
            errors.put(9, "地区不能为空");
        }  else if (!Pattern.matches("^[\\w,\\u4e00-\\u9fa5]+$", dto.getArea())) {
            errors.put(9, "地区必须使用英文逗号分割");
        }

        // 保存错误信息到 dto
        dto.setErrorMessages(errors);

        // 将 dto 和错误信息添加到列表
        jobList.add(dto);
        errorDataList.add(errors.isEmpty() ? new HashMap<>() : errors);  // 确保每行都有一个 Map
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 全部解析完成后的处理
    }
}
