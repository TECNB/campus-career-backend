package com.tec.campuscareerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.entity.EmploymentSearch;
import com.tec.campuscareerbackend.entity.JobSearch;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.mapper.ActivityTargetAudienceMapper;
import com.tec.campuscareerbackend.mapper.EmploymentSearchMapper;
import com.tec.campuscareerbackend.mapper.JobSearchMapper;
import com.tec.campuscareerbackend.mapper.UserInfoMapper;
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
    private UserInfoMapper userInfoMapper;
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
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("student_id", studentId));
        if (userInfo == null) {
            return new Page<>(); // 若未找到对应用户，返回空结果
        }
        String className = userInfo.getClassName();

        // 2. 根据 className 查找对应的专业
        String major = CLASS_MAJOR_MAP.get(className);
        if (major == null) {
            return new Page<>(); // 若 className 未匹配到对应专业，返回空结果
        }

        // 3. 创建分页和查询条件
        Page<JobSearch> jobSearchPage = new Page<>(page, size);
        QueryWrapper<JobSearch> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                wrapper.like("major_requirement", major).or().eq("major_requirement", "无")
        );

        // 查询数据
        Page<JobSearch> resultPage = this.page(jobSearchPage, queryWrapper);

        // 4. 获取用户偏好（薪资、地点）
        EmploymentSearch employmentSearch = employmentSearchMapper.selectOne(new QueryWrapper<EmploymentSearch>().eq("student_id", studentId));
        Integer lowSalaryValue = null;
        String[] areaArray = null;

        if (employmentSearch != null) {
            // 解析薪资
            Map<String, Integer> salaryMap = Map.of(
                    "2000-5000", 2000,
                    "5000-8000", 5000,
                    "8000-15000", 8000,
                    "15000以上", 15000
            );
            String money = employmentSearch.getSalary();
            if (money != null) {
                String[] moneyArray = money.split("/");
                lowSalaryValue = salaryMap.getOrDefault(moneyArray[0], null);
            }

            // 解析地点
            String area = employmentSearch.getWorkLocation();
            if (area != null) {
                areaArray = area.split("/");
            }
        }

        // 5. 在内存中计算 matchLevel 并排序
        List<JobSearch> records = resultPage.getRecords();
        for (JobSearch job : records) {
            int matchCount = 0;

            // 专业匹配
            if ("无".equals(job.getMajorRequirement()) ||
                    (job.getMajorRequirement() != null && job.getMajorRequirement().contains(major))) {
                matchCount++;
            }

            // 薪资匹配
            if (lowSalaryValue != null) {
                int jobSalary = switch (job.getMoney()) {
                    case "2000-5000" -> 2000;
                    case "5000-8000" -> 5000;
                    case "8000-15000" -> 8000;
                    case "15000以上" -> 15000;
                    default -> 0;
                };
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

            // 设置匹配等级
            String matchLevel = switch (matchCount) {
                case 1 -> "🌟";
                case 2 -> "🌟🌟";
                case 3 -> "🌟🌟🌟";
                default -> "";
            };
            job.setMatchLevel(matchLevel);
            job.setMatchCount(matchCount); // 添加 matchCount 作为排序依据
        }



        // 按 matchCount 排序，若 matchCount 相等，按 money 降序排序
        records.sort((o1, o2) -> {
            int matchCompare = Integer.compare(o2.getMatchCount(), o1.getMatchCount());
            if (matchCompare != 0) {
                return matchCompare;
            }
            // 若 matchCount 相等，按 money 降序排序
            int salary1 = getSalaryValue(o1.getMoney());
            int salary2 = getSalaryValue(o2.getMoney());
            return Integer.compare(salary2, salary1);
        });

        // 6. 更新结果并返回
        resultPage.setRecords(records);
        return resultPage;
    }
    private int getSalaryValue(String salaryRange) {
        return switch (salaryRange) {
            case "2000-5000" -> 2000;
            case "5000-8000" -> 5000;
            case "8000-15000" -> 8000;
            case "15000以上" -> 15000;
            default -> 0;
        };
    }


}