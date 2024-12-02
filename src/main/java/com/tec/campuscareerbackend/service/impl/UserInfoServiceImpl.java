package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.UserInfo;
import com.tec.campuscareerbackend.mapper.UserInfoMapper;
import com.tec.campuscareerbackend.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    @Override
    public Map<String, Integer> findExistingStudentIdMap(Set<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 使用 MyBatis Plus 查询数据库中匹配的学号和主键
        return this.lambdaQuery()
                .in(UserInfo::getStudentId, studentIds)
                .select(UserInfo::getStudentId, UserInfo::getId)
                .list()
                .stream()
                .collect(Collectors.toMap(UserInfo::getStudentId, UserInfo::getId));
    }
}
