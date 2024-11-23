package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.SpecialGroup;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.service.ISpecialGroupService;
import com.tec.campuscareerbackend.service.IUserInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-23
 */
@RestController
@RequestMapping("/special-group")
public class SpecialGroupController {
    @Resource
    private ISpecialGroupService specialGroupService;

    // 通过构建一个分页查询接口，实现获取special-group表中所有数据的接口
    @GetMapping
    public R<Page<SpecialGroup>> getAll(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<SpecialGroup> userInfoPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<SpecialGroup> result = specialGroupService.page(userInfoPage);
        return R.ok(result);
    }

    // 根据ID查询特殊群体信息
    @GetMapping("/{id}")
    public R<SpecialGroup> getSpecialGroupById(@PathVariable String id) {
        QueryWrapper<SpecialGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", id);
        SpecialGroup specialGroup = specialGroupService.getOne(queryWrapper);
        return R.ok(specialGroup);
    }

    // 添加特殊群体信息
    @PostMapping
    public R<SpecialGroup> addSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        specialGroupService.save(specialGroup);
        return R.ok(specialGroup);
    }

    // 删除特殊群体信息
    @DeleteMapping
    public R<String> deleteSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        specialGroupService.removeById(specialGroup);
        return R.ok("删除成功");
    }

    // 更新特殊群体信息
    @PutMapping
    public R<SpecialGroup> updateSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        specialGroupService.updateById(specialGroup);
        return R.ok(specialGroup);
    }

}
