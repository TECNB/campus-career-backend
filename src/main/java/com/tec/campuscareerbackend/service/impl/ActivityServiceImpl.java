package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.Activity;
import com.tec.campuscareerbackend.mapper.ActivityMapper;
import com.tec.campuscareerbackend.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动信息表 服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-10-31
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

}
