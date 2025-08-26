package com.smartoa.controller;

import com.smartoa.entity.Role;
import com.smartoa.entity.User;
import com.smartoa.repository.RoleRepository;
import com.smartoa.repository.UserRepository;
import com.smartoa.service.RoleService;
import com.smartoa.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员控制器
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建初始管理员用户（仅在生产环境使用�?
     */
    @PostMapping("/create-admin")
    @Profile("prod")
    public ResponseEntity<Result<String>> createAdminUser(@RequestBody CreateAdminRequest request) {
        try {
            // 检查是否已有管理员用户
            if (userRepository.findByUsername("admin").isPresent()) {
                return ResponseEntity.badRequest().body(Result.error("管理员用户已存在"));
            }

            // 创建管理员用�?
            User admin = new User();
            admin.setUsername(request.getUsername());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRealName(request.getRealName());
            admin.setEmail(request.getEmail());
            admin.setPhone(request.getPhone());
            admin.setStatus(User.Status.ENABLED.getValue());
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());

            User savedAdmin = userRepository.save(admin);

            // 为管理员分配系统管理员角�?
            Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN").orElse(null);
            if (adminRole != null) {
                roleService.assignRoleToUser(savedAdmin.getId(), adminRole.getId(), savedAdmin.getId());
            }

            log.info("成功创建管理员用�? {}", request.getUsername());
            return ResponseEntity.ok(Result.success("管理员用户创建成�?));

        } catch (Exception e) {
            log.error("创建管理员用户失�?, e);
            return ResponseEntity.badRequest().body(Result.error("创建管理员用户失�? " + e.getMessage()));
        }
    }

    public static class CreateAdminRequest {
        private String username;
        private String password;
        private String realName;
        private String email;
        private String phone;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
} 
