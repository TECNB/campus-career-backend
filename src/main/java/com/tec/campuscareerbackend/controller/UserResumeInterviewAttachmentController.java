package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.UserResumeInterviewAttachment;
import com.tec.campuscareerbackend.service.IUserResumeInterviewAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.tec.campuscareerbackend.utils.Utils.generateHash;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-07
 */
@RestController
@RequestMapping("/user-resume-interview-attachment")
public class UserResumeInterviewAttachmentController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.base-url}")
    private String serverBaseUrl;

    @Autowired
    private IUserResumeInterviewAttachmentService attachmentService;

    @GetMapping
    public R<List<UserResumeInterviewAttachment>> getResumeOrInterviewFile(
            @RequestParam("userId") String userId,
            @RequestParam("fileType") String fileType // "resume" or "interview"
    ) {
        if (!fileType.equals("resume") && !fileType.equals("interview")) {
            return R.error("文件类型必须为 'resume' 或 'interview'");
        }

        // 构建查询条件
        QueryWrapper<UserResumeInterviewAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("file_type", fileType);

        // 从数据库获取符合条件的文件列表
        List<UserResumeInterviewAttachment> files = attachmentService.list(queryWrapper);

        if (files.isEmpty()) {
            return R.error("未找到对应的文件");
        }

        return R.ok(files);
    }

    @PostMapping("/file")
    public R<String> uploadResumeOrInterviewFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("fileType") String fileType // "resume" or "interview"
    ) {
        if (file.isEmpty()) {
            return R.error("文件不能为空");
        }

        if (!fileType.equals("resume") && !fileType.equals("interview")) {
            return R.error("文件类型必须为 'resume' 或 'interview'");
        }

        try {
            // 创建上传目录（如果不存在）
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 使用 UUID 和时间戳生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueString = UUID.randomUUID().toString().substring(0, 8) + System.currentTimeMillis();
            String fileName = generateHash(uniqueString) + fileExtension;

            // 保存文件到指定目录
            File uploadFile = new File(dir, fileName);
            file.transferTo(uploadFile);

            // 构建文件的完整 URL 路径
            String fileUrl = serverBaseUrl + "/" + fileName;

            // 将文件信息保存到数据库
            UserResumeInterviewAttachment attachment = new UserResumeInterviewAttachment();
            attachment.setUserId(userId);
            attachment.setFilePath(fileUrl);
            attachment.setFileName(originalFilename);
            attachment.setFileType(fileType);
            attachmentService.save(attachment);

            return R.ok("文件上传成功：" + fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败：" + e.getMessage());
        }
    }
}
