package com.tec.campuscareerbackend.controller;


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
import java.util.List;

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
        // 创建分页对象
        Page<Activity> activityPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
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

            // 随机生成文件名并确保唯一
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            System.out.println(fileName);

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
}
