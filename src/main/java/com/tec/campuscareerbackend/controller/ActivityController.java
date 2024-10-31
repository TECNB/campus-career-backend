package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.Activity;
import com.tec.campuscareerbackend.service.IActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 活动信息表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private IActivityService activityService;

    // 通过构建一个分页查询接口，实现获取activity表中所有数据的接口
    @GetMapping
    public R<Page<Activity>> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<Activity> activityPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<Activity> result = activityService.page(activityPage);

        return R.ok(result);
    }

    // 根据ID查询活动
    @GetMapping("/{id}")
    public R<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return R.ok(activity);
    }


    // 添加活动
    @PostMapping
    public R<Activity> addActivity(@RequestBody Activity activity) {
        System.out.println(activity);
        activityService.save(activity);
        return R.ok(activity);
    }

    // 删除活动
    @DeleteMapping
    public R<Activity> deleteActivity(@RequestBody Activity activity) {
        activityService.removeById(activity.getId());
        return R.ok(activity);
    }

    // 修改活动
    @PutMapping
    public R<Activity> updateActivity(@RequestBody Activity activity) {
        activityService.updateById(activity);
        return R.ok(activity);
    }
}
