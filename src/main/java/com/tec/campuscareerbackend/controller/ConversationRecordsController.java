package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.ConversationRecordsExcelDto;
import com.tec.campuscareerbackend.entity.ConversationRecords;
import com.tec.campuscareerbackend.service.IConversationRecordsService;
import com.tec.campuscareerbackend.utils.ErrorCellStyleHandler;
import com.tec.campuscareerbackend.utils.ExcellmportListener.ConversationRecordsImportListener;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 谈话记录表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-23
 */
@RestController
@RequestMapping("/conversation-records")
public class ConversationRecordsController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Resource
    private IConversationRecordsService conversationRecordsService;

    // 通过构建一个分页查询接口，实现获取conversation-records表中所有数据的接口
    @GetMapping
    public R<Page<ConversationRecords>> getAll(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<ConversationRecords> conversationRecordsPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<ConversationRecords> result = conversationRecordsService.page(conversationRecordsPage);
        return R.ok(result);
    }

    // 根据ID查询谈话记录,构建一个分页查询接口
    @GetMapping("/{id}")
    public R<Page<ConversationRecords>> getConversationRecordsById(@PathVariable String id,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        QueryWrapper<ConversationRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", id);
        Page<ConversationRecords> conversationRecordsPage = new Page<>(page, size);
        Page<ConversationRecords> conversationRecords = conversationRecordsService.page(conversationRecordsPage, queryWrapper);

        return R.ok(conversationRecords);
    }

    // 添加谈话记录
    @PostMapping
    public R<ConversationRecords> addConversationRecords(@RequestBody ConversationRecords conversationRecords) {
        conversationRecordsService.save(conversationRecords);
        return R.ok(conversationRecords);
    }

    // 删除谈话记录
    @DeleteMapping
    public R<String> deleteConversationRecords(@RequestBody ConversationRecords conversationRecords) {
        conversationRecordsService.removeById(conversationRecords);
        return R.ok("删除成功");
    }

    // 更新谈话记录
    @PutMapping
    public R<ConversationRecords> updateConversationRecords(@RequestBody ConversationRecords conversationRecords) {
        conversationRecordsService.updateById(conversationRecords);
        return R.ok(conversationRecords);
    }

    // 批量删除谈话记录
    @DeleteMapping("/batch")
    public R<String> deleteConversationRecordsBatch(@RequestBody List<Integer> ids) {
        conversationRecordsService.removeByIds(ids);
        return R.ok("删除成功");
    }

    // 搜索谈话记录
    @GetMapping("/search")
    public R<Page<ConversationRecords>> searchConversationRecords(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<ConversationRecords> pageRequest = new Page<>(page, size);
        QueryWrapper<ConversationRecords> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "conversationTime":
                    queryWrapper.like("conversation_time", filterValue);
                    break;
                case "university":
                    queryWrapper.like("university", filterValue);
                    break;
                case "conversationTarget":
                    queryWrapper.like("conversation_target", filterValue);
                    break;
                case "participantCount":
                    queryWrapper.eq("participant_count", filterValue);
                    break;
                case "otherTopics":
                    queryWrapper.like("other_topics", filterValue);
                    break;
                case "conversationTopic":
                    queryWrapper.like("conversation_topic", filterValue);
                    break;
                case "studentId":
                    queryWrapper.eq("student_id", filterValue);
                    break;
                case "conversationType":
                    queryWrapper.like("conversation_type", filterValue);
                    break;
                case "parentContact":
                    queryWrapper.like("parent_contact", filterValue);
                    break;
                case "department":
                    queryWrapper.like("department", filterValue);
                    break;
                case "conversationTeacher":
                    queryWrapper.like("conversation_teacher", filterValue);
                    break;
                case "conversationLocation":
                    queryWrapper.like("conversation_location", filterValue);
                    break;
                case "conversationContent":
                    queryWrapper.like("conversation_content", filterValue);
                    break;
                case "status":
                    queryWrapper.eq("status", filterValue);
                    break;
                case "attentionLevel":
                    queryWrapper.eq("attention_level", filterValue);
                    break;
                case "createdAt":
                    queryWrapper.like("created_at", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<ConversationRecords> result = conversationRecordsService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }

    @PostMapping("/importExcel")
    public void importConversationRecordsExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            // 存储读取的数据和错误信息
            List<ConversationRecordsExcelDto> recordList = new ArrayList<>();
            List<Map<Integer, String>> errorDataList = new ArrayList<>();

            // 创建自定义监听器
            ConversationRecordsImportListener listener = new ConversationRecordsImportListener(recordList, errorDataList);

            // 使用 EasyExcel 读取 Excel 数据
            EasyExcel.read(file.getInputStream(), ConversationRecordsExcelDto.class, listener)
                    .sheet()
                    .doReadSync(); // 确保读取所有行数据

            // 如果没有数据，则返回提示
            if (recordList.isEmpty()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入数据为空\"}");
                return;
            }

            // 检查是否存在错误
            boolean hasErrors = recordList.stream()
                    .anyMatch(dto -> dto.getErrorMessages() != null && !dto.getErrorMessages().isEmpty());

            if (hasErrors) {
                // 如果存在错误，生成错误文件并返回
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=error_data.xlsx");

                // 调用 EasyExcel 写入错误数据
                EasyExcel.write(response.getOutputStream(), ConversationRecordsExcelDto.class)
                        .registerWriteHandler(new ErrorCellStyleHandler(errorDataList)) // 定制错误样式
                        .sheet("错误数据")
                        .doWrite(recordList);
                return;
            }

            // 日期格式化器
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");

            // 转换 DTO 为实体
            List<ConversationRecords> entityList = recordList.stream()
                    .map(dto -> convertToEntity(dto, dateFormatter)) // 使用提供的转换方法
                    .collect(Collectors.toList());

            // 批量保存或更新到数据库
            if (!entityList.isEmpty()) {
                conversationRecordsService.saveOrUpdateBatch(entityList); // 调用 service 方法保存
            }

            // 返回成功信息
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"message\":\"导入成功\"}");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/exportExcel")
    public void exportConversationRecordsExcel(HttpServletResponse response) {
        try {
            // 查询数据库中的 ConversationRecords 数据
            List<ConversationRecords> conversationRecordsList = conversationRecordsService.list();

            if (conversationRecordsList.isEmpty()) {
                throw new RuntimeException("无数据可导出");
            }

            // 将实体转换为 DTO
            List<ConversationRecordsExcelDto> conversationRecordsDtoList = conversationRecordsList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            // 设置响应头，确保正确下载文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("谈话记录", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 写入数据到响应流
            EasyExcel.write(response.getOutputStream(), ConversationRecordsExcelDto.class)
                    .sheet("谈话记录")
                    .doWrite(conversationRecordsDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 导出失败时返回 JSON 错误信息
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导出失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/downloadStandardTemplate")
    public void downloadStandardTemplate(HttpServletResponse response) {
        // 定义标准文件的路径
        String standardFilePath = uploadDir + "conversation_records_standard.xlsx";

        // 创建文件对象
        File file = new File(standardFilePath);

        if (!file.exists()) {
            // 如果文件不存在，返回错误提示
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"模板文件不存在\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 如果文件存在，设置响应头并将文件流写入响应
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("谈话记录模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 写入文件流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"文件下载失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 将 ConversationRecords 实体转换为 ConversationRecordsExcelDto
     */
    private ConversationRecordsExcelDto convertToDto(ConversationRecords entity) {
        ConversationRecordsExcelDto dto = new ConversationRecordsExcelDto();
        dto.setId(entity.getId());
        dto.setUniversity(entity.getUniversity());
        dto.setConversationTarget(entity.getConversationTarget());
        dto.setParticipantCount(entity.getParticipantCount());
        dto.setOtherTopics(entity.getOtherTopics());
        dto.setConversationTopic(entity.getConversationTopic());
        dto.setStudentId(entity.getStudentId());
        dto.setConversationType(entity.getConversationType());
        dto.setParentContact(entity.getParentContact());
        dto.setDepartment(entity.getDepartment());
        dto.setConversationTeacher(entity.getConversationTeacher());
        dto.setConversationLocation(entity.getConversationLocation());
        dto.setConversationContent(entity.getConversationContent());
        dto.setStatus(entity.getStatus());
        dto.setAttentionLevel(entity.getAttentionLevel());

        // 日期字段格式化
        dto.setConversationTime(formatDate(entity.getConversationTime()));
        dto.setCreatedAt(formatDate(entity.getCreatedAt()));
        dto.setUpdatedAt(formatDate(entity.getUpdatedAt()));

        return dto;
    }

    /**
     * 将 LocalDate 格式化为字符串
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    /**
     * 校验 DTO 是否为空白行
     */
    private boolean isValidDto(ConversationRecordsExcelDto dto) {
        return dto.getConversationTime() != null && !dto.getConversationTime().isEmpty();
    }

    /**
     * 将 DTO 转换为实体
     */
    private ConversationRecords convertToEntity(ConversationRecordsExcelDto dto, DateTimeFormatter dateFormatter) {
        ConversationRecords record = new ConversationRecords();
        record.setId(dto.getId()); // 确保设置主键以支持更新
        record.setUniversity(dto.getUniversity());
        record.setConversationTarget(dto.getConversationTarget());
        record.setParticipantCount(dto.getParticipantCount());
        record.setOtherTopics(dto.getOtherTopics());
        record.setConversationTopic(dto.getConversationTopic());
        record.setStudentId(dto.getStudentId());
        record.setConversationType(dto.getConversationType());
        record.setParentContact(dto.getParentContact());
        record.setDepartment(dto.getDepartment());
        record.setConversationTeacher(dto.getConversationTeacher());
        record.setConversationLocation(dto.getConversationLocation());
        record.setConversationContent(dto.getConversationContent());
        record.setStatus(dto.getStatus());
        record.setAttentionLevel(dto.getAttentionLevel());

        // 日期字段转换
        record.setConversationTime(parseDate(dto.getConversationTime(), dateFormatter));
        record.setCreatedAt(parseDate(dto.getCreatedAt(), dateFormatter));
        record.setUpdatedAt(parseDate(dto.getUpdatedAt(), dateFormatter));

        return record;
    }

    /**
     * 解析日期字段
     */
    private LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        return dateStr != null && !dateStr.isEmpty() ? LocalDate.parse(dateStr, formatter) : null;
    }

}
