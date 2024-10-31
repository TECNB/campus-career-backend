package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.EmploymentDatabase;
import com.tec.campuscareerbackend.service.IActivityService;
import com.tec.campuscareerbackend.service.IEmploymentDatabaseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@RestController
@RequestMapping("/employment-database")
public class EmploymentDatabaseController {
    @Resource
    private IEmploymentDatabaseService employmentDatabaseService;

    // 通过构建一个分页查询接口，实现获取employment-database表中所有数据的接口
    @GetMapping
    public R<Page<EmploymentDatabase>> getAll(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<EmploymentDatabase> employmentDatabasePage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<EmploymentDatabase> result = employmentDatabaseService.page(employmentDatabasePage);

        return R.ok(result);
    }

    // 根据ID查询就业信息
    @GetMapping("/{id}")
    public R<EmploymentDatabase> getEmploymentDatabaseById(@PathVariable Long id) {
        EmploymentDatabase employmentDatabase = employmentDatabaseService.getById(id);
        return R.ok(employmentDatabase);
    }

    // 添加就业信息
    @PostMapping
    public R<EmploymentDatabase> addEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        System.out.println(employmentDatabase);
        employmentDatabaseService.save(employmentDatabase);
        return R.ok(employmentDatabase);
    }

    // 删除就业信息
    @DeleteMapping
    public R<EmploymentDatabase> deleteEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        employmentDatabaseService.removeById(employmentDatabase.getId());
        return R.ok(employmentDatabase);
    }

    // 更新就业信息
    @PutMapping
    public R<EmploymentDatabase> updateEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        employmentDatabaseService.updateById(employmentDatabase);
        return R.ok(employmentDatabase);
    }


}
