package com.tec.campuscareerbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.EmploymentDatabase;
import com.tec.campuscareerbackend.entity.EmploymentDatabaseAttachment;
import com.tec.campuscareerbackend.service.IActivityService;
import com.tec.campuscareerbackend.service.IEmploymentDatabaseAttachmentService;
import com.tec.campuscareerbackend.service.IEmploymentDatabaseService;
import jakarta.annotation.Resource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@RestController
@RequestMapping("/employment-database")
public class EmploymentDatabaseController {
    @Resource
    private IEmploymentDatabaseService employmentDatabaseService;
    @Resource
    private IEmploymentDatabaseAttachmentService employmentDatabaseAttachmentService; // 用于保存附件路径

    // 通过构建一个分页查询接口，实现获取employment-database表中所有数据的接口
    @GetMapping
    public R<Page<EmploymentDatabase>> getAll(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<EmploymentDatabase> employmentDatabasePage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<EmploymentDatabase> result = employmentDatabaseService.page(employmentDatabasePage);

        // 查询附件
        for (EmploymentDatabase employmentDatabase : result.getRecords()) {
            employmentDatabase.setAttachment(employmentDatabaseAttachmentService.getAttachmentsByDatabaseId(employmentDatabase.getId()));
        }

        return R.ok(result);
    }

    // 根据ID查询就业信息
    @GetMapping("/{id}")
    public R<EmploymentDatabase> getEmploymentDatabaseById(@PathVariable Long id) {
        EmploymentDatabase employmentDatabase = employmentDatabaseService.getById(id);
        employmentDatabase.setAttachment(employmentDatabaseAttachmentService.getAttachmentsByDatabaseId(employmentDatabase.getId()));
        return R.ok(employmentDatabase);
    }

    // 添加就业信息
    @PostMapping
    public R<EmploymentDatabase> addEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        System.out.println(employmentDatabase);
        employmentDatabaseService.save(employmentDatabase);
        // 保存附件
        List<EmploymentDatabaseAttachment> attachment = employmentDatabase.getAttachment();
        if (attachment != null) {
            for (EmploymentDatabaseAttachment employmentDatabaseAttachment : attachment) {
                employmentDatabaseAttachment.setEmploymentDatabaseId(employmentDatabase.getId());
                employmentDatabaseAttachmentService.save(employmentDatabaseAttachment);
            }
        }

        return R.ok(employmentDatabase);
    }

    // 删除就业信息
    @DeleteMapping
    public R<EmploymentDatabase> deleteEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        employmentDatabaseService.removeById(employmentDatabase.getId());
        employmentDatabaseAttachmentService.deleteAllAttachment(employmentDatabase.getId());
        return R.ok(employmentDatabase);
    }

    // 更新就业信息
    @PutMapping
    public R<EmploymentDatabase> updateEmploymentDatabase(@RequestBody EmploymentDatabase employmentDatabase) {
        employmentDatabaseService.updateById(employmentDatabase);
        employmentDatabaseAttachmentService.deleteAllAttachment(employmentDatabase.getId());
        // 保存附件
        List<EmploymentDatabaseAttachment> attachment = employmentDatabase.getAttachment();
        if (attachment != null) {
            for (EmploymentDatabaseAttachment employmentDatabaseAttachment : attachment) {
                employmentDatabaseAttachment.setEmploymentDatabaseId(employmentDatabase.getId());
                employmentDatabaseAttachmentService.save(employmentDatabaseAttachment);
            }
        }

        return R.ok(employmentDatabase);
    }

    // 批量删除就业信息
    @DeleteMapping("/batch")
    public R<String> deleteEmploymentDatabaseBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("删除失败，ID列表不能为空！");
        }
        for (Integer id : ids) {
            employmentDatabaseService.removeById(id);
            employmentDatabaseAttachmentService.deleteAllAttachment(id);
        }
        return R.ok("删除成功！");
    }

    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadAttachmentsZip(@RequestBody EmploymentDatabase employmentDatabase) {
        List<EmploymentDatabaseAttachment> urls = employmentDatabase.getAttachment();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream)) {
            // 设置 ZipOutputStream 的编码为 UTF-8
            zipOutputStream.setEncoding("UTF-8");

            for (EmploymentDatabaseAttachment url : urls) {
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

    // 搜索就业信息
    @GetMapping("/search")
    public R<Page<EmploymentDatabase>> searchEmploymentDatabase(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<EmploymentDatabase> employmentDatabasePage = new Page<>(page, size);
        QueryWrapper<EmploymentDatabase> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "category":
                    queryWrapper.like("category", filterValue);
                    break;
                case "title":
                    queryWrapper.like("title", filterValue);
                    break;
                case "attachment":
                    queryWrapper.like("attachment", filterValue);
                    break;
                case "details":
                    queryWrapper.like("details", filterValue);
                    break;
                case "createdAt":
                    queryWrapper.like("created_at", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<EmploymentDatabase> result = employmentDatabaseService.page(employmentDatabasePage, queryWrapper);
        return R.ok(result);
    }
}
