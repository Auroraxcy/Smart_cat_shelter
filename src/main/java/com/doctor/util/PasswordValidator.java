package com.doctor.util;

import com.doctor.enums.UserRole;
import java.util.regex.Pattern;

public class PasswordValidator {
    
    // 至少8位长度
    private static final int MIN_LENGTH = 8;
    
    // 正则表达式模式
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    
    /**
     * 验证密码强度（根据角色）
     * @param password 待验证的密码
     * @param role 用户角色
     * @return 验证结果消息，如果密码有效则返回null
     */
    public static String validatePassword(String password, UserRole role) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        
        if (password.length() < MIN_LENGTH) {
            return "密码长度至少为8位";
        }
        
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).find();
        
        // 管理员和饲养员：只需要字母+数字
        if (role == UserRole.ADMIN || role == UserRole.KEEPER) {
            if (!hasLowercase && !hasUppercase) {
                return "密码必须包含字母（大小写均可）";
            }
            
            if (!hasDigit) {
                return "密码必须包含数字";
            }
        } else {
            // 普通用户：需要字母+数字+特殊符号（高强度）
            if (!hasLowercase && !hasUppercase) {
                return "密码必须包含字母（大小写均可）";
            }
            
            if (!hasDigit) {
                return "密码必须包含数字";
            }
            
            if (!hasSpecialChar) {
                return "密码必须包含特殊符号";
            }
        }
        
        return null; // 密码符合要求
    }
    
    /**
     * 验证密码强度（默认普通用户）
     * @param password 待验证的密码
     * @return 验证结果消息，如果密码有效则返回null
     */
    public static String validatePassword(String password) {
        return validatePassword(password, UserRole.USER);
    }
    
    /**
     * 检查密码是否符合强度要求
     * @param password 待检查的密码
     * @return 是否符合要求
     */
    public static boolean isPasswordStrong(String password) {
        return validatePassword(password) == null;
    }
}