package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.ActivityImage;
import com.tec.campuscareerbackend.entity.EmploymentDatabaseAttachment;
import com.tec.campuscareerbackend.mapper.EmploymentDatabaseAttachmentMapper;
import com.tec.campuscareerbackend.service.IEmploymentDatabaseAttachmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-04
 */
@Service
public class EmploymentDatabaseAttachmentServiceImpl extends ServiceImpl<EmploymentDatabaseAttachmentMapper, EmploymentDatabaseAttachment> implements IEmploymentDatabaseAttachmentService {

    @Override
    public List<EmploymentDatabaseAttachment> getAttachmentsByDatabaseId(Integer databaseId) {
        // 获取附件
        return this.lambdaQuery().eq(EmploymentDatabaseAttachment::getEmploymentDatabaseId, databaseId).list();
    }

    @Override
    public EmploymentDatabaseAttachment addAttachment(Integer databaseId, String filePath) {
        // 添加附件
        EmploymentDatabaseAttachment attachment = new EmploymentDatabaseAttachment();
        attachment.setEmploymentDatabaseId(databaseId);
        attachment.setFilePath(filePath);
        this.save(attachment);
        return attachment;
    }

    @Override
    public EmploymentDatabaseAttachment deleteAllAttachment(Integer databaseId) {
        // 删除所有附件
        this.lambdaQuery().eq(EmploymentDatabaseAttachment::getEmploymentDatabaseId, databaseId).list().forEach(this::removeById);
        return null;
    }

}
