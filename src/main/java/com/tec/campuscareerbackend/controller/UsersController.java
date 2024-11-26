package com.tec.campuscareerbackend.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.tec.campuscareerbackend.common.CustomException;
import com.tec.campuscareerbackend.common.R;
import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.service.IUsersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.tec.campuscareerbackend.common.ErrorCodeEnum.*;
import static com.tec.campuscareerbackend.utils.Utils.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author TECNB
 * @since 2024-10-30
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    @Resource
    private IUsersService usersService;

    // 实现获取users表中所有数据的接口
    @GetMapping("/all")
    public R<List<Users>> getAll() {
        List<Users> usersList = usersService.list();
        return R.ok(usersList);
    }

    // 注册功能
    @PostMapping("/register")
    public Users register(@RequestBody Users users) throws NoSuchAlgorithmException {
        // 如果存在该用户，返回错误信息
        if (usersService.getByPhone(users.getPhone()) != null) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }
        Users user = new Users();
        // 生成盐
        String salt = generateSalt();

        // 生成哈希值
        String hv = encryptHv(users.getPasswordHash(), salt);

        user.setUsername(users.getUsername());
        user.setPhone(users.getPhone());
        user.setPasswordHash(hv);
        user.setUserType(users.getUserType());
        user.setSalt(salt);

        usersService.save(user);
        // 返回完整用户信息
        return user;
    }

    // 登录功能
    @PostMapping("/login")
    public Users login(@RequestBody Users users) throws NoSuchAlgorithmException{
        Users user = usersService.getByStudentId(users.getStudentId());
        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        String salt = user.getSalt();
        String hv = user.getPasswordHash();
        if (hv.equals(encryptHv(users.getPasswordHash(), salt))) {
            user.setToken(StpUtil.createLoginSession(user.getStudentId()));
            user.setLastLogin(getCurrentTime());
            usersService.updateById(user);
            return user;
        } else {
            // 通过CustomException丢出报错
            throw new CustomException(PASSWORD_ERROR);
        }
    }
}
