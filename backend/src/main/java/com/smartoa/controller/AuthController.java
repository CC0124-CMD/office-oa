package com.smartoa.controller;

import com.smartoa.common.Result;
import com.smartoa.entity.User;
import com.smartoa.repository.UserRepository;
import com.smartoa.repository.RoleRepository;
import com.smartoa.service.RoleService;
import com.smartoa.security.CustomUserDetailsService;
import com.smartoa.security.UserPrincipal;
import com.smartoa.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制�?
 * 处理用户登录、登出等认证相关操作
 * 
 * @author SmartOA
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    
    public AuthController(AuthenticationManager authenticationManager,
                         JwtUtil jwtUtil,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         CustomUserDetailsService userDetailsService,
                         RoleRepository roleRepository,
                         RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        log.info("AuthController 初始化完成，登录失败限制功能已禁用（无Redis�?);
    }

    // 登录失败次数限制配置（已禁用�?
    // private static final int MAX_LOGIN_ATTEMPTS = 5; // 最大登录失败次�?
    // private static final int LOCK_DURATION_MINUTES = 30; // 锁定时间（分钟）
    // private static final String LOGIN_ATTEMPTS_PREFIX = "login_attempts:";
    // private static final String ACCOUNT_LOCKED_PREFIX = "account_locked:";

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // 首先检查用户是否存�?
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                // 用户不存在，返回错误（登录失败限制已禁用�?
                log.warn("用户 {} 不存�?, username);
                return ResponseEntity.badRequest().body(Result.error("用户名或密码错误"));
            }

            User user = userOpt.get();
            
            // 检查用户状态是否正�?
            if (user.getStatus() != 1) {
                return ResponseEntity.badRequest().body(Result.error("账户已被禁用，请联系管理�?));
            }

            // 检查账户是否被锁定
            if (isAccountLocked(username)) {
                long remainingTime = getRemainingLockTime(username);
                return ResponseEntity.badRequest().body(Result.error(
                    "账户已被锁定，请等待 " + remainingTime + " 分钟后重�?));
            }

            // 登录失败限制已禁用，跳过检�?

            // 尝试认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 登录成功，清除失败次�?
            clearFailedLoginAttempts(username);

            // 获取用户信息
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtUtil.generateToken(authentication);

            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", buildUserInfo(userPrincipal));

            log.info("用户 {} 登录成功", username);
            return ResponseEntity.ok(Result.success(response));

        } catch (BadCredentialsException e) {
            // 密码错误（登录失败限制已禁用�?
            String username = loginRequest.getUsername();
            log.warn("用户 {} 登录失败：密码错�?, username);
            return ResponseEntity.badRequest().body(Result.error("用户名或密码错误"));

        } catch (LockedException e) {
            // 账户被锁�?
            return ResponseEntity.badRequest().body(Result.error("账户已被锁定，请联系管理�?));

        } catch (AuthenticationException e) {
            // 其他认证异常
            log.error("登录认证异常: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error("登录失败: " + e.getMessage()));

        } catch (Exception e) {
            // 其他异常
            log.error("登录过程中发生异�?, e);
            return ResponseEntity.badRequest().body(Result.error("登录失败，请稍后重试"));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Result<String>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存�?
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Result.error("用户名已存在"));
            }
            // 创建新用�?
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRealName(registerRequest.getRealName());
            user.setEmail(registerRequest.getEmail());
            user.setPhone(registerRequest.getPhone());
            user.setStatus(1); // 启用
            user.setCreateTime(java.time.LocalDateTime.now());
            user.setUpdateTime(java.time.LocalDateTime.now());
            User savedUser = userRepository.save(user);
            // 分配默认角色（如普通员工）
            com.smartoa.entity.Role defaultRole = null;
            if (registerRequest.getRoleCode() != null) {
                defaultRole = roleRepository.findByRoleCode(registerRequest.getRoleCode()).orElse(null);
            } else {
                defaultRole = roleRepository.findByRoleCode("ROLE_EMPLOYEE").orElse(null);
            }
            if (defaultRole != null) {
                roleService.assignRoleToUser(savedUser.getId(), defaultRole.getId(), savedUser.getId());
            }
            return ResponseEntity.ok(Result.success("注册成功"));
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ResponseEntity.badRequest().body(Result.error("注册失败: " + e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                // 可以将token加入黑名单（这里简化处理）
                log.info("用户登出成功");
            }
            return ResponseEntity.ok(Result.success("登出成功"));
        } catch (Exception e) {
            log.error("登出过程中发生异�?, e);
            return ResponseEntity.badRequest().body(Result.error("登出失败"));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Result<Map<String, Object>>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(jwt);
                
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // 获取用户权限信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    Map<String, Object> userInfo = buildUserInfo((UserPrincipal) userDetails);
                    return ResponseEntity.ok(Result.success(userInfo));
                }
            }
            return ResponseEntity.badRequest().body(Result.error("获取用户信息失败"));
        } catch (Exception e) {
            log.error("获取用户信息时发生异�?, e);
            return ResponseEntity.badRequest().body(Result.error("获取用户信息失败"));
        }
    }

    /**
     * 检查账户是否被锁定
     * Redis已禁用，始终返回false
     */
    private boolean isAccountLocked(String username) {
        return false; // 登录失败限制功能已禁�?
    }

    /**
     * 获取剩余锁定时间（分钟）
     * Redis已禁用，始终返回0
     */
    private long getRemainingLockTime(String username) {
        return 0; // 登录失败限制功能已禁�?
    }

    /**
     * 锁定账户
     * Redis已禁用，不执行任何操�?
     */
    private void lockAccount(String username) {
        log.debug("登录失败限制功能已禁用，跳过账户锁定: {}", username);
    }

    /**
     * 获取登录失败次数
     * Redis已禁用，始终返回0
     */
    private int getFailedLoginAttempts(String username) {
        return 0; // 登录失败限制功能已禁�?
    }

    /**
     * 增加登录失败次数
     * Redis已禁用，不执行任何操�?
     */
    private void incrementFailedLoginAttempts(String username) {
        log.debug("登录失败限制功能已禁用，跳过失败次数记录: {}", username);
    }

    /**
     * 清除登录失败次数
     * Redis已禁用，不执行任何操�?
     */
    private void clearFailedLoginAttempts(String username) {
        log.debug("登录失败限制功能已禁用，跳过清除操作: {}", username);
    }

    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(UserPrincipal userPrincipal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getId());
        userInfo.put("username", userPrincipal.getUsername());
        userInfo.put("realName", userPrincipal.getRealName());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("phone", userPrincipal.getPhone());
        userInfo.put("deptId", userPrincipal.getDeptId());
        userInfo.put("deptName", userPrincipal.getDeptName());
        userInfo.put("status", userPrincipal.getStatus());
        userInfo.put("roles", userPrincipal.getAuthorities());
        return userInfo;
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 注册请求DTO
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String realName;
        private String email;
        private String phone;
        private String roleCode; // 可选，默认普通员�?
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
        public String getRoleCode() { return roleCode; }
        public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    }
} 
