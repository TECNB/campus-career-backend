package com.tec.campuscareerbackend.service;

import com.tec.campuscareerbackend.entity.ActivityImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 活动图片表 服务类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-03
 */
public interface IActivityImageService extends IService<ActivityImage> {
        List<String> getActivityImagesByActivityId(Integer activityId);

        ActivityImage addActivityImage(Integer activityId, String imagePath);

        ActivityImage deleteAllActivityImage(Integer activityId);

        ActivityImage updateActivityImage(Integer activityId, Integer imageId, String imagePath);

}
