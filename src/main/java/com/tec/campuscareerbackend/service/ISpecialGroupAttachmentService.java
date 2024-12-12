package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.EmploymentDatabaseAttachment;
import com.tec.campuscareerbackend.entity.SpecialGroupAttachment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 特殊学生附件表 服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-12-12
 */
public interface ISpecialGroupAttachmentService extends IService<SpecialGroupAttachment> {
    SpecialGroupAttachment deleteAllAttachment(Integer specialGroupId);

    List<SpecialGroupAttachment> getAttachmentsBySpecialGroupId(Integer specialGroupId);

}
