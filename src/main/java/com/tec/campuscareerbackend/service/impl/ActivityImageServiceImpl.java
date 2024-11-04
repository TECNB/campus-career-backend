package com.tec.campuscareerbackend.service.impl;

import com.tec.campuscareerbackend.entity.ActivityImage;
import com.tec.campuscareerbackend.mapper.ActivityImageMapper;
import com.tec.campuscareerbackend.service.IActivityImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动图片表 服务实现类
 * </p>
 *
 * @author TECNB
 * @since 2024-11-03
 */
@Service
public class ActivityImageServiceImpl extends ServiceImpl<ActivityImageMapper, ActivityImage> implements IActivityImageService {
        @Override
        public List<String> getActivityImagesByActivityId(Integer activityId) {
            return this.lambdaQuery().eq(ActivityImage::getActivityId, activityId).list().stream().map(ActivityImage::getImagePath).collect(Collectors.toList());
        }

        @Override
        public ActivityImage addActivityImage(Integer activityId, String imagePath) {
            ActivityImage activityImage = new ActivityImage();
            activityImage.setActivityId(activityId);
            activityImage.setImagePath(imagePath);
            this.save(activityImage);
            return activityImage;
        }

        @Override
        public ActivityImage deleteAllActivityImage(Integer activityId) {
            this.lambdaQuery().eq(ActivityImage::getActivityId, activityId).list().forEach(this::removeById);
            return null;
        }
}
