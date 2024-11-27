package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.UserInfoExcelDto;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.service.IUserInfoService;
import com.tec.campuscareerbackend.service.IUsersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private IUsersService usersService;

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

    // 搜索用户信息
    @GetMapping("/search")
    public R<Page<UserInfo>> searchUserInfo(
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserInfo> pageRequest = new Page<>(page, size);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        // 根据字段名动态添加查询条件
        if (filterField != null && filterValue != null) {
            switch (filterField) {
                case "studentId":
                    queryWrapper.eq("student_id", filterValue);
                    break;
                case "idCard":
                    queryWrapper.eq("id_card", filterValue);
                    break;
                case "grade":
                    queryWrapper.like("grade", filterValue);
                    break;
                case "major":
                    queryWrapper.like("major", filterValue);
                    break;
                case "className":
                    queryWrapper.like("class_name", filterValue);
                    break;
                case "classRole":
                    queryWrapper.like("class_role", filterValue);
                    break;
                case "specialization":
                    queryWrapper.like("specialization", filterValue);
                    break;
                case "birthDate":
                    queryWrapper.eq("birth_date", filterValue);
                    break;
                case "admissionDate":
                    queryWrapper.eq("admission_date", filterValue);
                    break;
                case "expectedGraduation":
                    queryWrapper.eq("expected_graduation", filterValue);
                    break;
                case "nativePlace":
                    queryWrapper.like("native_place", filterValue);
                    break;
                case "sourcePlace":
                    queryWrapper.like("source_place", filterValue);
                    break;
                case "ethnicity":
                    queryWrapper.like("ethnicity", filterValue);
                    break;
                case "residence":
                    queryWrapper.like("residence", filterValue);
                    break;
                case "homeAddress":
                    queryWrapper.like("home_address", filterValue);
                    break;
                case "counselor":
                    queryWrapper.like("counselor", filterValue);
                    break;
                case "counselorPhone":
                    queryWrapper.eq("counselor_phone", filterValue);
                    break;
                case "classTeacher":
                    queryWrapper.like("class_teacher", filterValue);
                    break;
                case "classTeacherPhone":
                    queryWrapper.eq("class_teacher_phone", filterValue);
                    break;
                case "graduationTutor":
                    queryWrapper.like("graduation_tutor", filterValue);
                    break;
                case "graduationTutorPhone":
                    queryWrapper.eq("graduation_tutor_phone", filterValue);
                    break;
                case "dormitoryNumber":
                    queryWrapper.like("dormitory_number", filterValue);
                    break;
                case "networkStatus":
                    queryWrapper.eq("network_status", filterValue);
                    break;
                case "dormitoryMembers":
                    queryWrapper.like("dormitory_members", filterValue);
                    break;
                default:
                    return R.error("无效的筛选字段");
            }
        }

        Page<UserInfo> result = userInfoService.page(pageRequest, queryWrapper);
        return R.ok(result);
    }

    // 批量删除用户信息
    @DeleteMapping("/batch")
    public R<String> deleteUserInfoBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("ID列表为空");
        }
        userInfoService.removeByIds(ids);
        return R.ok("删除成功");
    }

    @PostMapping("/importExcel")
    public R<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 定义日期格式解析器
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");

            // 使用 EasyExcel 读取 Excel 数据
            List<UserInfoExcelDto> userList = EasyExcel.read(file.getInputStream())
                    .head(UserInfoExcelDto.class)
                    .sheet()
                    .doReadSync()
                    .stream()
                    .map(dto -> (UserInfoExcelDto) dto) // 确保类型转换
                    .filter(dto -> dto.getName() != null && !dto.getName().isEmpty()) // 过滤空白行
                    .collect(Collectors.toList());

            if (userList.isEmpty()) {
                return R.ok("导入数据为空");
            }

            // DTO 转换为实体
            List<UserInfo> userInfoList = userList.stream()
                    .map(dto -> mapToUserInfo(dto, dateFormatter))
                    .collect(Collectors.toList());

            // 批量保存 UserInfo
            userInfoService.saveOrUpdateBatch(userInfoList);

            // 批量初始化 Users
            List<Users> usersList = userList.stream()
                    .map(dto -> {
                        try {
                            return mapToUsers(dto);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            usersService.saveOrUpdateBatch(usersList);

            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 将 DTO 转换为 UserInfo 实体
     */
    private UserInfo mapToUserInfo(UserInfoExcelDto dto, DateTimeFormatter dateFormatter) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(dto.getId());
        userInfo.setName(dto.getName());
        userInfo.setPhone(dto.getPhone());
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
        return userInfo;
    }

    /**
     * 将 DTO 转换为 Users 实体
     */
    private Users mapToUsers(UserInfoExcelDto dto) throws NoSuchAlgorithmException {
        Users user = new Users();
        user.setStudentId(dto.getStudentId());
        user.setUsername(dto.getName());

        // 生成初始密码为学号后6位
        String initialPassword = dto.getStudentId().substring(dto.getStudentId().length() - 6);
        String salt = generateSalt();
        String passwordHash = encryptHv(initialPassword, salt);

        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        user.setUserType("student");
        user.setPhone(dto.getCounselorPhone());
        return user;
    }

}
