package com.smartoa.entity;

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
 * 角色实体�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(exclude = {"userRoles", "rolePermissions"})
@ToString(exclude = {"userRoles", "rolePermissions"})
public class Role {

    /**
     * 角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色代码（如：ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE�?
     */
    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    /**
     * 角色描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 权限列表（JSON格式存储�?
     */
    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions;

    /**
     * 状态：1-启用�?-禁用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 用户角色关联（一个角色可以分配给多个用户�?
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRole> userRoles;

    /**
     * 角色权限关联（一个角色可以有多个权限�?
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RolePermission> rolePermissions;

    /**
     * 角色枚举
     */
    public enum RoleType {
        ROLE_ADMIN("ROLE_ADMIN", "系统管理�?),
        ROLE_MANAGER("ROLE_MANAGER", "部门主管"),
        ROLE_EMPLOYEE("ROLE_EMPLOYEE", "普通员�?);

        private final String code;
        private final String name;

        RoleType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        /**
         * 根据代码获取角色类型
         */
        public static RoleType fromCode(String code) {
            for (RoleType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的角色代�? " + code);
        }
    }

    /**
     * 角色状态枚�?
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
     * 检查角色是否启�?
     */
    public boolean isEnabled() {
        return Status.ENABLED.getValue().equals(this.status);
    }

    /**
     * 检查是否为系统管理员角�?
     */
    public boolean isAdmin() {
        return RoleType.ROLE_ADMIN.getCode().equals(this.roleCode);
    }

    /**
     * 检查是否为部门主管角色
     */
    public boolean isManager() {
        return RoleType.ROLE_MANAGER.getCode().equals(this.roleCode);
    }

    /**
     * 检查是否为普通员工角�?
     */
    public boolean isEmployee() {
        return RoleType.ROLE_EMPLOYEE.getCode().equals(this.roleCode);
    }

    /**
     * 获取权限列表（解析JSON字符串）
     */
    public List<String> getPermissionList() {
        if (permissions == null || permissions.trim().isEmpty()) {
            return List.of();
        }
        try {
            // 简单的JSON解析，实际项目中建议使用Jackson或Gson
            String cleanPermissions = permissions.replaceAll("[\\[\\]\"]", "");
            return List.of(cleanPermissions.split(",\\s*"));
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 设置权限列表（转换为JSON字符串）
     */
    public void setPermissionList(List<String> permissionList) {
        if (permissionList == null || permissionList.isEmpty()) {
            this.permissions = "[]";
        } else {
            this.permissions = "[\"" + String.join("\", \"", permissionList) + "\"]";
        }
    }
}
