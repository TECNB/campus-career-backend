package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Resource
    private IUserInfoService userInfoService;

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
        if (employmentSearch == null) {
            employmentSearch = new EmploymentSearch();
        }

        // 通过用户ID查询用户信息
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", userId);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

        employmentSearch.setUserInfo(userInfo);

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

    // 批量删除就业信息
    @DeleteMapping("/batch")
    public R<String> deleteEmploymentSearchBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("删除失败，ID列表不能为空！");
        }
        boolean result = employmentSearchService.removeByIds(ids);
        if (result) {
            return R.ok("删除成功！");
        } else {
            return R.error("删除失败！");
        }
    }

    // 搜索就业信息
    @GetMapping("/search")
    public R<Page<EmploymentSearch>> searchEmploymentSearch(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<EmploymentSearch> pageRequest = new Page<>(page, size);
        QueryWrapper<EmploymentSearch> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "name":
                    queryWrapper.like("name", filterValue);
                    break;
                case "gender":
                    queryWrapper.eq("gender", filterValue);
                    break;
                case "className":
                    queryWrapper.like("class_name", filterValue);
                    break;
                case "userId":
                    queryWrapper.eq("user_id", filterValue);
                    break;
                case "contactNumber":
                    queryWrapper.like("contact_number", filterValue);
                    break;
                case "classTeacher":
                    queryWrapper.like("class_teacher", filterValue);
                    break;
                case "graduationTutor":
                    queryWrapper.like("graduation_tutor", filterValue);
                    break;
                case "futurePlan":
                    queryWrapper.like("future_plan", filterValue);
                    break;
                case "companyName":
                    queryWrapper.like("company_name", filterValue);
                    break;
                case "employmentStatus":
                    queryWrapper.like("employment_status", filterValue);
                    break;
                case "workLocation":
                    queryWrapper.like("work_location", filterValue);
                    break;
                case "salary":
                    queryWrapper.eq("salary", filterValue);
                    break;
                case "companyNature":
                    queryWrapper.like("company_nature", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<EmploymentSearch> result = employmentSearchService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }
}
