package com.smartoa.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码生成工具�?
 * 用于生成BCrypt加密的测试密�?
 * 
 * @author SmartOA
 * @since 2025-08-07
 */
public class PasswordGeneratorUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 生成测试用户的BCrypt密码哈希�?
     */
    public static void main(String[] args) {
        System.out.println("=== 测试用户密码BCrypt哈希值生�?===");
        System.out.println();
        
        // 管理员密�?
        String adminPassword = "admin123";
        String adminHash = passwordEncoder.encode(adminPassword);
        System.out.println("管理员用�?(admin):");
        System.out.println("  明文密码: " + adminPassword);
        System.out.println("  BCrypt哈希: " + adminHash);
        System.out.println("  验证结果: " + passwordEncoder.matches(adminPassword, adminHash));
        System.out.println();
        
        // 部门主管密码
        String managerPassword = "manager123";
        String managerHash = passwordEncoder.encode(managerPassword);
        System.out.println("部门主管用户 (manager):");
        System.out.println("  明文密码: " + managerPassword);
        System.out.println("  BCrypt哈希: " + managerHash);
        System.out.println("  验证结果: " + passwordEncoder.matches(managerPassword, managerHash));
        System.out.println();
        
        // 普通员工密�?
        String employeePassword = "123456";
        String employeeHash = passwordEncoder.encode(employeePassword);
        System.out.println("普通员工用�?(employee1-5):");
        System.out.println("  明文密码: " + employeePassword);
        System.out.println("  BCrypt哈希: " + employeeHash);
        System.out.println("  验证结果: " + passwordEncoder.matches(employeePassword, employeeHash));
        System.out.println();
        
        // 生成多个不同的哈希值（演示BCrypt的随机性）
        System.out.println("=== BCrypt随机性演�?===");
        System.out.println("相同密码 '" + employeePassword + "' 的多个不同哈希�?");
        for (int i = 1; i <= 5; i++) {
            String hash = passwordEncoder.encode(employeePassword);
            System.out.println("  哈希" + i + ": " + hash);
            System.out.println("  验证: " + passwordEncoder.matches(employeePassword, hash));
        }
        System.out.println();
        
        // 生成SQL插入语句
        System.out.println("=== SQL插入语句 ===");
        generateSQLInsertStatements();
        
        // 测试错误密码
        System.out.println("=== 错误密码验证测试 ===");
        testWrongPasswords();
    }

    /**
     * 生成SQL插入语句
     */
    private static void generateSQLInsertStatements() {
        String adminHash = passwordEncoder.encode("admin123");
        String managerHash = passwordEncoder.encode("manager123");
        String employeeHash = passwordEncoder.encode("123456");
        
        System.out.println("-- 用户数据插入语句（使用新生成的BCrypt哈希值）");
        System.out.println("INSERT INTO users (username, password, real_name, email, phone, status, dept_id, dept_name, create_time, update_time) VALUES");
        System.out.println("('admin', '" + adminHash + "', '系统管理�?, 'admin@smartoa.com', '13800138001', 1, 1, '管理�?, NOW(), NOW()),");
        System.out.println("('manager', '" + managerHash + "', '部门主管', 'manager@smartoa.com', '13800138002', 1, 2, '业务�?, NOW(), NOW()),");
        System.out.println("('employee1', '" + employeeHash + "', '张三', 'zhangsan@smartoa.com', '13800138003', 1, 2, '业务�?, NOW(), NOW()),");
        System.out.println("('employee2', '" + employeeHash + "', '李四', 'lisi@smartoa.com', '13800138004', 1, 2, '业务�?, NOW(), NOW()),");
        System.out.println("('employee3', '" + employeeHash + "', '王五', 'wangwu@smartoa.com', '13800138005', 1, 3, '技术部', NOW(), NOW()),");
        System.out.println("('employee4', '" + employeeHash + "', '赵六', 'zhaoliu@smartoa.com', '13800138006', 1, 3, '技术部', NOW(), NOW()),");
        System.out.println("('employee5', '" + employeeHash + "', '钱七', 'qianqi@smartoa.com', '13800138007', 1, 4, '财务�?, NOW(), NOW());");
        System.out.println();
    }

    /**
     * 测试错误密码验证
     */
    private static void testWrongPasswords() {
        String correctPassword = "admin123";
        String correctHash = passwordEncoder.encode(correctPassword);
        
        String[] wrongPasswords = {
            "admin",           // 不完�?
            "Admin123",        // 大小写错�?
            "admin1234",       // 多一个字�?
            "admin12",         // 少一个字�?
            "wrongpassword",   // 完全错误
            "",                // 空字符串
            "123456"           // 其他用户的密�?
        };
        
        System.out.println("正确密码: " + correctPassword);
        System.out.println("正确密码验证: " + passwordEncoder.matches(correctPassword, correctHash));
        System.out.println();
        
        System.out.println("错误密码验证测试:");
        for (String wrongPassword : wrongPasswords) {
            boolean matches = passwordEncoder.matches(wrongPassword, correctHash);
            System.out.println("  密码 '" + wrongPassword + "' -> " + (matches ? "通过" : "失败") + 
                             (matches ? " �?不应该通过!" : " �?正确拒绝"));
        }
        System.out.println();
    }

    /**
     * 验证密码强度
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // 可以添加更多密码强度验证规则
        // 例如：包含数字、字母、特殊字符等
        
        return true;
    }

    /**
     * 生成指定密码的BCrypt哈希�?
     */
    public static String generateBCryptHash(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 验证密码是否匹配哈希�?
     */
    public static boolean verifyPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}
