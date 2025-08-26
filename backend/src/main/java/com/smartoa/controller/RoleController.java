package com.smartoa.controller;

import com.smartoa.entity.Role;
import com.smartoa.entity.Permission;
import com.smartoa.service.RoleService;
import com.smartoa.security.UserPrincipal;
import com.smartoa.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理控制�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Role> createRole(@Valid @RequestBody Role role, 
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("创建角色: {}, 操作�? {}", role.getRoleName(), currentUser.getUsername());
        
        role.setCreatedBy(currentUser.getId());
        Role createdRole = roleService.createRole(role);
        
        return Result.success(createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Role> updateRole(@PathVariable Long roleId, 
                                  @Valid @RequestBody Role role,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("更新角色: {}, 操作�? {}", roleId, currentUser.getUsername());
        
        role.setId(roleId);
        role.setUpdatedBy(currentUser.getId());
        Role updatedRole = roleService.updateRole(role);
        
        return Result.success(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteRole(@PathVariable Long roleId,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("删除角色: {}, 操作�? {}", roleId, currentUser.getUsername());
        
        roleService.deleteRole(roleId);
        
        return Result.success();
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Role> getRoleById(@PathVariable Long roleId) {
        Role role = roleService.findById(roleId);
        if (role == null) {
            return Result.error("角色不存�?);
        }
        return Result.success(role);
    }

    /**
     * 查询所有启用的角色
     */
    @GetMapping("/enabled")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<List<Role>> getEnabledRoles() {
        List<Role> roles = roleService.findEnabledRoles();
        return Result.success(roles);
    }

    /**
     * 分页查询角色
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Role>> getRoles(@RequestParam(required = false) String roleName,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        List<Role> roles = roleService.findRoles(roleName, status, page, size);
        return Result.success(roles);
    }

    /**
     * 启用角色
     */
    @PutMapping("/{roleId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> enableRole(@PathVariable Long roleId,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("启用角色: {}, 操作�? {}", roleId, currentUser.getUsername());
        
        roleService.enableRole(roleId);
        
        return Result.success();
    }

    /**
     * 禁用角色
     */
    @PutMapping("/{roleId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> disableRole(@PathVariable Long roleId,
                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("禁用角色: {}, 操作�? {}", roleId, currentUser.getUsername());
        
        roleService.disableRole(roleId);
        
        return Result.success();
    }

    /**
     * 获取角色的权限列�?
     */
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    /**
     * 为角色分配权�?
     */
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignPermissionsToRole(@PathVariable Long roleId,
                                               @RequestBody List<Long> permissionIds,
                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("为角�?{} 分配权限: {}, 操作�? {}", roleId, permissionIds, currentUser.getUsername());
        
        roleService.assignPermissionsToRole(roleId, permissionIds, currentUser.getId());
        
        return Result.success();
    }

    /**
     * 移除角色的权�?
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> removePermissionFromRole(@PathVariable Long roleId,
                                                @PathVariable Long permissionId,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("移除角色 {} 的权�?{}, 操作�? {}", roleId, permissionId, currentUser.getUsername());
        
        roleService.removePermissionFromRole(roleId, permissionId);
        
        return Result.success();
    }

    /**
     * 统计角色的用户数�?
     */
    @GetMapping("/{roleId}/users/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> countUsersByRole(@PathVariable Long roleId) {
        long count = roleService.countUsersByRole(roleId);
        return Result.success(count);
    }

    /**
     * 检查角色代码是否存�?
     */
    @GetMapping("/check-code")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> checkRoleCode(@RequestParam String roleCode) {
        boolean exists = roleService.existsByRoleCode(roleCode);
        return Result.success(exists);
    }
}
