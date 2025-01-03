package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.Activity;
import com.tec.campuscareerbackend.entity.ActivityImage;
import com.tec.campuscareerbackend.mapper.ActivityImageMapper;
import com.tec.campuscareerbackend.service.IActivityImageService;
import com.tec.campuscareerbackend.service.IActivityService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import static com.tec.campuscareerbackend.utils.Utils.generateHash;

/**
 * <p>
 * 活动信息表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    // 定义文件存储的目录路径（可以通过 application.yml 或 application.properties 配置）
    @Value("${file.upload-dir}")
    private String uploadDir;
    // 定义服务器的 URL 基础路径，用于生成完整文件路径
    @Value("${server.base-url}")
    private String serverBaseUrl;

    @Resource
    private IActivityService activityService;
    @Resource
    private IActivityImageService activityImageService; // 用于保存图片路径

    // 通过构建一个分页查询接口，实现获取activity表中所有数据的接口
    @GetMapping
    public R<Page<Activity>> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Page<Activity> activityPage = new Page<>(page, size);
        Page<Activity> result = activityService.page(activityPage);
        return R.ok(result);
    }

    // 根据ID查询活动
    @GetMapping("/{id}")
    public R<Activity> getActivityById(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        // 查询活动图片
        List<String> images = activityImageService.getActivityImagesByActivityId(id);
        activity.setImagePaths(images);
        return R.ok(activity);
    }


    // 添加活动
    @PostMapping
    public R<Activity> addActivity(@RequestBody Activity activity) {
        System.out.println(activity);
        activityService.save(activity);

        // 通过activityImageService保存图片路径
        List<String> images = activity.getImagePaths();
        for (String image : images) {
            activityImageService.addActivityImage(activity.getId(), image);
        }
        return R.ok(activity);
    }

    // 删除活动
    @DeleteMapping
    public R<Activity> deleteActivity(@RequestBody Activity activity) {
        activityService.removeById(activity.getId());
        // 删除活动图片
        activityImageService.deleteAllActivityImage(activity.getId());

        return R.ok(activity);
    }

    // 修改活动
    @PutMapping
    public R<Activity> updateActivity(@RequestBody Activity activity) {
        activityService.updateById(activity);
        // 删除原有图片
        activityImageService.deleteAllActivityImage(activity.getId());
        // 保存新图片
        List<String> images = activity.getImagePaths();
        for (String image : images) {
            activityImageService.addActivityImage(activity.getId(), image);
        }

        return R.ok(activity);
    }

    // 批量删除活动发布详情
    @DeleteMapping("/batch")
    public R<String> deleteActivityBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("删除失败，ID列表不能为空！");
        }
        boolean result = activityService.removeByIds(ids);
        if (result) {
            return R.ok("删除成功！");
        } else {
            return R.error("删除失败！");
        }
    }

    // 上传多张活动照片到服务器并将路径存入数据库
    @PostMapping("/file")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("文件不能为空");
        }

        try {
            // 创建上传目录（如果不存在）
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 使用 UUID 部分值和时间戳生成短的唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueString = UUID.randomUUID().toString().substring(0, 8) + System.currentTimeMillis();
            String fileName = generateHash(uniqueString) + fileExtension;
            System.out.println("Generated File Name: " + fileName);

            // 保存文件到指定目录
            File uploadFile = new File(dir, fileName);
            file.transferTo(uploadFile);

            // 构建文件的完整 URL 路径
            String fileUrl = serverBaseUrl + "/" + fileName;
            System.out.println("File URL: " + fileUrl);

            return R.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败：" + e.getMessage());
        }
    }

    // 搜素活动
    @GetMapping("/search")
    public R<Page<Activity>> searchActivity(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<Activity> pageRequest = new Page<>(page, size);
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "name":
                    queryWrapper.like("name", filterValue);
                    break;
                case "startTime":
                    queryWrapper.like("start_time", filterValue);
                    break;
                case "endTime":
                    queryWrapper.like("end_time", filterValue);
                    break;
                case "place":
                    queryWrapper.like("place", filterValue);
                    break;
                case "participantCount":
                    queryWrapper.eq("participant_count", filterValue);
                    break;
                case "money":
                    queryWrapper.eq("money", filterValue);
                    break;
                case "nature":
                    queryWrapper.like("nature", filterValue);
                    break;
                case "area":
                    queryWrapper.like("area", filterValue);
                    break;
                case "jobPosition":
                    queryWrapper.like("job_position", filterValue);
                    break;
                case "targetAudience":
                    queryWrapper.like("target_audience", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<Activity> result = activityService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }
}
