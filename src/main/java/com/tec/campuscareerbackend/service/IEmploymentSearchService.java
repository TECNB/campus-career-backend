package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
public interface IEmploymentSearchService extends IService<EmploymentSearch> {
    EmploymentSearch getByUserId(Long userId);

}
