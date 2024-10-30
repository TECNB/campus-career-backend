package com.tec.campuscareerbackend.common;

import lombok.Data;

@Data
public class R<T> {
    private int code; // 状态码
    private String message; // 消息
    private T data; // 返回数据

    public R() {}

    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "Success", data);
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> error(String message) {
        return new R<>(500, message, null);
    }
}
