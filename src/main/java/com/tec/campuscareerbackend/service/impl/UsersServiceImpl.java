package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.Users;
import com.tec.campuscareerbackend.mapper.UsersMapper;
import com.tec.campuscareerbackend.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-10-30
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Override
    public Users getByUsername(String username) {
        // 根据用户名查询用户
        return this.lambdaQuery().eq(Users::getUsername, username).one();
    }

    @Override
    public Users getByPhone(String phone) {
        // 根据手机号查询用户
        return this.lambdaQuery().eq(Users::getPhone, phone).one();
    }

    @Override
    public boolean checkUserExistByUserId(String userId) {
        // 根据用户id查询用户
        return this.lambdaQuery().eq(Users::getUserId, userId).count() > 0;
    }
}
