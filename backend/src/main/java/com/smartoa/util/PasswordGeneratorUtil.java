package com.smartoa.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 用于生成BCrypt密码哈希�?
 * 
 * @author SmartOA
 * @since 2025-08-12
 */
public class PasswordGeneratorUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        System.out.println("=== 密码哈希生成工具 ===");
        System.out.println();
        
        // 生成测试密码的哈希�?
        String[] passwords = {"admin123", "manager123", "123456"};
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("密码: " + password + " -> 哈希: " + hash);
        }
        
        System.out.println();
        System.out.println("=== 验证测试 ===");
        
        // 验证密码
        String testPassword = "admin123";
        String testHash = encoder.encode(testPassword);
        System.out.println("测试密码: " + testPassword);
        System.out.println("生成的哈�? " + testHash);
        System.out.println("验证结果: " + encoder.matches(testPassword, testHash));
        
        // 验证已知哈希
        String knownHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        System.out.println("已知哈希: " + knownHash);
        System.out.println("验证 admin123: " + encoder.matches("admin123", knownHash));
        System.out.println("验证 manager123: " + encoder.matches("manager123", knownHash));
        System.out.println("验证 123456: " + encoder.matches("123456", knownHash));
    }
} 
