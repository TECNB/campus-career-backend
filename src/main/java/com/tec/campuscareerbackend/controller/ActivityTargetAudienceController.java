package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.ActivityTargetAudienceExcelDto;
import com.tec.campuscareerbackend.entity.ActivityTargetAudience;
import com.tec.campuscareerbackend.service.IActivityTargetAudienceService;
import com.tec.campuscareerbackend.utils.ErrorCellStyleHandler;
import com.tec.campuscareerbackend.utils.ExcellmportListener.ActivityTargetAudienceImportListener;
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

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-10
 */
@RestController
@RequestMapping("/activity-target-audience")
public class ActivityTargetAudienceController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Resource
    private IActivityTargetAudienceService activityTargetAudienceService;

    // 通过构建一个分页查询接口，实现获取ActivityTargetAudience表中所有数据的接口
    @GetMapping
    public R<Page<ActivityTargetAudience>> getAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<ActivityTargetAudience> activityTargetAudiencePage = new Page<>(page, size);
        Page<ActivityTargetAudience> result = activityTargetAudienceService.page(activityTargetAudiencePage);
        return R.ok(result);
    }

    // 根据ID查询活动对象
    @GetMapping("/{id}")
    public R<ActivityTargetAudience> getActivityById(@PathVariable Integer id) {
        ActivityTargetAudience activityTargetAudience = activityTargetAudienceService.getById(id);
        return R.ok(activityTargetAudience);
    }

    // 添加活动对象
    @PostMapping
    public R<ActivityTargetAudience> addActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.save(activityTargetAudience);
        return R.ok(activityTargetAudience);
    }

    // 删除活动对象
    @DeleteMapping
    public R<String> deleteActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.removeById(activityTargetAudience.getId());
        return R.ok("删除成功");
    }

    // 修改活动对象
    @PutMapping
    public R<ActivityTargetAudience> updateActivityTargetAudience(@RequestBody ActivityTargetAudience activityTargetAudience) {
        activityTargetAudienceService.updateById(activityTargetAudience);
        return R.ok(activityTargetAudience);
    }

    // 批量删除活动对象
    @DeleteMapping("/batch")
    public R<String> deleteActivityTargetAudienceBatch(@RequestBody List<Integer> ids) {
        activityTargetAudienceService.removeByIds(ids);
        return R.ok("删除成功");
    }

    @GetMapping("/search")
    public R<Page<ActivityTargetAudience>> searchActivityTargetAudience(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<ActivityTargetAudience> pageRequest = new Page<>(page, size);
        QueryWrapper<ActivityTargetAudience> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "audienceLabel":
                    queryWrapper.like("audience_label", filterValue);
                    break;
                case "audienceValue":
                    queryWrapper.like("audience_value", filterValue);
                    break;
                case "createdAt":
                    queryWrapper.like("created_at", filterValue);
                    break;
                // 其他可筛选的字段同理添加
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<ActivityTargetAudience> result = activityTargetAudienceService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }

    @PostMapping("/importExcel")
    public void importActivityTargetAudienceExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            // 存储读取的数据和错误信息
            List<ActivityTargetAudienceExcelDto> audienceList = new ArrayList<>();
            List<Map<Integer, String>> errorDataList = new ArrayList<>();

            // 创建 ExcelImportListener
            ActivityTargetAudienceImportListener listener = new ActivityTargetAudienceImportListener(audienceList, errorDataList);

            // 使用 EasyExcel 读取 Excel 数据，使用自定义监听器
            EasyExcel.read(file.getInputStream(), ActivityTargetAudienceExcelDto.class, listener)
                    .sheet()
                    .doReadSync();  // 使用同步读取方式，确保读取所有行

            // 如果没有数据，则返回提示
            if (audienceList.isEmpty()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入数据为空\"}");
                return;
            }

            // 检查是否存在错误
            boolean hasErrors = audienceList.stream()
                    .anyMatch(dto -> dto.getErrorMessages() != null && !dto.getErrorMessages().isEmpty());

            if (hasErrors) {
                // 如果有错误，生成错误文件并返回
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=error_data.xlsx");

                // 调用 EasyExcel 写入错误数据
                EasyExcel.write(response.getOutputStream(), ActivityTargetAudienceExcelDto.class)
                        .registerWriteHandler(new ErrorCellStyleHandler(errorDataList))
                        .sheet("错误数据")
                        .doWrite(audienceList);
                return;
            }

            // 使用 mapToEntity 将 DTO 转换为实体列表
            List<ActivityTargetAudience> audienceEntities = audienceList.stream()
                    .map(this::mapToActivityTargetAudience)  // 调用 mapToEntity 方法
                    .collect(Collectors.toList());

            // 批量保存或更新
            if (!audienceEntities.isEmpty()) {
                activityTargetAudienceService.saveOrUpdateBatch(audienceEntities);
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
    public void exportActivityTargetAudienceExcel(HttpServletResponse response) {
        try {
            // 查询数据库中的 ActivityTargetAudience 数据
            List<ActivityTargetAudience> audienceList = activityTargetAudienceService.list();

            if (audienceList.isEmpty()) {
                throw new RuntimeException("无数据可导出");
            }

            // 将实体对象转换为 DTO 对象
            List<ActivityTargetAudienceExcelDto> audienceDtoList = audienceList.stream()
                    .map(this::mapToActivityTargetAudienceExcelDto)
                    .collect(Collectors.toList());

            // 设置响应头，确保文件正确下载
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("活动发送人群", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 写入数据到响应流
            EasyExcel.write(response.getOutputStream(), ActivityTargetAudienceExcelDto.class)
                    .sheet("目标受众信息")
                    .doWrite(audienceDtoList);
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
        String standardFilePath = uploadDir + "activity_target_audience_standard.xlsx";

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
            String fileName = URLEncoder.encode("发送人群模板", "UTF-8").replaceAll("\\+", "%20");
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
     * 将 ActivityTargetAudienceExcelDto 转化为 ActivityTargetAudience 实体对象
     */
    private ActivityTargetAudience mapToActivityTargetAudience(ActivityTargetAudienceExcelDto dto) {
        ActivityTargetAudience entity = new ActivityTargetAudience();
        entity.setId(dto.getId());
        entity.setAudienceLabel(dto.getAudienceLabel());
        entity.setAudienceValue(dto.getAudienceValue());
        entity.setMajor(dto.getMajor());
        return entity;
    }

    /**
     * 将 ActivityTargetAudience 实体对象 转化为 ActivityTargetAudienceExcelDto
     */
    private ActivityTargetAudienceExcelDto mapToActivityTargetAudienceExcelDto(ActivityTargetAudience entity) {
        ActivityTargetAudienceExcelDto dto = new ActivityTargetAudienceExcelDto();
        dto.setId(entity.getId());
        dto.setAudienceLabel(entity.getAudienceLabel());
        dto.setAudienceValue(entity.getAudienceValue());
        dto.setMajor(entity.getMajor());
        return dto;
    }

}
