package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.StudentIntention;
import com.tec.campuscareerbackend.service.IStudentIntentionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 学生求职意向表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/student-intention")
public class StudentIntentionController {
    @Resource
    private IStudentIntentionService studentIntentionService;

    // 通过构建一个分页查询接口，实现获取student-intention表中所有数据的接口
    @GetMapping
    public R<Page<StudentIntention>> getAll(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<StudentIntention> studentIntentionPage = new Page<>(page, size);
        Page<StudentIntention> result = studentIntentionService.page(studentIntentionPage);
        return R.ok(result);
    }

    // 根据ID查询学生求职意向
    @GetMapping("/{id}")
    public R<StudentIntention> getById(@PathVariable Integer id) {
        StudentIntention studentIntention = studentIntentionService.getById(id);
        return R.ok(studentIntention);
    }

    // 根据studentId查询学生求职意向
    @GetMapping("/student/{studentId}")
    public R<List<StudentIntention>> getByStudentId(@PathVariable String studentId) {
        QueryWrapper<StudentIntention> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<StudentIntention> studentIntention = studentIntentionService.list(queryWrapper);
        return R.ok(studentIntention);
    }

    // 添加学生求职意向
    @PostMapping
    public R<StudentIntention> add(@RequestBody StudentIntention studentIntention) {
        studentIntentionService.save(studentIntention);
        return R.ok(studentIntention);
    }

    // 更新学生求职意向
    @PutMapping
    public R<StudentIntention> update(@RequestBody StudentIntention studentIntention) {
        studentIntentionService.updateById(studentIntention);
        return R.ok(studentIntention);
    }

    // 删除学生求职意向
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Integer id) {
        studentIntentionService.removeById(id);
        return R.ok("删除成功");
    }

    // 根据studentId以及companyId删除学生求职意向
    @DeleteMapping
    public R<String> deleteByStudentIdAndCompanyId(@RequestParam String studentId, @RequestParam Integer companyId) {
        QueryWrapper<StudentIntention> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("company_id", companyId);
        studentIntentionService.remove(queryWrapper);
        return R.ok("删除成功");

    }


}
