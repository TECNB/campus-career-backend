package com.tec.campuscareerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.entity.UserDetail;
import com.tec.campuscareerbackend.mapper.JobSearchMapper;
import com.tec.campuscareerbackend.mapper.UserDetailMapper;
import com.tec.campuscareerbackend.service.IJobSearchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 岗位发布详情表 服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-01
 */
@Service
public class JobSearchServiceImpl extends ServiceImpl<JobSearchMapper, JobSearch> implements IJobSearchService {

    @Autowired
    private UserDetailMapper userDetailMapper;

    private static final Map<String, String> CLASS_MAJOR_MAP = new HashMap<>();

    static {
        // 建立 className 和专业的对应关系
        CLASS_MAJOR_MAP.put("电子2101", "电气自动化");
        CLASS_MAJOR_MAP.put("电子2102", "电气自动化");
        CLASS_MAJOR_MAP.put("电子2103", "电气自动化");
        CLASS_MAJOR_MAP.put("电子2104", "电气自动化");
        CLASS_MAJOR_MAP.put("电（专）2301", "电气自动化");
        CLASS_MAJOR_MAP.put("电（专）2302", "电气自动化");

        CLASS_MAJOR_MAP.put("计算机2101", "计算机科学");
        CLASS_MAJOR_MAP.put("计算机2102", "计算机科学");
        CLASS_MAJOR_MAP.put("计算机2103", "计算机科学");
        CLASS_MAJOR_MAP.put("计算机2104", "计算机科学");
        CLASS_MAJOR_MAP.put("计（专）2301", "计算机科学");
        CLASS_MAJOR_MAP.put("计（专）2302", "计算机科学");
        CLASS_MAJOR_MAP.put("软（专）2301", "计算机科学");
        CLASS_MAJOR_MAP.put("软（专）2302", "计算机科学");

        CLASS_MAJOR_MAP.put("软件2101", "软件工程");
        CLASS_MAJOR_MAP.put("软件2102", "软件工程");
        CLASS_MAJOR_MAP.put("软（专）2301", "软件工程");
        CLASS_MAJOR_MAP.put("软（专）2302", "软件工程");

        CLASS_MAJOR_MAP.put("自动化2101", "机械自动化");
        CLASS_MAJOR_MAP.put("自动化2102", "机械自动化");
        CLASS_MAJOR_MAP.put("自（专）2301", "机械自动化");
        CLASS_MAJOR_MAP.put("自（专）2302", "机械自动化");
    }

    @Override
    public Page<JobSearch> matchJobsByStudentId(String studentId, int page, int size) {
        // 1. 通过 studentId 查询出对应的 className
        UserDetail userDetail = userDetailMapper.selectOne(new QueryWrapper<UserDetail>().eq("student_id", studentId));
        if (userDetail == null) {
            return new Page<>(); // 若未找到对应用户，返回空结果
        }
        String className = userDetail.getClassName();

        // 2. 根据 className 查找对应的专业
        String major = CLASS_MAJOR_MAP.get(className);
        System.out.println("major: " + major);
        if (major == null) {
            return new Page<>(); // 若 className 未匹配到对应专业，返回空结果
        }

        // 3. 创建查询条件，基于 major 模糊匹配 job_search 表中的 majorRequirement
        Page<JobSearch> jobSearchPage = new Page<>(page, size);
        QueryWrapper<JobSearch> queryWrapper = new QueryWrapper<>();

        // 4. 使用 FIND_IN_SET 来处理多个专业的匹配
        queryWrapper.like("major_requirement", major);
//        queryWrapper.apply("FIND_IN_SET(?, REPLACE(major_requirement, '/', ','))", major);

        // TODO: 薪资高的岗位排在前面，浙江省的岗位排在前面

        return this.page(jobSearchPage, queryWrapper);
    }
}