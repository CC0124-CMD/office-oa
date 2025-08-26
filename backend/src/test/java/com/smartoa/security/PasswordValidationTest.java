package com.smartoa.security;

import com.smartoa.entity.User;
import com.smartoa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码验证测试�?
 * 验证BCrypt密码加密和验证功�?
 * 
 * @author SmartOA
 * @since 2025-08-07
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PasswordValidationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("测试管理员用户密码验�?)
    public void testAdminPasswordValidation() {
        // 查找管理员用�?
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        assertTrue(adminOpt.isPresent(), "管理员用户应该存�?);
        
        User admin = adminOpt.get();
        
        // 验证正确密码
        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()), 
            "正确的管理员密码应该验证通过");
        
        // 验证错误密码
        assertFalse(passwordEncoder.matches("wrongpassword", admin.getPassword()), 
            "错误的密码应该验证失�?);
        assertFalse(passwordEncoder.matches("admin", admin.getPassword()), 
            "不完整的密码应该验证失败");
        assertFalse(passwordEncoder.matches("Admin123", admin.getPassword()), 
            "大小写不匹配的密码应该验证失�?);
    }

    @Test
    @DisplayName("测试部门主管用户密码验证")
    public void testManagerPasswordValidation() {
        // 查找部门主管用户
        Optional<User> managerOpt = userRepository.findByUsername("manager");
        assertTrue(managerOpt.isPresent(), "部门主管用户应该存在");
        
        User manager = managerOpt.get();
        
        // 验证正确密码
        assertTrue(passwordEncoder.matches("manager123", manager.getPassword()), 
            "正确的部门主管密码应该验证通过");
        
        // 验证错误密码
        assertFalse(passwordEncoder.matches("wrongpassword", manager.getPassword()), 
            "错误的密码应该验证失�?);
        assertFalse(passwordEncoder.matches("manager", manager.getPassword()), 
            "不完整的密码应该验证失败");
    }

    @Test
    @DisplayName("测试普通员工用户密码验�?)
    public void testEmployeePasswordValidation() {
        // 测试所有员工用�?
        String[] employeeUsernames = {"employee1", "employee2", "employee3", "employee4", "employee5"};
        
        for (String username : employeeUsernames) {
            Optional<User> employeeOpt = userRepository.findByUsername(username);
            assertTrue(employeeOpt.isPresent(), "员工用户 " + username + " 应该存在");
            
            User employee = employeeOpt.get();
            
            // 验证正确密码
            assertTrue(passwordEncoder.matches("123456", employee.getPassword()), 
                "员工 " + username + " 的正确密码应该验证通过");
            
            // 验证错误密码
            assertFalse(passwordEncoder.matches("wrongpassword", employee.getPassword()), 
                "员工 " + username + " 的错误密码应该验证失�?);
            assertFalse(passwordEncoder.matches("12345", employee.getPassword()), 
                "员工 " + username + " 的不完整密码应该验证失败");
            assertFalse(passwordEncoder.matches("1234567", employee.getPassword()), 
                "员工 " + username + " 的错误长度密码应该验证失�?);
        }
    }

    @Test
    @DisplayName("测试BCrypt密码加密的随机�?)
    public void testBCryptRandomness() {
        String plainPassword = "testpassword123";
        
        // 多次加密同一个密码，应该产生不同的哈希�?
        String hash1 = passwordEncoder.encode(plainPassword);
        String hash2 = passwordEncoder.encode(plainPassword);
        String hash3 = passwordEncoder.encode(plainPassword);
        
        // 哈希值应该不同（BCrypt每次都会生成不同的salt�?
        assertNotEquals(hash1, hash2, "BCrypt应该为相同密码生成不同的哈希�?);
        assertNotEquals(hash2, hash3, "BCrypt应该为相同密码生成不同的哈希�?);
        assertNotEquals(hash1, hash3, "BCrypt应该为相同密码生成不同的哈希�?);
        
        // 但是都应该能验证原始密码
        assertTrue(passwordEncoder.matches(plainPassword, hash1), "第一个哈希值应该验证通过");
        assertTrue(passwordEncoder.matches(plainPassword, hash2), "第二个哈希值应该验证通过");
        assertTrue(passwordEncoder.matches(plainPassword, hash3), "第三个哈希值应该验证通过");
    }

    @Test
    @DisplayName("测试密码强度要求")
    public void testPasswordStrengthValidation() {
        // 测试各种密码强度
        String[] weakPasswords = {"", "123", "abc", "password"};
        String[] strongPasswords = {"admin123", "manager123", "123456"};
        
        for (String weakPassword : weakPasswords) {
            String encoded = passwordEncoder.encode(weakPassword);
            assertTrue(passwordEncoder.matches(weakPassword, encoded), 
                "即使是弱密码，BCrypt也应该能正确加密和验�? " + weakPassword);
        }
        
        for (String strongPassword : strongPasswords) {
            String encoded = passwordEncoder.encode(strongPassword);
            assertTrue(passwordEncoder.matches(strongPassword, encoded), 
                "强密码应该能正确加密和验�? " + strongPassword);
        }
    }

    @Test
    @DisplayName("测试用户状态和密码验证")
    public void testUserStatusAndPasswordValidation() {
        // 查找管理员用�?
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        assertTrue(adminOpt.isPresent(), "管理员用户应该存�?);
        
        User admin = adminOpt.get();
        
        // 验证用户状�?
        assertEquals(1, admin.getStatus(), "管理员用户应该是启用状�?);
        assertTrue(admin.isEnabled(), "管理员用户应该是启用状�?);
        
        // 验证用户信息
        assertEquals("admin", admin.getUsername(), "用户名应该正�?);
        assertEquals("系统管理�?, admin.getRealName(), "真实姓名应该正确");
        assertEquals("admin@smartoa.com", admin.getEmail(), "邮箱应该正确");
        assertEquals("13800138001", admin.getPhone(), "手机号应该正�?);
        
        // 验证密码
        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()), 
            "管理员密码应该验证通过");
    }

    @Test
    @DisplayName("测试空密码和null密码处理")
    public void testEmptyAndNullPasswordHandling() {
        String testPassword = "testpassword";
        String encodedPassword = passwordEncoder.encode(testPassword);
        
        // 测试空字符串密码
        assertFalse(passwordEncoder.matches("", encodedPassword), 
            "空字符串密码应该验证失败");
        
        // 测试null密码（这会抛出异常，这是预期的行为）
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.matches(null, encodedPassword);
        }, "null密码应该抛出异常");
    }
}
