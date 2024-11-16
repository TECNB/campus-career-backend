package com.tec.campuscareerbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 岗位发布详情表 服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
public interface IJobSearchService extends IService<JobSearch> {

    Page<JobSearch> matchJobsByStudentId(String studentId, int page, int size);
}
