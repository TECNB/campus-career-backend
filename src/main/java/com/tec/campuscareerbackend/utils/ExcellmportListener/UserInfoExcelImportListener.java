package com.tec.campuscareerbackend.utils.ExcellmportListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tec.campuscareerbackend.dto.UserInfoExcelDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;



public class UserInfoExcelImportListener extends AnalysisEventListener<UserInfoExcelDto> {

    private final List<UserInfoExcelDto> userList;
    private final DateTimeFormatter dateFormatter;
    private final List<Map<Integer, String>> errorDataList;
    private final Set<String> excelStudentIds; // 用于存储已经出现过的学号
    private final Map<String, Integer> existingStudentIdMap; // 数据库中的学号与主键映射

    public UserInfoExcelImportListener(List<UserInfoExcelDto> userList, DateTimeFormatter dateFormatter, List<Map<Integer, String>> errorDataList, Map<String, Integer> existingStudentIdMap) {
        this.userList = userList;
        this.dateFormatter = dateFormatter;
        this.errorDataList = errorDataList;
        this.excelStudentIds = new HashSet<>(); // 初始化集合
        this.existingStudentIdMap = existingStudentIdMap;
    }

    @Override
    public void invoke(UserInfoExcelDto dto, AnalysisContext context) {
        // 初始化错误消息
        Map<Integer, String> errors = new HashMap<>();

        // 获取学号和主键 ID
        String studentId = dto.getStudentId();
        Integer dtoId = dto.getId(); // Excel 数据中包含的主键 ID（区分新增或更新）

        // 校验性别是否为“男”或“女”
        if (dto.getGender() != null && !("男".equals(dto.getGender()) || "女".equals(dto.getGender()))) {
            errors.put(2, "性别必须为“男”或“女”"); // 第 3 列错误
        }

        // 校验手机号必须为11位
        if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9]{11}$")) {
            errors.put(3, "手机号必须为11位"); // 第 4 列错误
        }

        // 校验学号是否重复
        if (studentId == null || studentId.isEmpty()) {
            errors.put(4, "学号不能为空");
        } else if (!studentId.matches("^[0-9]{12}$")) {
            errors.put(4, "学号必须为12位");
        } else {
            // 校验 Excel 内部重复
            if (excelStudentIds.contains(studentId)) {
                errors.put(4, "学号在表格中重复");
            } else {
                excelStudentIds.add(studentId); // 添加到表格集合
            }

            // 校验数据库中重复
            Integer existingId = existingStudentIdMap.get(studentId);
            if (existingId != null && !existingId.equals(dtoId)) {
                errors.put(4, "学号在数据库中已存在，请修改序号为：" + existingId);
            }
        }

        // 校验身份证号格式
        if (dto.getIdCard() != null && !dto.getIdCard().matches("^[0-9]{17}[0-9Xx]$")) {
            errors.put(5, "身份证号格式不正确"); // 第 6 列错误
        }

        // 校验专业不为空
        if (dto.getMajor() == null || dto.getMajor().isEmpty()) {
            errors.put(7, "专业不能为空"); // 第 8 列错误
        }

        // 校验班级不为空
        if (dto.getClassName() == null || dto.getClassName().isEmpty()) {
            errors.put(8, "班级不能为空"); // 第 9 列错误
        }

        // 校验 expectedGraduation 格式是否正确
        try {
            if (dto.getExpectedGraduation() != null && !dto.getExpectedGraduation().isEmpty()) {
                // 尝试解析日期
                LocalDate.parse(dto.getExpectedGraduation(), dateFormatter);
            }
        } catch (DateTimeParseException e) {
            errors.put(12, "时间格式需要为 yyyy/M/d"); // 第 13 列错误
        }

        // 校验党课工时必须为整数
        if (dto.getPartyHours() == 0) {
            errors.put(36, "党课工时必须为数字整数"); // 第 37 列错误
        }

        // 将错误信息保存到 dto 中
        dto.setErrorMessages(errors);

        // 添加数据和错误信息到 userList 和 errorDataList
        userList.add(dto);
        errorDataList.add(errors.isEmpty() ? new HashMap<>() : errors);  // 确保每行都有一个 Map
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}

