package com.tec.campuscareerbackend.mapper;

import com.tec.campuscareerbackend.entity.ActivityTargetAudience;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author TECNB
 * @since 2024-11-10
 */
public interface ActivityTargetAudienceMapper extends BaseMapper<ActivityTargetAudience> {
    /**
     * 查询 audienceValue 和 major 的对应关系
     * @return 包含 audienceValue 和 major 的列表
     */
    @Select("SELECT audience_value AS audienceValue, major FROM activity_target_audience")
    List<Map<String, String>> getAudienceMajorMapping();
}
