package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.JobSearchExcelDto;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.service.IJobSearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

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

    // 批量删除岗位发布详情
    @DeleteMapping("/batch")
    public R<?> deleteJobSearchBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("删除失败，ID列表不能为空！");
        }
        boolean result = jobSearchService.removeByIds(ids);
        if (result) {
            return R.ok("删除成功！");
        } else {
            return R.error("删除失败！");
        }
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

    // 智能匹配岗位接口，通过 studentId 获取 className 后再匹配岗位
    @GetMapping("/match")
    public R<Page<JobSearch>> matchJobsByStudentId(@RequestParam String studentId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<JobSearch> result = jobSearchService.matchJobsByStudentId(studentId, page, size);
        return R.ok(result);
    }

    @PostMapping("/importExcel")
    public R<String> importJobSearchExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 使用 EasyExcel 读取 Excel 数据
            List<JobSearchExcelDto> jobList = EasyExcel.read(file.getInputStream())
                    .head(JobSearchExcelDto.class)
                    .sheet()
                    .doReadSync();

            // 过滤掉空白行
            List<JobSearchExcelDto> validJobList = jobList.stream()
                    .filter(dto -> dto.getCompanyName() != null && !dto.getCompanyName().isEmpty())
                    .collect(Collectors.toList());

            // 将 DTO 转换为实体列表
            List<JobSearch> jobEntities = validJobList.stream().map(dto -> {
                JobSearch entity = new JobSearch();
                entity.setCompanyName(dto.getCompanyName());
                entity.setPositionName(dto.getPositionName());
                entity.setHrName(dto.getHrName());
                entity.setHrPhone(dto.getHrPhone());
                entity.setMajorRequirement(dto.getMajorRequirement());
                entity.setParticipantCount(dto.getParticipantCount());
                entity.setMoney(dto.getMoney());
                entity.setArea(dto.getArea());
                entity.setApplicationLink(dto.getApplicationLink());
                entity.setAdditionalRequirements(dto.getAdditionalRequirements());
                entity.setCompanyDescription(dto.getCompanyDescription());
                return entity;
            }).collect(Collectors.toList());

            // 批量保存或更新
            if (!jobEntities.isEmpty()) {
                jobSearchService.saveOrUpdateBatch(jobEntities);
            }

            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

}
