package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.EmploymentDatabaseAttachment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-04
 */
public interface IEmploymentDatabaseAttachmentService extends IService<EmploymentDatabaseAttachment> {

    List<EmploymentDatabaseAttachment> getAttachmentsByDatabaseId(Integer databaseId);

    EmploymentDatabaseAttachment addAttachment(Integer databaseId, String filePath);

    EmploymentDatabaseAttachment deleteAllAttachment(Integer databaseId);
}
