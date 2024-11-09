package com.tec.campuscareerbackend.controller;


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
    public R<List<ActivityPlace>> getAll() {
        List<ActivityPlace> activityPlaceList = activityPlaceService.list();
        return R.ok(activityPlaceList);
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

}
