package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.*;
import com.tec.campuscareerbackend.service.ISpecialGroupAttachmentService;
import com.tec.campuscareerbackend.service.ISpecialGroupService;
import com.tec.campuscareerbackend.service.IUserInfoService;
import jakarta.annotation.Resource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-23
 */
@RestController
@RequestMapping("/special-group")
public class SpecialGroupController {
    @Resource
    private ISpecialGroupService specialGroupService;
    @Resource
    private ISpecialGroupAttachmentService specialGroupAttachmentService; // 用于保存附件路径

    // 通过构建一个分页查询接口，实现获取special-group表中所有数据的接口
    @GetMapping
    public R<Page<SpecialGroup>> getAll(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<SpecialGroup> userInfoPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<SpecialGroup> result = specialGroupService.page(userInfoPage);

        // 查询附件
        for (SpecialGroup specialGroup : result.getRecords()) {
            specialGroup.setAttachment(specialGroupAttachmentService.getAttachmentsByStudentId(specialGroup.getStudentId()));
        }

        return R.ok(result);
    }

    // 根据ID查询特殊群体信息
    @GetMapping("/{id}")
    public R<SpecialGroup> getSpecialGroupById(@PathVariable String id) {
        SpecialGroup specialGroup = new SpecialGroup();
        specialGroup.setAttachment(specialGroupAttachmentService.getAttachmentsByStudentId(id));
        return R.ok(specialGroup);
    }

    // 添加特殊群体信息
    @PostMapping
    public R<SpecialGroup> addSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        // 保存附件
        List<SpecialGroupAttachment> attachment = specialGroup.getAttachment();
        if (attachment != null) {
            for (SpecialGroupAttachment specialGroupAttachment : attachment) {
                specialGroupAttachment.setStudentId(specialGroup.getStudentId());
                specialGroupAttachmentService.save(specialGroupAttachment);
            }
        }

        return R.ok(specialGroup);
    }

    // 删除特殊群体信息
    @DeleteMapping
    public R<String> deleteSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        specialGroupService.removeById(specialGroup);
        specialGroupAttachmentService.deleteAllAttachment(specialGroup.getStudentId());
        return R.ok("删除成功");
    }

    // 更新特殊群体信息
    @PutMapping
    public R<SpecialGroup> updateSpecialGroup(@RequestBody SpecialGroup specialGroup) {
        specialGroupAttachmentService.deleteAllAttachment(specialGroup.getStudentId());
        // 保存附件
        List<SpecialGroupAttachment> attachment = specialGroup.getAttachment();
        if (attachment != null) {
            for (SpecialGroupAttachment specialGroupAttachment : attachment) {
                specialGroupAttachment.setStudentId(specialGroup.getStudentId());
                specialGroupAttachmentService.save(specialGroupAttachment);
            }
        }
        return R.ok(specialGroup);
    }

    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadAttachmentsZip(@RequestBody SpecialGroup specialGroup){
        List<SpecialGroupAttachment> urls = specialGroup.getAttachment();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream)) {
            // 设置 ZipOutputStream 的编码为 UTF-8
            zipOutputStream.setEncoding("UTF-8");

            for (SpecialGroupAttachment url : urls) {
                URL fileUrl = new URL(url.getFilePath());
                try (InputStream inputStream = fileUrl.openStream()) {
                    // 使用 UTF-8 编码的文件名
                    ZipArchiveEntry entry = new ZipArchiveEntry(url.getFileName());
                    zipOutputStream.putArchiveEntry(entry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                    zipOutputStream.closeArchiveEntry();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attachments.zip");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(byteArrayResource.contentLength())
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

}
