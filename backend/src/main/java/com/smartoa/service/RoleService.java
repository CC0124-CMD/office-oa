package com.smartoa.service;

import com.smartoa.entity.Role;
import com.smartoa.entity.UserRole;
import com.smartoa.entity.Permission;

import java.util.List;

/**
 * 角色管理服务接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
public interface RoleService {

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 根据ID查找角色
     */
    Role findById(Long roleId);

    /**
     * 根据角色代码查找角色
     */
    Role findByRoleCode(String roleCode);

    /**
     * 查找所有启用的角色
     */
    List<Role> findEnabledRoles();

    /**
     * 根据用户ID查找用户的角�?
     */
    List<Role> findRolesByUserId(Long userId);

    /**
     * 为用户分配角�?
     */
    void assignRoleToUser(Long userId, Long roleId, Long assignedBy);

    /**
     * 为用户分配多个角�?
     */
    void assignRolesToUser(Long userId, List<Long> roleIds, Long assignedBy);

    /**
     * 移除用户的角�?
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * 移除用户的所有角�?
     */
    void removeAllRolesFromUser(Long userId);

    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 检查用户是否有指定角色ID
     */
    boolean hasRoleById(Long userId, Long roleId);

    /**
     * 获取角色的权限列�?
     */
    List<Permission> getRolePermissions(Long roleId);

    /**
     * 为角色分配权�?
     */
    void assignPermissionToRole(Long roleId, Long permissionId, Long grantedBy);

    /**
     * 为角色分配多个权�?
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long grantedBy);

    /**
     * 移除角色的权�?
     */
    void removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * 移除角色的所有权�?
     */
    void removeAllPermissionsFromRole(Long roleId);

    /**
     * 根据用户ID获取用户的所有权�?
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 启用角色
     */
    void enableRole(Long roleId);

    /**
     * 禁用角色
     */
    void disableRole(Long roleId);

    /**
     * 统计角色的用户数�?
     */
    long countUsersByRole(Long roleId);

    /**
     * 分页查询角色
     */
    List<Role> findRoles(String roleName, Integer status, int page, int size);

    /**
     * 检查角色代码是否存�?
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 检查角色名称是否存在（排除指定ID�?
     */
    boolean existsByRoleNameAndIdNot(String roleName, Long id);
}
