package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-22
 */
public interface IUserInfoService extends IService<UserInfo> {
    /**
     * 查询数据库中是否存在给定的学号，并返回学号与主键映射
     *
     * @param studentIds 学号集合
     * @return 学号与主键映射
     */
    Map<String, Integer> findExistingStudentIdMap(Set<String> studentIds);

}
