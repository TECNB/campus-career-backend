package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.UserInfoExcelDto;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.service.IUserInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.tec.campuscareerbackend.utils.Utils.*;
import static com.tec.campuscareerbackend.utils.Utils.parseDate;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-22
 */
@RestController
@RequestMapping("/user-info")
public class UserInfoController {
    @Resource
    private IUserInfoService userInfoService;

    // 通过构建一个分页查询接口，实现获取user-info表中所有数据的接口
    @GetMapping
    public R<Page<UserInfo>> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        // 创建分页对象
        Page<UserInfo> userInfoPage = new Page<>(page, size);
        // 使用 MyBatis Plus 进行分页查询
        Page<UserInfo> result = userInfoService.page(userInfoPage);
        return R.ok(result);
    }

    // 根据ID查询用户信息
    @GetMapping("/{id}")
    public R<UserInfo> getUserInfoById(@PathVariable String id) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", id);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        return R.ok(userInfo);
    }

    // 添加用户信息
    @PostMapping
    public R<UserInfo> addUserInfo(@RequestBody UserInfo userInfo) {
        userInfoService.save(userInfo);
        return R.ok(userInfo);
    }

    // 删除用户信息
    @DeleteMapping
    public R<UserInfo> deleteUserInfo(@RequestBody UserInfo userInfo) {
        userInfoService.removeById(userInfo.getId());
        return R.ok(userInfo);
    }

    // 更新用户信息
    @PutMapping
    public R<UserInfo> updateUserInfo(@RequestBody UserInfo userInfo) {
        userInfoService.updateById(userInfo);
        return R.ok(userInfo);
    }

    @PostMapping("/importExcel")
    public R<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 定义日期格式解析器
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 读取Excel数据并解析为 UserInfoExcelDto 对象列表
            List<UserInfoExcelDto> userList = EasyExcel.read(file.getInputStream())
                    .head(UserInfoExcelDto.class)
                    .sheet()
                    .doReadSync();

            for (UserInfoExcelDto dto : userList) {
                // 判断是否为空白行
                if (dto.getName() == null || dto.getName().isEmpty()) {
                    continue; // 跳过空白行
                }

                // 保存到 user_info 表
                UserInfo userInfo = new UserInfo();
                userInfo.setId(dto.getId());
                userInfo.setName(dto.getName());
                userInfo.setGender(dto.getGender());
                userInfo.setStudentId(dto.getStudentId());
                userInfo.setIdCard(dto.getIdCard());
                userInfo.setGrade(dto.getGrade());
                userInfo.setMajor(dto.getMajor());
                userInfo.setClassName(dto.getClassName());
                userInfo.setClassRole(dto.getClassRole());
                userInfo.setNativePlace(dto.getNativePlace());
                userInfo.setSourcePlace(dto.getSourcePlace());
                userInfo.setEthnicity(dto.getEthnicity());
                userInfo.setResidence(dto.getResidence());
                userInfo.setHomeAddress(dto.getHomeAddress());
                userInfo.setCounselor(dto.getCounselor());
                userInfo.setCounselorPhone(dto.getCounselorPhone());
                userInfo.setClassTeacher(dto.getClassTeacher());
                userInfo.setClassTeacherPhone(dto.getClassTeacherPhone());
                userInfo.setGraduationTutor(dto.getGraduationTutor());
                userInfo.setGraduationTutorPhone(dto.getGraduationTutorPhone());
                userInfo.setDormitoryNumber(dto.getDormitoryNumber());
                userInfo.setNetworkStatus(dto.getNetworkStatus());
                userInfo.setDormitoryMembers(dto.getDormitoryMembers());
                userInfo.setPoliticalStatus(dto.getPoliticalStatus());
                userInfo.setPartyProgress(dto.getPartyProgress());
                userInfo.setPartyTrainingProgress(dto.getPartyTrainingProgress());
                userInfo.setBranchName(dto.getBranchName());

                // 日期字段转换
                userInfo.setSpecialization(dto.getSpecialization());
                userInfo.setBirthDate(parseDate(dto.getBirthDate(), dateFormatter));
                userInfo.setAdmissionDate(parseDate(dto.getAdmissionDate(), dateFormatter));
                userInfo.setExpectedGraduation(parseDate(dto.getExpectedGraduation(), dateFormatter));
                userInfo.setApplicationDate(parseDate(dto.getApplicationDate(), dateFormatter));
                userInfo.setActivistDate(parseDate(dto.getActivistDate(), dateFormatter));
                userInfo.setDevelopmentDate(parseDate(dto.getDevelopmentDate(), dateFormatter));
                userInfo.setProbationaryDate(parseDate(dto.getProbationaryDate(), dateFormatter));
                userInfo.setFullMemberDate(parseDate(dto.getFullMemberDate(), dateFormatter));

                userInfo.setPartyHours(dto.getPartyHours());
                userInfo.setBranchSecretary(dto.getBranchSecretary());
                userInfo.setBranchDeputySecretary(dto.getBranchDeputySecretary());
                userInfoService.save(userInfo);

//                // 初始化保存到 users 表
//                Users user = new Users();
//                user.setStudentId(dto.getStudentId());
//                user.setUsername(dto.getName());
//
//                // 生成初始密码为学号后6位
//                String initialPassword = dto.getStudentId().substring(dto.getStudentId().length() - 6);
//                String salt = generateSalt();
//                String passwordHash = encryptHv(initialPassword, salt);
//
//                user.setPasswordHash(passwordHash);
//                user.setSalt(salt);
//                user.setUserType("student");
//                user.setPhone(dto.getCounselorPhone());
//                usersService.save(user);
            }
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

}
