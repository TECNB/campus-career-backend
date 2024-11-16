package com.tec.campuscareerbackend.mapper;

import com.tec.campuscareerbackend.entity.JobSearch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 岗位发布详情表 Mapper 接口
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
public interface JobSearchMapper extends BaseMapper<JobSearch> {
    @Select("SELECT * FROM job_search WHERE FIND_IN_SET(#{className}, REPLACE(major_requirement, '/', ','))")
    List<JobSearch> findJobsByClassName(@Param("className") String className);
}
