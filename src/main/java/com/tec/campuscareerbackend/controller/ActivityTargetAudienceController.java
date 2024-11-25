package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.ActivityTargetAudienceExcelDto;
import com.tec.campuscareerbackend.entity.ActivityTargetAudience;
import com.tec.campuscareerbackend.service.IActivityTargetAudienceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-10
 */
@RestController
@RequestMapping("/activity-target-audience")
public class ActivityTargetAudienceController {
    @Resource
    private IActivityTargetAudienceService activityTargetAudienceService;

    // 通过构建一个分页查询接口，实现获取ActivityTargetAudience表中所有数据的接口
    @GetMapping
    public R<Page<ActivityTargetAudience>> getAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<ActivityTargetAudience> activityTargetAudiencePage = new Page<>(page, size);
        Page<ActivityTargetAudience> result = activityTargetAudienceService.page(activityTargetAudiencePage);
        return R.ok(result);
    }

    // 根据ID查询活动对象
    @GetMapping("/{id}")
    public R<ActivityTargetAudience> getActivityById(@PathVariable Integer id) {
        ActivityTargetAudience activityTargetAudience = activityTargetAudienceService.getById(id);
        return R.ok(activityTargetAudience);
    }

    // 添加活动对象
    @PostMapping
    public R<ActivityTargetAudience> addActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.save(activityTargetAudience);
        return R.ok(activityTargetAudience);
    }

    // 删除活动对象
    @DeleteMapping
    public R<String> deleteActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.removeById(activityTargetAudience.getId());
        return R.ok("删除成功");
    }

    // 修改活动对象
    @PutMapping
    public R<ActivityTargetAudience> updateActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.updateById(activityTargetAudience);
        return R.ok(activityTargetAudience);
    }

    @GetMapping("/search")
    public R<Page<ActivityTargetAudience>> searchActivityTargetAudience(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<ActivityTargetAudience> pageRequest = new Page<>(page, size);
        QueryWrapper<ActivityTargetAudience> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "audienceLabel":
                    queryWrapper.like("audience_label", filterValue);
                    break;
                case "audienceValue":
                    queryWrapper.like("audience_value", filterValue);
                    break;
                case "createdAt":
                    queryWrapper.like("created_at", filterValue);
                    break;
                // 其他可筛选的字段同理添加
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<ActivityTargetAudience> result = activityTargetAudienceService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }

    @PostMapping("/importExcel")
    public R<String> importActivityTargetAudienceExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 读取Excel数据并过滤只获取需要的字段
            List<ActivityTargetAudienceExcelDto> audienceList = EasyExcel.read(file.getInputStream())
                    .head(ActivityTargetAudienceExcelDto.class)
                    .sheet()
                    .doReadSync();

            for (ActivityTargetAudienceExcelDto dto : audienceList) {
                // 检查关键字段是否为空，判断是否为空白行
                if (dto.getAudienceLabel() == null || dto.getAudienceLabel().isEmpty()) {
                    // 遇到空白行，跳出循环并返回成功
                    return R.ok("导入成功");
                }

                // 保存到 activity_target_audience 表
                ActivityTargetAudience activityTargetAudience = new ActivityTargetAudience();
                activityTargetAudience.setAudienceLabel(dto.getAudienceLabel());
                activityTargetAudience.setAudienceValue(dto.getAudienceValue());

                // 保存到数据库
                activityTargetAudienceService.save(activityTargetAudience);
            }
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

}
