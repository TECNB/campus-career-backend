package com.tec.campuscareerbackend.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.dto.UserDetailExcelDto;
import com.tec.campuscareerbackend.entity.UserDetail;
import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.service.IUserDetailService;
import com.tec.campuscareerbackend.service.IUsersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.tec.campuscareerbackend.utils.Utils.encryptHv;
import static com.tec.campuscareerbackend.utils.Utils.generateSalt;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-11-10
 */
@RestController
@RequestMapping("/user-detail")
public class UserDetailController {
    @Resource
    private IUserDetailService userDetailService;
    @Resource
    private IUsersService usersService;

    // 获取所有用户信息构建一个分页查询接口
    @GetMapping
    public R<List<UserDetail>> getAll(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Page<UserDetail> userDetailPage = new Page<>(page, size);
        Page<UserDetail> result = userDetailService.page(userDetailPage);
        return R.ok(result.getRecords());
    }

    // 通过ID查询用户信息
    @GetMapping("/{id}")
    public R<UserDetail> getUserById(@PathVariable Integer id) {
        UserDetail userDetail = userDetailService.getById(id);
        return R.ok(userDetail);
    }

    // 添加用户信息
    @PostMapping
    public R<UserDetail> addUserDetail(@RequestBody UserDetail userDetail) {
        userDetailService.save(userDetail);
        return R.ok(userDetail);
    }

    // 删除用户信息
    @DeleteMapping
    public R<String> deleteUserDetail(@RequestBody UserDetail userDetail) {
        userDetailService.removeById(userDetail.getId());
        return R.ok("删除成功");
    }

    // 修改用户信息
    @PutMapping
    public R<UserDetail> updateUserDetail(@RequestBody UserDetail userDetail) {
        userDetailService.updateById(userDetail);
        return R.ok(userDetail);
    }


    @PostMapping("/importExcel")
    public R<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 读取Excel数据并过滤只获取需要的字段
            List<UserDetailExcelDto> userList = EasyExcel.read(file.getInputStream())
                    .head(UserDetailExcelDto.class)
                    .sheet()
                    .doReadSync();

            for (UserDetailExcelDto dto : userList) {
                // 检查关键字段是否为空，判断是否为空白行
                if (dto.getName() == null || dto.getName().isEmpty()) {
                    // 遇到空白行，跳出循环并返回成功
                    return R.ok("导入成功");
                }
                // 保存到 user_detail 表
                UserDetail userDetail = new UserDetail();
                userDetail.setName(dto.getName());
                userDetail.setGender(dto.getGender());
                userDetail.setClassName(dto.getClassName());
                userDetail.setStudentId(dto.getStudentId());
                userDetail.setContactNumber(dto.getContactNumber());
                userDetail.setClassTeacher(dto.getClassTeacher());
                userDetail.setGraduationTutor(dto.getGraduationTutor());
                userDetailService.save(userDetail);

                // 初始化保存到 users 表
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
                user.setPhone(dto.getContactNumber());
                usersService.save(user);
            }
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败: " + e.getMessage());
        }
    }

}
