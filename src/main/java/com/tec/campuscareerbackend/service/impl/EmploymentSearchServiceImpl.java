package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.tec.campuscareerbackend.mapper.EmploymentSearchMapper;
import com.tec.campuscareerbackend.service.IEmploymentSearchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@Service
public class EmploymentSearchServiceImpl extends ServiceImpl<EmploymentSearchMapper, EmploymentSearch> implements IEmploymentSearchService {

    @Override
    public EmploymentSearch getByUserId(Long userId) {
        // 根据用户id查询就业搜索信息
        return this.lambdaQuery().eq(EmploymentSearch::getStudentId, userId).one();
    }
}
