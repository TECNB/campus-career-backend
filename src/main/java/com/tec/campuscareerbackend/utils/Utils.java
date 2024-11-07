package com.tec.campuscareerbackend.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.tec.campuscareerbackend.common.CustomException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;

import static com.tec.campuscareerbackend.common.ErrorCodeEnum.INVALID_TOKEN;
public class Utils {
    // 使用SHA-512算法加密密码
    public static String encryptHv(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest hash = MessageDigest.getInstance("SHA-512");
        hash.update(salt.getBytes());
        hash.update(password.getBytes());
        byte[] value = hash.digest();

        // 重复512次，使散列更安全
        for (int i = 0; i < 512; i++) {
            MessageDigest hashInner = MessageDigest.getInstance("SHA-512");
            hashInner.update(value);
            value = hashInner.digest();
        }

        return Base64.getEncoder().encodeToString(value);
    }

    // 生成盐
    public static String generateSalt() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder objectId = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 48; i++) {
            int randomIndex = random.nextInt(characters.length());
            objectId.append(characters.charAt(randomIndex));
        }

        return objectId.toString();
    }

    public static String getCurrentTime() {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'");
            return currentDateTime.format(formatter);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

    // 校验中国手机号
    public static boolean isPhone(String phone) {
        String regex = "^[1][3,4,5,7,8,9][0-9]{9}$";
        return phone.matches(regex);
    }

    // 通过Token获取用户ID
    public static String getUserIdByToken(String token) {
        String userId = (String) StpUtil.getLoginIdByToken(token);
        if (userId == null) {
            throw new CustomException(INVALID_TOKEN);
        }
        return userId;
    }

    public static String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().substring(0, 12); // 取前12位，生成较短唯一文件名
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
