package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.Activity;
import com.tec.campuscareerbackend.entity.ActivityPlace;
import com.tec.campuscareerbackend.service.IActivityPlaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-09
 */
@RestController
@RequestMapping("/activity-place")
public class ActivityPlaceController {
    @Resource
    private IActivityPlaceService activityPlaceService;

    // 通过构建一个分页查询接口，实现获取ActivityPlace表中所有数据的接口
    @GetMapping
    public R<Page<ActivityPlace>> getAll(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Page<ActivityPlace> activityPlacePage = new Page<>(page, size);
        Page<ActivityPlace> result = activityPlaceService.page(activityPlacePage);
        return R.ok(result);
    }

    // 根据ID查询活动地点
    @GetMapping("/{id}")
    public R<ActivityPlace> getActivityById(@PathVariable Integer id) {
        ActivityPlace activityPlace = activityPlaceService.getById(id);
        return R.ok(activityPlace);
    }

    // 添加活动地点
    @PostMapping
    public R<ActivityPlace> addActivityPlace(@RequestBody ActivityPlace activityPlace) {
        activityPlaceService.save(activityPlace);
        return R.ok(activityPlace);
    }

    // 删除活动地点
    @DeleteMapping
    public R<String> deleteActivityPlace(@RequestBody ActivityPlace activityPlace) {
        activityPlaceService.removeById(activityPlace.getId());
        return R.ok("删除成功");
    }

    // 修改活动地点
    @PutMapping
    public R<ActivityPlace> updateActivityPlace(@RequestBody ActivityPlace activityPlace) {
        activityPlaceService.updateById(activityPlace);
        return R.ok(activityPlace);
    }

    // 批量删除活动地点
    @DeleteMapping("/batch")
    public R<String> deleteActivityPlaceBatch(@RequestBody List<Integer> ids) {
        activityPlaceService.removeByIds(ids);
        return R.ok("删除成功");
    }

    // 搜索活动地点
    @GetMapping("/search")
    public R<List<ActivityPlace>> searchActivityPlace(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue) {

        QueryWrapper<ActivityPlace> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "name":
                    queryWrapper.like("name", filterValue);
                    break;
                case "createdAt":
                    queryWrapper.like("created_at", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        List<ActivityPlace> result = activityPlaceService.list(queryWrapper);
        return R.ok(result);
    }

}
