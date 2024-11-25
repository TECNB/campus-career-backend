package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.ConversationRecordsExcelDto;
import com.tec.campuscareerbackend.entity.ConversationRecords;
import com.tec.campuscareerbackend.service.IConversationRecordsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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

    @PostMapping("/importExcel")
    public R<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 定义日期格式解析器
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            // 读取 Excel 数据
            List<ConversationRecordsExcelDto> conversationList = EasyExcel.read(file.getInputStream())
                    .head(ConversationRecordsExcelDto.class)
                    .sheet()
                    .doReadSync();

            // 过滤掉空白行并转换为实体
            List<ConversationRecords> conversationRecords = conversationList.stream()
                    .filter(this::isValidDto) // 检查是否为空白行
                    .map(dto -> convertToEntity(dto, dateFormatter)) // DTO 转实体
                    .collect(Collectors.toList());

            // 批量保存或更新数据
            if (!conversationRecords.isEmpty()) {
                conversationRecordsService.saveOrUpdateBatch(conversationRecords);
            }

            return R.ok("导入成功");
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("导入失败: 文件读取错误");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return R.error("导入失败: 日期格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
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
