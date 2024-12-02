package com.tec.campuscareerbackend.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.tec.campuscareerbackend.dto.UserInfoExcelDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 自定义监听器
public class ExcelImportListener extends AnalysisEventListener<UserInfoExcelDto> {

    private final List<UserInfoExcelDto> userList;
    private final DateTimeFormatter dateFormatter;
    private final List<Map<Integer, String>> errorDataList;

    public ExcelImportListener(List<UserInfoExcelDto> userList, DateTimeFormatter dateFormatter, List<Map<Integer, String>> errorDataList) {
        this.userList = userList;
        this.dateFormatter = dateFormatter;
        this.errorDataList = errorDataList;
    }

    @Override
    public void invoke(UserInfoExcelDto dto, AnalysisContext context) {
        // 初始化错误消息
        Map<Integer, String> errors = new HashMap<>();

        // 校验 expectedGraduation 格式是否正确
        try {
            if (dto.getExpectedGraduation() != null && !dto.getExpectedGraduation().isEmpty()) {
                // 尝试解析日期
                LocalDate.parse(dto.getExpectedGraduation(), dateFormatter);
            }
        } catch (DateTimeParseException e) {
            errors.put(13, "时间格式需要为 yyyy/M/d"); // 第 14 列错误
        }

        // 校验 gender 是否为“男”或“女”
        if (dto.getGender() != null && !("男".equals(dto.getGender()) || "女".equals(dto.getGender()))) {
            errors.put(2, "性别必须为“男”或“女”"); // 第 3 列错误
        }

        // 校验 partyHours 必须为整数
        if (dto.getPartyHours() == 0) {
            errors.put(37, "党课工时必须为整数"); // 第 15 列错误
        }

        // 将错误信息保存到 dto 中
        dto.setErrorMessages(errors);

        // 添加数据和错误信息到 userList 和 errorDataList
        userList.add(dto);
        errorDataList.add(errors);
    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 解析完成后的处理逻辑（如日志记录等）
    }
}
