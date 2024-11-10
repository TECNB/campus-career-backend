package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-10-30
 */
public interface IUsersService extends IService<Users> {
    Users getByUsername(String username);

    Users getByPhone(String phone);

    // 通过userId检查是否存在该用户
    boolean checkUserExistByUserId(String userId);

    // 通过studentId获取用户
    Users getByStudentId(String studentId);
}
