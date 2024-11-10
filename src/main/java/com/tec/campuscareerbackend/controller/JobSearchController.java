package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.service.IJobSearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 岗位发布详情表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@RestController
@RequestMapping("/job-search")
public class JobSearchController {
    @Resource
    private IJobSearchService jobSearchService;

    // 通过构建一个分页查询接口，实现获取job-search表中所有数据的接口
    @GetMapping
    public R<Page<JobSearch>> getAll(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<JobSearch> jobSearchPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<JobSearch> result = jobSearchService.page(jobSearchPage);

        return R.ok(result);
    }

    // 根据ID查询岗位发布详情
    @GetMapping("/{id}")
    public R<JobSearch> getJobSearchById(@PathVariable Long id) {
        JobSearch jobSearch = jobSearchService.getById(id);
        return R.ok(jobSearch);
    }

    // 添加岗位发布详情
    @PostMapping
    public R<JobSearch> addJobSearch(@RequestBody JobSearch jobSearch) {
        System.out.println(jobSearch);
        jobSearchService.save(jobSearch);
        return R.ok(jobSearch);
    }

    // 删除岗位发布详情
    @DeleteMapping
    public R<JobSearch> deleteJobSearch(@RequestBody JobSearch jobSearch) {
        jobSearchService.removeById(jobSearch.getId());
        return R.ok(jobSearch);
    }

    // 更新岗位发布详情
    @PutMapping
    public R<JobSearch> updateJobSearch(@RequestBody JobSearch jobSearch) {
        jobSearchService.updateById(jobSearch);
        return R.ok(jobSearch);
    }

    // 搜索岗位发布详情
    @GetMapping("/search")
    public R<Page<JobSearch>> searchJobSearch(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<JobSearch> jobSearchPage = new Page<>(page, size);
        QueryWrapper<JobSearch> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "companyName":
                    queryWrapper.like("company_name", filterValue);
                    break;
                case "positionName":
                    queryWrapper.like("position_name", filterValue);
                    break;
                case "hrName":
                    queryWrapper.like("hr_name", filterValue);
                    break;
                case "hrPhone":
                    queryWrapper.like("hr_phone", filterValue);
                    break;
                case "majorRequirement":
                    queryWrapper.like("major_requirement", filterValue);
                    break;
                case "participantCount":
                    queryWrapper.eq("participant_count", filterValue);
                    break;
                case "money":
                    queryWrapper.eq("money", filterValue);
                    break;
                case "area":
                    queryWrapper.like("area", filterValue);
                    break;
                case "applicationLink":
                    queryWrapper.like("application_link", filterValue);
                    break;
                case "additionalRequirements":
                    queryWrapper.like("additional_requirements", filterValue);
                    break;
                case "companyDescription":
                    queryWrapper.like("company_description", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<JobSearch> result = jobSearchService.page(jobSearchPage, queryWrapper);
        return R.ok(result);
    }

}
