package com.tec.campuscareerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.entity.UserDetail;
import com.tec.campuscareerbackend.mapper.ActivityTargetAudienceMapper;
import com.tec.campuscareerbackend.mapper.EmploymentSearchMapper;
import com.tec.campuscareerbackend.mapper.JobSearchMapper;
import com.tec.campuscareerbackend.mapper.UserDetailMapper;
import com.tec.campuscareerbackend.service.IJobSearchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
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
    @Autowired
    private EmploymentSearchMapper employmentSearchMapper;
    @Autowired
    private ActivityTargetAudienceMapper activityTargetAudienceMapper;

    private static final Map<String, String> CLASS_MAJOR_MAP = new HashMap<>();

    @PostConstruct
    public void initClassMajorMap() {
        // 从数据库中加载 audienceValue 和 major 的对应关系
        List<Map<String, String>> audienceMajorList = activityTargetAudienceMapper.getAudienceMajorMapping();
        for (Map<String, String> entry : audienceMajorList) {
            String audienceValue = entry.get("audienceValue");
            String major = entry.get("major");
            if (audienceValue != null && major != null) {
                CLASS_MAJOR_MAP.put(audienceValue, major);
            }
        }
    }

    // 提供一个方法获取 CLASS_MAJOR_MAP
    public Map<String, String> getClassMajorMap() {
        return CLASS_MAJOR_MAP;
    }

    @Override
    public Page<JobSearch> matchJobsByStudentId(String studentId, int page, int size) {
        // 1. 通过 studentId 查询出对应的 className
        UserDetail userDetail = userDetailMapper.selectOne(new QueryWrapper<UserDetail>().eq("student_id", studentId));
        if (userDetail == null) {
            return new Page<>(); // 若未找到对应用户，返回空结果
        }
        String className = userDetail.getClassName();
        System.out.println("className: " + className);

        // 2. 根据 className 查找对应的专业
        String major = CLASS_MAJOR_MAP.get(className);
        System.out.println("major: " + major);
        if (major == null) {
            return new Page<>(); // 若 className 未匹配到对应专业，返回空结果
        }

        // 3. 创建查询条件
        Page<JobSearch> jobSearchPage = new Page<>(page, size);
        QueryWrapper<JobSearch> queryWrapper = new QueryWrapper<>();

        // 专业筛选条件
        queryWrapper.like("major_requirement", major);

        // 4. 获取用户想要的薪资
        EmploymentSearch employmentSearch = employmentSearchMapper.selectOne(new QueryWrapper<EmploymentSearch>().eq("student_id", studentId));
        Integer lowSalaryValue = null;
        if (employmentSearch != null) {
            String money = employmentSearch.getSalary();
            System.out.println("money: " + money);

            // 根据‘/’分割字符串，取第一个数字作为最低薪资
            String[] moneyArray = money.split("/");
            String lowMoney = moneyArray[0];
            System.out.println("lowMoney: " + lowMoney);

            // 定义薪资范围映射
            Map<String, Integer> salaryMap = new HashMap<>();
            salaryMap.put("2000-5000", 2000);
            salaryMap.put("5000-8000", 5000);
            salaryMap.put("8000-15000", 8000);
            salaryMap.put("15000以上", 15000);

            // 获取用户最低薪资值
            lowSalaryValue = salaryMap.get(lowMoney);
        }

        // 5. 获取用户想要的工作地点
        String[] areaArray = null;
        if (employmentSearch != null) {
            String area = employmentSearch.getWorkLocation();
            System.out.println("area: " + area);

            // 根据‘/’分割字符串，获取用户想要的工作地点
            areaArray = area.split("/");
        }

        // 6. 薪资从高到低排序
        queryWrapper.orderByDesc("CASE " +
                "WHEN money = '2000-5000' THEN 2000 " +
                "WHEN money = '5000-8000' THEN 5000 " +
                "WHEN money = '8000-15000' THEN 8000 " +
                "WHEN money = '15000以上' THEN 15000 " +
                "ELSE 0 END");

        // 7. 查询数据
        Page<JobSearch> resultPage = this.page(jobSearchPage, queryWrapper);

        // 8. 逐条计算 matchLevel 并设置星级
        for (JobSearch job : resultPage.getRecords()) {
            int matchCount = 0;

            // 专业匹配
            if (job.getMajorRequirement() != null && job.getMajorRequirement().contains(major)) {
                matchCount++;
            }

            // 薪资匹配
            if (lowSalaryValue != null) {
                int jobSalary = 0;
                switch (job.getMoney()) {
                    case "2000-5000": jobSalary = 2000; break;
                    case "5000-8000": jobSalary = 5000; break;
                    case "8000-15000": jobSalary = 8000; break;
                    case "15000以上": jobSalary = 15000; break;
                }
                if (jobSalary >= lowSalaryValue) {
                    matchCount++;
                }
            }

            // 地点匹配
            if (areaArray != null && job.getArea() != null) {
                for (String area : areaArray) {
                    if (job.getArea().contains(area)) {
                        matchCount++;
                        break;
                    }
                }
            }

            // 根据匹配数量设置星级
            String matchLevel = switch (matchCount) {
                case 1 -> "🌟";
                case 2 -> "🌟🌟";
                case 3 -> "🌟🌟🌟";
                default -> "";
            };
            job.setMatchLevel(matchLevel); // 假设 JobSearch 有 matchLevel 字段
        }

        // 9. 返回结果
        return resultPage;
    }
}