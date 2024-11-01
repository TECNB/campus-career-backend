package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.EmploymentDatabase;
import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.tec.campuscareerbackend.service.IEmploymentDatabaseService;
import com.tec.campuscareerbackend.service.IEmploymentSearchService;
import com.tec.campuscareerbackend.service.IJobSearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@RestController
@RequestMapping("/employment-search")
public class EmploymentSearchController {
    @Resource
    private IEmploymentSearchService employmentSearchService;

    // 通过构建一个分页查询接口，实现获取employment-search表中所有数据的接口
    @GetMapping
    public R<Page<EmploymentSearch>> getAll(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<EmploymentSearch> employmentSearchPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<EmploymentSearch> result = employmentSearchService.page(employmentSearchPage);

        return R.ok(result);
    }

    // 根据ID查询就业信息
    @GetMapping("/{id}")
    public R<EmploymentSearch> getEmploymentSearchById(@PathVariable Long id) {
        EmploymentSearch employmentSearch = employmentSearchService.getById(id);
        return R.ok(employmentSearch);
    }

    // 根据userId查询就业信息
    @GetMapping("/user/{userId}")
    public R<EmploymentSearch> getEmploymentSearchByUserId(@PathVariable Long userId) {
        EmploymentSearch employmentSearch = employmentSearchService.getByUserId(userId);
        return R.ok(employmentSearch);
    }

    // 添加就业信息
    @PostMapping
    public R<EmploymentSearch> addEmploymentSearch(@RequestBody EmploymentSearch employmentSearch) {
        System.out.println(employmentSearch);
        employmentSearchService.save(employmentSearch);
        return R.ok(employmentSearch);
    }

    // 删除就业信息
    @DeleteMapping
    public R<EmploymentSearch> deleteEmploymentSearch(@RequestBody EmploymentSearch employmentSearch) {
        employmentSearchService.removeById(employmentSearch.getId());
        return R.ok(employmentSearch);
    }

    // 更新就业信息
    @PutMapping
    public R<EmploymentSearch> updateEmploymentSearch(@RequestBody EmploymentSearch employmentSearch) {
        employmentSearchService.updateById(employmentSearch);
        return R.ok(employmentSearch);
    }
}
