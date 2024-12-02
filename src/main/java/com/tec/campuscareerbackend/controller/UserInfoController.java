package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.UserInfoExcelDto;
import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.service.IUserInfoService;
import com.tec.campuscareerbackend.service.IUsersService;
import com.tec.campuscareerbackend.utils.ErrorCellStyleHandler;
import com.tec.campuscareerbackend.utils.ExcelImportListener;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tec.campuscareerbackend.utils.Utils.*;
import static com.tec.campuscareerbackend.utils.Utils.parseDate;
import static com.tec.campuscareerbackend.utils.Utils.formatDate;

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
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.base-url}")
    private String serverBaseUrl;

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
        System.out.println("ids"+ids);
        // 根据ids获取到studentIds
        List<String> studentIds = userInfoService.listByIds(ids).stream()
                .map(UserInfo::getStudentId)
                .collect(Collectors.toList());

        System.out.println("studentIds"+studentIds);
        if (ids == null || ids.isEmpty()) {
            return R.error("ID列表为空");
        }
        userInfoService.removeByIds(ids);

        // 同时删除用户表的数据
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("student_id", studentIds);
        usersService.remove(queryWrapper);

        return R.ok("删除成功");
    }

    @PostMapping("/importExcel")
    public void importExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            // 定义日期格式解析器
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");

            // 存储读取的数据和错误信息
            List<UserInfoExcelDto> userList = new ArrayList<>();
            List<Map<Integer, String>> errorDataList = new ArrayList<>();

            // 从 Excel 文件中提取所有学号
            Set<String> studentIdsFromExcel = getAllStudentIdsFromExcel(file.getInputStream());

            // 从数据库查询学号和主键 ID 映射
            Map<String, Integer> existingStudentIdMap = userInfoService.findExistingStudentIdMap(studentIdsFromExcel);

            // 创建 ExcelImportListener
            ExcelImportListener listener = new ExcelImportListener(userList, dateFormatter, errorDataList, existingStudentIdMap);


            // 使用 EasyExcel 读取 Excel 数据，使用自定义监听器
            EasyExcel.read(file.getInputStream(), UserInfoExcelDto.class, listener)
                    .sheet()
                    .doRead();

            // 如果没有数据，则返回提示
            if (userList.isEmpty()) {
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"导入数据为空\"}");
                return;
            }

            // 检查是否存在错误
            boolean hasErrors = userList.stream()
                    .anyMatch(dto -> dto.getErrorMessages() != null && !dto.getErrorMessages().isEmpty());

            if (hasErrors) {
                // 如果有错误，生成错误文件并返回
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=error_data.xlsx");

                // 调用 EasyExcel 写入错误数据
                EasyExcel.write(response.getOutputStream(), UserInfoExcelDto.class)
                        .registerWriteHandler(new ErrorCellStyleHandler(errorDataList))
                        .sheet("错误数据")
                        .doWrite(userList);
                return;
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

            // 返回成功信息
            response.setContentType("application/json");
            // 避免乱码
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"message\":\"导入成功\"}");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                // 避免乱码
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导入失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) {
        try {
            // 查询数据库中的用户数据
            List<UserInfo> userInfoList = userInfoService.list();

            if (userInfoList.isEmpty()) {
                throw new RuntimeException("无数据可导出");
            }

            // 将实体对象转为 Excel DTO 对象
            List<UserInfoExcelDto> userInfoExcelDtoList = userInfoList.stream()
                    .map(this::mapToExcelDto)
                    .collect(Collectors.toList());

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("用户信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 写入数据到响应流
            EasyExcel.write(response.getOutputStream(), UserInfoExcelDto.class)
                    .sheet("用户信息")
                    .doWrite(userInfoExcelDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 在导出失败时返回错误提示
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"导出失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/downloadStandardTemplate")
    public void downloadStandardTemplate(HttpServletResponse response) {
        // 定义标准文件的路径
        String standardFilePath = uploadDir + "user_info_standard.xlsx";

        // 创建文件对象
        File file = new File(standardFilePath);

        if (!file.exists()) {
            // 如果文件不存在，返回错误提示
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"模板文件不存在\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 如果文件存在，设置响应头并将文件流写入响应
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生个人信息模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            // 写入文件流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"message\":\"文件下载失败: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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
     * 将 UserInfo 转换为 UserInfoExcelDto
     */
    private UserInfoExcelDto mapToExcelDto(UserInfo userInfo) {
        UserInfoExcelDto dto = new UserInfoExcelDto();
        dto.setId(userInfo.getId());
        dto.setName(userInfo.getName());
        dto.setPhone(userInfo.getPhone());
        dto.setGender(userInfo.getGender());
        dto.setStudentId(userInfo.getStudentId());
        dto.setIdCard(userInfo.getIdCard());
        dto.setGrade(userInfo.getGrade());
        dto.setMajor(userInfo.getMajor());
        dto.setClassName(userInfo.getClassName());
        dto.setClassRole(userInfo.getClassRole());
        dto.setNativePlace(userInfo.getNativePlace());
        dto.setSourcePlace(userInfo.getSourcePlace());
        dto.setEthnicity(userInfo.getEthnicity());
        dto.setResidence(userInfo.getResidence());
        dto.setHomeAddress(userInfo.getHomeAddress());
        dto.setCounselor(userInfo.getCounselor());
        dto.setCounselorPhone(userInfo.getCounselorPhone());
        dto.setClassTeacher(userInfo.getClassTeacher());
        dto.setClassTeacherPhone(userInfo.getClassTeacherPhone());
        dto.setGraduationTutor(userInfo.getGraduationTutor());
        dto.setGraduationTutorPhone(userInfo.getGraduationTutorPhone());
        dto.setDormitoryNumber(userInfo.getDormitoryNumber());
        dto.setNetworkStatus(userInfo.getNetworkStatus());
        dto.setDormitoryMembers(userInfo.getDormitoryMembers());
        dto.setPoliticalStatus(userInfo.getPoliticalStatus());
        dto.setPartyProgress(userInfo.getPartyProgress());
        dto.setPartyTrainingProgress(userInfo.getPartyTrainingProgress());
        dto.setBranchName(userInfo.getBranchName());
        dto.setSpecialization(userInfo.getSpecialization());
        dto.setBirthDate(formatDate(userInfo.getBirthDate()));
        dto.setAdmissionDate(formatDate(userInfo.getAdmissionDate()));
        dto.setExpectedGraduation(formatDate(userInfo.getExpectedGraduation()));
        dto.setApplicationDate(formatDate(userInfo.getApplicationDate()));
        dto.setActivistDate(formatDate(userInfo.getActivistDate()));
        dto.setDevelopmentDate(formatDate(userInfo.getDevelopmentDate()));
        dto.setProbationaryDate(formatDate(userInfo.getProbationaryDate()));
        dto.setFullMemberDate(formatDate(userInfo.getFullMemberDate()));
        dto.setPartyHours(userInfo.getPartyHours());
        dto.setBranchSecretary(userInfo.getBranchSecretary());
        dto.setBranchDeputySecretary(userInfo.getBranchDeputySecretary());
        return dto;
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

    public Set<String> getAllStudentIdsFromExcel(InputStream inputStream) {
        Set<String> studentIds = new HashSet<>();

        // 自定义监听器，用于只提取学号列
        AnalysisEventListener<UserInfoExcelDto> listener = new AnalysisEventListener<UserInfoExcelDto>() {
            @Override
            public void invoke(UserInfoExcelDto dto, AnalysisContext context) {
                if (dto.getStudentId() != null && !dto.getStudentId().isEmpty()) {
                    studentIds.add(dto.getStudentId()); // 收集学号
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 完成解析后的逻辑
            }
        };

        // 使用 EasyExcel 读取学号列
        EasyExcel.read(inputStream, UserInfoExcelDto.class, listener)
                .headRowNumber(1) // 设置表头行数
                .sheet()
                .doRead();

        return studentIds; // 返回所有收集到的学号
    }


}
