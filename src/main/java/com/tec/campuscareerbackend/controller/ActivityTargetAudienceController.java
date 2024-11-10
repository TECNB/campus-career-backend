package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.ActivityTargetAudience;
import com.tec.campuscareerbackend.service.IActivityTargetAudienceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    public R<List<ActivityTargetAudience>> getAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<ActivityTargetAudience> activityTargetAudiencePage = new Page<>(page, size);
        Page<ActivityTargetAudience> result = activityTargetAudienceService.page(activityTargetAudiencePage);
        return R.ok(result.getRecords());
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

}
