package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.JobSearchExcelDto;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.service.IJobSearchService;
import com.tec.campuscareerbackend.utils.ErrorCellStyleHandler;
import com.tec.campuscareerbackend.utils.ExcellmportListener.JobSearchExcelImportListener;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Value("${file.upload-dir}")
    private String uploadDir;
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
    public R<String> deleteJobSearchBatch(@RequestBody List<Long> ids) {
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

    // 搜索匹配后的岗位
    @GetMapping("/search-match")
    public R<Page<JobSearch>> searchMatch(
            @RequestParam String studentId, // 学生ID，用于获取匹配结果
            @RequestParam(required = false) String filterField, // 筛选字段
            @RequestParam(required = false) String filterValue, // 筛选值
            @RequestParam(defaultValue = "1") int page, // 分页页码
            @RequestParam(defaultValue = "10") int size // 每页数量
    ) {
        // 1. 调用 match 接口获取匹配的岗位
        Page<JobSearch> result = jobSearchService.matchJobsByStudentId(studentId, page, size);
        List<JobSearch> matchedJobs = result.getRecords();

        if (matchedJobs == null || matchedJobs.isEmpty()) {
            return R.ok(new Page<>(page, size)); // 返回空分页
        }

        // 2. 筛选匹配的岗位
        Stream<JobSearch> jobStream = matchedJobs.stream();

        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "matchLevel":
                    jobStream = jobStream.filter(job -> job.getMatchLevel().equals(filterValue));
                    break;
                case "companyName":
                    jobStream = jobStream.filter(job -> job.getCompanyName().contains(filterValue));
                    break;
                case "positionName":
                    jobStream = jobStream.filter(job -> job.getPositionName().contains(filterValue));
                    break;
                case "hrName":
                    jobStream = jobStream.filter(job -> job.getHrName().contains(filterValue));
                    break;
                case "hrPhone":
                    jobStream = jobStream.filter(job -> job.getHrPhone().contains(filterValue));
                    break;
                case "majorRequirement":
                    jobStream = jobStream.filter(job -> job.getMajorRequirement().contains(filterValue));
                    break;
                case "participantCount":
                    try {
                        int count = Integer.parseInt(filterValue);
                        jobStream = jobStream.filter(job -> job.getParticipantCount() == count);
                    } catch (NumberFormatException e) {
                        return R.error("无效的筛选值: participantCount 应为数字");
                    }
                    break;
                case "money":
                    jobStream = jobStream.filter(job -> job.getMoney().contains(filterValue));
                    break;
                case "area":
                    jobStream = jobStream.filter(job -> job.getArea().contains(filterValue));
                    break;
                case "applicationLink":
                    jobStream = jobStream.filter(job -> job.getApplicationLink().contains(filterValue));
                    break;
                case "additionalRequirements":
                    jobStream = jobStream.filter(job -> job.getAdditionalRequirements().contains(filterValue));
                    break;
                case "companyDescription":
                    jobStream = jobStream.filter(job -> job.getCompanyDescription().contains(filterValue));
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        // 3. 分页处理
        List<JobSearch> filteredJobs = jobStream.collect(Collectors.toList());
        int total = filteredJobs.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(page * size, total);
        List<JobSearch> pagedJobs = filteredJobs.subList(fromIndex, toIndex);

        // 4. 封装分页结果
        Page<JobSearch> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pagedJobs);
        return R.ok(resultPage);
    }

    @PostMapping("/importExcel")
    public void importJobSearchExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            // 存储读取的数据和错误信息
            List<JobSearchExcelDto> jobList = new ArrayList<>();
            List<Map<Integer, String>> errorDataList = new ArrayList<>();

            // 创建 ExcelImportListener
            JobSearchExcelImportListener listener = new JobSearchExcelImportListener(jobList, errorDataList);

            // 使用 EasyExcel 读取 Excel 数据，使用自定义监听器
            EasyExcel.read(file.getInputStream(), JobSearchExcelDto.class, listener)
                    .sheet()
                    .doReadSync();  // 使用同步读取方式，确保读取所有行

            // 如果没有数据，则返回提示
            if (jobList.isEmpty()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入数据为空\"}");
                return;
            }

            // 检查是否存在错误
            boolean hasErrors = jobList.stream()
                    .anyMatch(dto -> dto.getErrorMessages() != null && !dto.getErrorMessages().isEmpty());

            if (hasErrors) {
                // 如果有错误，生成错误文件并返回
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=error_data.xlsx");

                // 调用 EasyExcel 写入错误数据
                EasyExcel.write(response.getOutputStream(), JobSearchExcelDto.class)
                        .registerWriteHandler(new ErrorCellStyleHandler(errorDataList))
                        .sheet("错误数据")
                        .doWrite(jobList);
                return;
            }

            // 使用 mapToJobSearch 将 DTO 转换为实体列表
            List<JobSearch> jobEntities = jobList.stream()
                    .map(this::mapToJobSearch)  // 调用 mapToJobSearch 方法
                    .collect(Collectors.toList());

            // 批量保存或更新
            if (!jobEntities.isEmpty()) {
                jobSearchService.saveOrUpdateBatch(jobEntities);
            }

            // 返回成功信息
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"message\":\"导入成功\"}");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/exportExcel")
    public void exportJobSearchExcel(HttpServletResponse response) {
        try {
            // 查询数据库中的 JobSearch 数据
            List<JobSearch> jobSearchList = jobSearchService.list();

            if (jobSearchList.isEmpty()) {
                throw new RuntimeException("无数据可导出");
            }

            // 将实体对象转换为 DTO 对象
            List<JobSearchExcelDto> jobSearchDtoList = jobSearchList.stream()
                    .map(this::mapToJobSearchExcelDto)
                    .collect(Collectors.toList());

            // 设置响应头，确保文件正确下载
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("求职信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 写入数据到响应流
            EasyExcel.write(response.getOutputStream(), JobSearchExcelDto.class)
                    .sheet("岗位信息")
                    .doWrite(jobSearchDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 导出失败时返回 JSON 错误信息
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导出失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/downloadStandardTemplate")
    public void downloadStandardTemplate(HttpServletResponse response) {
        // 定义标准文件的路径
        String standardFilePath = uploadDir + "job_search_standard.xlsx";

        // 创建文件对象
        File file = new File(standardFilePath);

        if (!file.exists()) {
            // 如果文件不存在，返回错误提示
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"模板文件不存在\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 如果文件存在，设置响应头并将文件流写入响应
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生个人信息模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 写入文件流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"文件下载失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 将 JobSearchExcelDto 转换为 JobSearch 实体对象
     */
    public JobSearch mapToJobSearch(JobSearchExcelDto dto) {
        JobSearch jobSearch = new JobSearch();

        jobSearch.setId(dto.getId());
        jobSearch.setDisplayId(dto.getDisplayId());
        jobSearch.setCompanyName(dto.getCompanyName());
        jobSearch.setPositionName(dto.getPositionName());
        jobSearch.setHrName(dto.getHrName());
        jobSearch.setHrPhone(dto.getHrPhone());
        jobSearch.setMajorRequirement(dto.getMajorRequirement());
        jobSearch.setParticipantCount(dto.getParticipantCount());
        jobSearch.setMoney(dto.getMoney());
        jobSearch.setArea(dto.getArea());
        jobSearch.setApplicationLink(dto.getApplicationLink());
        jobSearch.setAdditionalRequirements(dto.getAdditionalRequirements());
        jobSearch.setCompanyDescription(dto.getCompanyDescription());

        return jobSearch;
    }

    /**
     * 将 JobSearch 实体对象转换为 JobSearchExcelDto
     */
    private JobSearchExcelDto mapToJobSearchExcelDto(JobSearch jobSearch) {
        JobSearchExcelDto dto = new JobSearchExcelDto();
        dto.setId(jobSearch.getId());
        dto.setDisplayId(jobSearch.getDisplayId());
        dto.setCompanyName(jobSearch.getCompanyName());
        dto.setPositionName(jobSearch.getPositionName());
        dto.setHrName(jobSearch.getHrName());
        dto.setHrPhone(jobSearch.getHrPhone());
        dto.setMajorRequirement(jobSearch.getMajorRequirement());
        dto.setParticipantCount(jobSearch.getParticipantCount());
        dto.setMoney(jobSearch.getMoney());
        dto.setArea(jobSearch.getArea());
        dto.setApplicationLink(jobSearch.getApplicationLink());
        dto.setAdditionalRequirements(jobSearch.getAdditionalRequirements());
        dto.setCompanyDescription(jobSearch.getCompanyDescription());
        return dto;
    }

}
