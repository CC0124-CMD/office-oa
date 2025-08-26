package com.smartoa.config;

import com.smartoa.entity.Role;
import com.smartoa.entity.Permission;
import com.smartoa.entity.User;
import com.smartoa.repository.RoleRepository;
import com.smartoa.repository.PermissionRepository;
import com.smartoa.repository.UserRepository;
import com.smartoa.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化器
 * 仅在开发环境运行，生产环境不自动创建测试用�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Component
@Profile({"h2", "dev", "test"}) // 只在H2、开发、测试环境运�?
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("开始初始化系统数据...");
        
        // 初始化权�?
        initPermissions();
        
        // 初始化角�?
        initRoles();
        
        // 初始化管理员用户
        initAdminUser();
        
        // 初始化测试用�?
        initTestUsers();
        
        log.info("系统数据初始化完�?);
    }

    /**
     * 初始化权限数�?
     */
    private void initPermissions() {
        if (permissionRepository.count() > 0) {
            log.info("权限数据已存在，跳过初始�?);
            return;
        }

        log.info("初始化权限数�?..");

        List<Permission> permissions = Arrays.asList(
            // 系统管理权限
            createPermission("system:read", "系统查看", "system", "read", "查看系统信息", 1),
            createPermission("system:write", "系统配置", "system", "write", "修改系统配置", 2),
            createPermission("system:delete", "系统删除", "system", "delete", "删除系统数据", 3),

            // 用户管理权限
            createPermission("user:read", "用户查看", "user", "read", "查看用户信息", 10),
            createPermission("user:create", "用户创建", "user", "create", "创建新用�?, 11),
            createPermission("user:update", "用户更新", "user", "update", "更新用户信息", 12),
            createPermission("user:delete", "用户删除", "user", "delete", "删除用户", 13),

            // 角色管理权限
            createPermission("role:read", "角色查看", "role", "read", "查看角色信息", 20),
            createPermission("role:create", "角色创建", "role", "create", "创建新角�?, 21),
            createPermission("role:update", "角色更新", "role", "update", "更新角色信息", 22),
            createPermission("role:delete", "角色删除", "role", "delete", "删除角色", 23),

            // 部门管理权限
            createPermission("dept:read", "部门查看", "dept", "read", "查看部门信息", 30),
            createPermission("dept:create", "部门创建", "dept", "create", "创建新部�?, 31),
            createPermission("dept:update", "部门更新", "dept", "update", "更新部门信息", 32),
            createPermission("dept:delete", "部门删除", "dept", "delete", "删除部门", 33),

            // 审批流程权限
            createPermission("approval:read", "审批查看", "approval", "read", "查看审批流程", 40),
            createPermission("approval:create", "审批创建", "approval", "create", "创建审批流程", 41),
            createPermission("approval:update", "审批处理", "approval", "update", "处理审批流程", 42),
            createPermission("approval:delete", "审批删除", "approval", "delete", "删除审批流程", 43),

            // 工作流权�?
            createPermission("workflow:read", "工作流查�?, "workflow", "read", "查看工作�?, 50),
            createPermission("workflow:create", "工作流创�?, "workflow", "create", "创建工作�?, 51),
            createPermission("workflow:update", "工作流更�?, "workflow", "update", "更新工作�?, 52),

            // 考勤管理权限
            createPermission("attendance:read", "考勤查看", "attendance", "read", "查看考勤信息", 60),
            createPermission("attendance:create", "考勤打卡", "attendance", "create", "考勤打卡", 61),
            createPermission("attendance:update", "考勤更新", "attendance", "update", "更新考勤信息", 62),

            // 个人信息权限
            createPermission("profile:read", "个人信息查看", "profile", "read", "查看个人信息", 70),
            createPermission("profile:update", "个人信息更新", "profile", "update", "更新个人信息", 71),

            // 文档管理权限
            createPermission("document:read", "文档查看", "document", "read", "查看文档", 80),
            createPermission("document:create", "文档创建", "document", "create", "创建文档", 81),
            createPermission("document:update", "文档更新", "document", "update", "更新文档", 82),
            createPermission("document:delete", "文档删除", "document", "delete", "删除文档", 83)
        );

        permissionRepository.saveAll(permissions);
        log.info("权限数据初始化完成，�?{} �?, permissions.size());
    }

    /**
     * 初始化角色数�?
     */
    private void initRoles() {
        if (roleRepository.count() > 0) {
            log.info("角色数据已存在，跳过初始�?);
            return;
        }

        log.info("初始化角色数�?..");

        // 系统管理员角�?
        Role adminRole = createRole(
            "ROLE_ADMIN", 
            "系统管理�?, 
            "拥有系统最高权限，负责系统配置、用户管理、角色管理等",
            "[\"system:*\", \"user:*\", \"role:*\", \"permission:*\", \"audit:*\", \"config:*\"]"
        );

        // 部门主管角色
        Role managerRole = createRole(
            "ROLE_MANAGER", 
            "部门主管", 
            "管理本部门事务，负责审批流程、部门人员管理等",
            "[\"dept:*\", \"approval:*\", \"user:read\", \"user:update\", \"report:*\"]"
        );

        // 普通员工角�?
        Role employeeRole = createRole(
            "ROLE_EMPLOYEE", 
            "普通员�?, 
            "日常办公操作，可以发起流程、查看个人信息等",
            "[\"profile:*\", \"workflow:create\", \"workflow:read\", \"attendance:*\", \"document:read\"]"
        );

        roleRepository.saveAll(Arrays.asList(adminRole, managerRole, employeeRole));
        log.info("角色数据初始化完成，�?3 �?);
    }

    /**
     * 初始化管理员用户
     */
    private void initAdminUser() {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }

        log.info("初始化管理员用户...");

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRealName("系统管理�?);
        admin.setEmail("admin@smartoa.com");
        admin.setPhone("13800138000");
        admin.setStatus(User.Status.ENABLED.getValue());
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());

        User savedAdmin = userRepository.save(admin);

        // 为管理员分配系统管理员角�?
        Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN").orElse(null);
        if (adminRole != null) {
            roleService.assignRoleToUser(savedAdmin.getId(), adminRole.getId(), savedAdmin.getId());
            log.info("为管理员用户分配系统管理员角�?);
        }

        log.info("管理员用户初始化完成: admin/admin123");
    }

    /**
     * 初始化测试用�?
     */
    private void initTestUsers() {
        log.info("初始化测试用户数�?..");

        // 检查是否已有测试用�?
        if (userRepository.findByUsername("manager").isPresent()) {
            log.info("测试用户已存在，跳过初始�?);
            return;
        }

        // 获取角色
        Role managerRole = roleRepository.findByRoleCode("ROLE_MANAGER").orElse(null);
        Role employeeRole = roleRepository.findByRoleCode("ROLE_EMPLOYEE").orElse(null);

        // 创建部门主管用户
        User manager = createUser("manager", "manager123", "部门主管", 
                                "manager@smartoa.com", "13800138002", 2L, "业务�?);
        User savedManager = userRepository.save(manager);
        if (managerRole != null) {
            roleService.assignRoleToUser(savedManager.getId(), managerRole.getId(), 1L);
        }

        // 创建普通员工用�?
        User[] employees = {
            createUser("employee1", "123456", "张三", "zhangsan@smartoa.com", "13800138003", 2L, "业务�?),
            createUser("employee2", "123456", "李四", "lisi@smartoa.com", "13800138004", 2L, "业务�?),
            createUser("employee3", "123456", "王五", "wangwu@smartoa.com", "13800138005", 3L, "技术部"),
            createUser("employee4", "123456", "赵六", "zhaoliu@smartoa.com", "13800138006", 3L, "技术部"),
            createUser("employee5", "123456", "钱七", "qianqi@smartoa.com", "13800138007", 4L, "财务�?)
        };

        for (User employee : employees) {
            User savedEmployee = userRepository.save(employee);
            if (employeeRole != null) {
                roleService.assignRoleToUser(savedEmployee.getId(), employeeRole.getId(), 1L);
            }
        }

        log.info("测试用户初始化完�?");
        log.info("- 部门主管: manager/manager123");
        log.info("- 普通员�? employee1-5/123456");
    }

    /**
     * 创建用户对象
     */
    private User createUser(String username, String password, String realName, 
                          String email, String phone, Long deptId, String deptName) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDeptId(deptId);
        user.setDeptName(deptName);
        user.setStatus(User.Status.ENABLED.getValue());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    /**
     * 创建权限对象
     */
    private Permission createPermission(String code, String name, String resource, 
                                      String action, String description, int sortOrder) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setResource(resource);
        permission.setAction(action);
        permission.setDescription(description);
        permission.setSortOrder(sortOrder);
        permission.setStatus(Permission.Status.ENABLED.getValue());
        return permission;
    }

    /**
     * 创建角色对象
     */
    private Role createRole(String code, String name, String description, String permissions) {
        Role role = new Role();
        role.setRoleCode(code);
        role.setRoleName(name);
        role.setDescription(description);
        role.setPermissions(permissions);
        role.setStatus(Role.Status.ENABLED.getValue());
        role.setCreatedBy(1L);
        return role;
    }
}
