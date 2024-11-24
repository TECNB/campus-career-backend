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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    // 根据ID查询谈话记录
    @GetMapping("/{id}")
    public R<ConversationRecords> getConversationRecordsById(@PathVariable String id) {
        QueryWrapper<ConversationRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", id);
        ConversationRecords conversationRecords = conversationRecordsService.getOne(queryWrapper);
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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 读取Excel数据并过滤只获取需要的字段
            List<ConversationRecordsExcelDto> conversationList = EasyExcel.read(file.getInputStream())
                    .head(ConversationRecordsExcelDto.class)
                    .sheet()
                    .doReadSync();

            for (ConversationRecordsExcelDto dto : conversationList) {
                // 检查关键字段是否为空，判断是否为空白行
                if (dto.getConversationTime() == null || dto.getConversationTime().isEmpty()) {
                    // 遇到空白行，跳出循环并返回成功
                    return R.ok("导入成功");
                }
                System.out.println("不是哥们"+dto.getConversationTime());

                // 保存到 conversation_records 表
                ConversationRecords conversationRecord = new ConversationRecords();
                conversationRecord.setUniversity(dto.getUniversity());
                conversationRecord.setConversationTarget(dto.getConversationTarget());
                conversationRecord.setParticipantCount(dto.getParticipantCount());
                conversationRecord.setOtherTopics(dto.getOtherTopics());
                conversationRecord.setConversationTopic(dto.getConversationTopic());
                conversationRecord.setStudentId(dto.getStudentId());
                conversationRecord.setConversationType(dto.getConversationType());
                conversationRecord.setParentContact(dto.getParentContact());
                conversationRecord.setDepartment(dto.getDepartment());
                conversationRecord.setConversationTeacher(dto.getConversationTeacher());
                conversationRecord.setConversationLocation(dto.getConversationLocation());
                conversationRecord.setConversationContent(dto.getConversationContent());
                conversationRecord.setStatus(dto.getStatus());
                conversationRecord.setAttentionLevel(dto.getAttentionLevel());

                // 日期字段转换
                conversationRecord.setConversationTime(LocalDateTime.parse(dto.getConversationTime(), dateFormatter));
                conversationRecord.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt(), dateFormatter));
                conversationRecord.setUpdatedAt(LocalDateTime.parse(dto.getUpdatedAt(), dateFormatter));

                conversationRecordsService.save(conversationRecord); // 保存到数据库
            }
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

}
