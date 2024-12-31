package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.SpecialGroupAttachment;
import com.tec.campuscareerbackend.mapper.SpecialGroupAttachmentMapper;
import com.tec.campuscareerbackend.service.ISpecialGroupAttachmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 特殊学生附件表 服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-12-12
 */
@Service
public class SpecialGroupAttachmentServiceImpl extends ServiceImpl<SpecialGroupAttachmentMapper, SpecialGroupAttachment> implements ISpecialGroupAttachmentService {

    @Override
    public SpecialGroupAttachment deleteAllAttachment(String studentId) {
        // 删除所有附件
        this.lambdaQuery().eq(SpecialGroupAttachment::getStudentId, studentId).list().forEach(this::removeById);
        return null;
    }
    @Override
    public List<SpecialGroupAttachment> getAttachmentsByStudentId(String studentId) {
        // 获取附件
        return this.lambdaQuery().eq(SpecialGroupAttachment::getStudentId, studentId).list();
    }
}
