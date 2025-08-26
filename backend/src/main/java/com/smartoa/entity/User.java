package com.smartoa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户实体�?
 * 
 * @author SmartOA
 * @version 2.0.0
 * @since 2025-08-06
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(exclude = {"userRoles"})
@ToString(exclude = {"userRoles"})
public class User {
    
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户�?
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;
    
    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;
    
    /**
     * 手机�?
     */
    @Column(name = "phone", length = 20)
    private String phone;
    
    /**
     * 头像
     */
    @Column(name = "avatar")
    private String avatar;
    
    /**
     * 状�?0:禁用 1:启用)
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;
    
    /**
     * 部门ID
     */
    @Column(name = "dept_id")
    private Long deptId;
    
    /**
     * 部门名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 用户角色关联（一个用户可以有多个角色�?
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRole> userRoles;
    
    /**
     * 角色列表（通过关联表获取）
     */
    @Transient
    private List<Role> roles;
    
    /**
     * 权限列表（通过角色获取�?
     */
    @Transient
    private List<String> permissions;

    /**
     * 构造函�?
     */
    public User() {}

    public User(Long id, String username, String realName) {
        this.id = id;
        this.username = username;
        this.realName = realName;
    }

    /**
     * 用户状态枚�?
     */
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");

        private final Integer value;
        private final String description;

        Status(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 检查用户是否启�?
     */
    public boolean isEnabled() {
        return Status.ENABLED.getValue().equals(this.status);
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(String roleCode) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return permissions.contains(permission);
    }

    /**
     * 检查是否为系统管理�?
     */
    public boolean isAdmin() {
        return hasRole(Role.RoleType.ROLE_ADMIN.getCode());
    }

    /**
     * 检查是否为部门主管
     */
    public boolean isManager() {
        return hasRole(Role.RoleType.ROLE_MANAGER.getCode());
    }

    /**
     * 检查是否为普通员�?
     */
    public boolean isEmployee() {
        return hasRole(Role.RoleType.ROLE_EMPLOYEE.getCode());
    }
} 
