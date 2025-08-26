package com.smartoa.service.impl;

import com.smartoa.entity.Role;
import com.smartoa.entity.UserRole;
import com.smartoa.entity.Permission;
import com.smartoa.entity.RolePermission;
import com.smartoa.repository.RoleRepository;
import com.smartoa.repository.UserRoleRepository;
import com.smartoa.repository.PermissionRepository;
import com.smartoa.repository.RolePermissionRepository;
import com.smartoa.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public Role createRole(Role role) {
        log.info("创建角色: {}", role.getRoleName());
        
        // 检查角色代码是否已存在
        if (existsByRoleCode(role.getRoleCode())) {
            throw new RuntimeException("角色代码已存�? " + role.getRoleCode());
        }
        
        // 设置默认状�?
        if (role.getStatus() == null) {
            role.setStatus(Role.Status.ENABLED.getValue());
        }
        
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Role role) {
        log.info("更新角色: {}", role.getId());
        
        Role existingRole = findById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存�? " + role.getId());
        }
        
        // 检查角色名称是否重复（排除当前角色�?
        if (existsByRoleNameAndIdNot(role.getRoleName(), role.getId())) {
            throw new RuntimeException("角色名称已存�? " + role.getRoleName());
        }
        
        // 更新字段
        existingRole.setRoleName(role.getRoleName());
        existingRole.setDescription(role.getDescription());
        existingRole.setPermissions(role.getPermissions());
        existingRole.setStatus(role.getStatus());
        existingRole.setUpdatedBy(role.getUpdatedBy());
        
        return roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        log.info("删除角色: {}", roleId);
        
        Role role = findById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存�? " + roleId);
        }
        
        // 检查是否有用户使用该角�?
        long userCount = countUsersByRole(roleId);
        if (userCount > 0) {
            throw new RuntimeException("该角色正在被 " + userCount + " 个用户使用，无法删除");
        }
        
        // 删除角色权限关联
        removeAllPermissionsFromRole(roleId);
        
        // 删除角色
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    @Override
    public Role findByRoleCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode).orElse(null);
    }

    @Override
    public List<Role> findEnabledRoles() {
        return roleRepository.findEnabledRoles();
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId, Long assignedBy) {
        log.info("为用�?{} 分配角色 {}", userId, roleId);
        
        // 检查是否已经分�?
        if (hasRoleById(userId, roleId)) {
            log.warn("用户 {} 已经拥有角色 {}", userId, roleId);
            return;
        }
        
        // 创建用户角色关联
        UserRole userRole = new UserRole(userId, roleId, assignedBy);
        userRoleRepository.save(userRole);
    }

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds, Long assignedBy) {
        log.info("为用�?{} 分配角色列表: {}", userId, roleIds);
        
        // 先移除用户的所有角�?
        removeAllRolesFromUser(userId);
        
        // 分配新角�?
        for (Long roleId : roleIds) {
            assignRoleToUser(userId, roleId, assignedBy);
        }
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        log.info("移除用户 {} 的角�?{}", userId, roleId);
        
        userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .ifPresent(userRoleRepository::delete);
    }

    @Override
    @Transactional
    public void removeAllRolesFromUser(Long userId) {
        log.info("移除用户 {} 的所有角�?, userId);
        userRoleRepository.deleteByUserId(userId);
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        Role role = findByRoleCode(roleCode);
        if (role == null) {
            return false;
        }
        return hasRoleById(userId, role.getId());
    }

    @Override
    public boolean hasRoleById(Long userId, Long roleId) {
        return userRoleRepository.existsByUserIdAndRoleIdAndStatus(userId, roleId);
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionRepository.findPermissionsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void assignPermissionToRole(Long roleId, Long permissionId, Long grantedBy) {
        log.info("为角�?{} 分配权限 {}", roleId, permissionId);
        
        // 检查是否已经分�?
        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            log.warn("角色 {} 已经拥有权限 {}", roleId, permissionId);
            return;
        }
        
        // 创建角色权限关联
        RolePermission rolePermission = new RolePermission(roleId, permissionId, grantedBy);
        rolePermissionRepository.save(rolePermission);
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds, Long grantedBy) {
        log.info("为角�?{} 分配权限列表: {}", roleId, permissionIds);
        
        // 先移除角色的所有权�?
        removeAllPermissionsFromRole(roleId);
        
        // 分配新权�?
        for (Long permissionId : permissionIds) {
            assignPermissionToRole(roleId, permissionId, grantedBy);
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        log.info("移除角色 {} 的权�?{}", roleId, permissionId);
        
        rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .ifPresent(rolePermissionRepository::delete);
    }

    @Override
    @Transactional
    public void removeAllPermissionsFromRole(Long roleId) {
        log.info("移除角色 {} 的所有权�?, roleId);
        rolePermissionRepository.deleteByRoleId(roleId);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<Permission> permissions = permissionRepository.findPermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        List<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permission) || userPermissions.contains("*");
    }

    @Override
    @Transactional
    public void enableRole(Long roleId) {
        log.info("启用角色: {}", roleId);
        
        Role role = findById(roleId);
        if (role != null) {
            role.setStatus(Role.Status.ENABLED.getValue());
            roleRepository.save(role);
        }
    }

    @Override
    @Transactional
    public void disableRole(Long roleId) {
        log.info("禁用角色: {}", roleId);
        
        Role role = findById(roleId);
        if (role != null) {
            role.setStatus(Role.Status.DISABLED.getValue());
            roleRepository.save(role);
        }
    }

    @Override
    public long countUsersByRole(Long roleId) {
        return userRoleRepository.countUsersByRoleId(roleId);
    }

    @Override
    public List<Role> findRoles(String roleName, Integer status, int page, int size) {
        // 这里简化实现，实际项目中应该使�?Specification �?Criteria API
        if (roleName != null && !roleName.trim().isEmpty()) {
            return roleRepository.findByRoleNameContaining(roleName);
        } else if (status != null) {
            return roleRepository.findByStatus(status);
        } else {
            Pageable pageable = PageRequest.of(page, size);
            return roleRepository.findAll(pageable).getContent();
        }
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }

    @Override
    public boolean existsByRoleNameAndIdNot(String roleName, Long id) {
        return roleRepository.existsByRoleNameAndIdNot(roleName, id);
    }
}
