package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.mapper.UserInfoMapper;
import com.tec.campuscareerbackend.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-22
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
