package com.smartoa.util;

import com.smartoa.entity.Role;
import com.smartoa.entity.User;
import com.smartoa.repository.RoleRepository;
import com.smartoa.repository.UserRepository;
import com.smartoa.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * 用户管理工具
 * 仅在生产环境手动创建用户，避免自动创建测试用�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Component
@Profile("prod") // 只在生产环境运行
@RequiredArgsConstructor
@Slf4j
public class UserManagementUtil implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否需要创建初始管理员用户
        if (userRepository.count() == 0) {
            log.warn("数据库中没有用户，需要创建初始管理员用户");
            log.info("请使用以下命令创建管理员用户�?);
            log.info("curl -X POST http://localhost:8080/api/auth/register-admin");
            log.info("或者手动在数据库中插入管理员用�?);
        } else {
            log.info("数据库中发现 {} 个用�?, userRepository.count());
        }
    }

    /**
     * 创建管理员用户（通过API调用�?
     */
    public void createAdminUser(String username, String password, String realName, String email) {
        try {
            // 检查用户是否已存在
            if (userRepository.findByUsername(username).isPresent()) {
                log.error("用户 {} 已存�?, username);
                return;
            }

            // 创建用户
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRealName(realName);
            admin.setEmail(email);
            admin.setPhone("13800138000");
            admin.setStatus(User.Status.ENABLED.getValue());
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());

            User savedAdmin = userRepository.save(admin);

            // 为管理员分配系统管理员角�?
            Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN").orElse(null);
            if (adminRole != null) {
                roleService.assignRoleToUser(savedAdmin.getId(), adminRole.getId(), savedAdmin.getId());
                log.info("成功创建管理员用�? {} / {}", username, password);
            } else {
                log.error("未找到管理员角色，请先初始化角色数据");
            }

        } catch (Exception e) {
            log.error("创建管理员用户失�?, e);
        }
    }

    /**
     * 创建普通用�?
     */
    public void createUser(String username, String password, String realName, String email, String roleCode) {
        try {
            // 检查用户是否已存在
            if (userRepository.findByUsername(username).isPresent()) {
                log.error("用户 {} 已存�?, username);
                return;
            }

            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRealName(realName);
            user.setEmail(email);
            user.setPhone("13800138000");
            user.setStatus(User.Status.ENABLED.getValue());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            // 分配角色
            Role role = roleRepository.findByRoleCode(roleCode).orElse(null);
            if (role != null) {
                roleService.assignRoleToUser(savedUser.getId(), role.getId(), 1L);
                log.info("成功创建用户: {} / {} (角色: {})", username, password, roleCode);
            } else {
                log.error("未找到角�? {}", roleCode);
            }

        } catch (Exception e) {
            log.error("创建用户失败", e);
        }
    }

    /**
     * 重置用户密码
     */
    public void resetUserPassword(String username, String newPassword) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.error("用户 {} 不存�?, username);
                return;
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);

            log.info("成功重置用户 {} 的密�?, username);

        } catch (Exception e) {
            log.error("重置用户密码失败", e);
        }
    }

    /**
     * 禁用用户
     */
    public void disableUser(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.error("用户 {} 不存�?, username);
                return;
            }

            user.setStatus(User.Status.DISABLED.getValue());
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);

            log.info("成功禁用用户: {}", username);

        } catch (Exception e) {
            log.error("禁用用户失败", e);
        }
    }

    /**
     * 启用用户
     */
    public void enableUser(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.error("用户 {} 不存�?, username);
                return;
            }

            user.setStatus(User.Status.ENABLED.getValue());
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);

            log.info("成功启用用户: {}", username);

        } catch (Exception e) {
            log.error("启用用户失败", e);
        }
    }
} 
