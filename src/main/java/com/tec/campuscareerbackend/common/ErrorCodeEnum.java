package com.tec.campuscareerbackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    /**
     * 通用错误码
     */
    // token无效
    INVALID_TOKEN(201, "当前token无效"),
    // 用户已存在
    USER_ALREADY_EXISTS(202, "用户已存在"),
    // 密码错误
    PASSWORD_ERROR(203, "密码错误"),
    // 用户不存在
    USER_NOT_FOUND(204, "用户不存在"),
    // 手机号格式错误
    PHONE_FORMAT_ERROR(205, "手机号格式错误"),
    // 找不到匹配
    MATCH_NOT_FOUND(301, "找不到匹配"),
    // 用户已经添加过该兴趣爱好
    INTEREST_ALREADY_EXISTS(401, "用户已经添加过该兴趣爱好"),
    // 用户不存在该兴趣爱好
    INTEREST_NOT_FOUND(402, "用户不存在该兴趣爱好"),
    // 用户暂时没有星耀值评分历史
    STAR_RATINGS_NOT_FOUND(501, "用户暂时没有星耀值评分历史"),
    // 您已经给这个用户打了分
    ALREADY_RATED_THIS_USER(502, "您已经给这个用户打了分"),

    DIARY_NOT_BELONG_TO_USER(601, "日记不属于该用户"),

    DIARY_NOT_FOUND(602, "找不到日记"),;








    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 中文错误描述
     */
    private final String message;
}
