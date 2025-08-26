package com.smartoa.controller;

import com.smartoa.entity.Role;
import com.smartoa.service.RoleService;
import com.smartoa.security.UserPrincipal;
import com.smartoa.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色管理控制�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {

    private final RoleService roleService;

    /**
     * 获取用户的角色列�?
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Result<List<Role>> getUserRoles(@PathVariable Long userId) {
        List<Role> roles = roleService.findRolesByUserId(userId);
        return Result.success(roles);
    }

    /**
     * 为用户分配角�?
     */
    @PostMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignRoleToUser(@PathVariable Long userId,
                                        @PathVariable Long roleId,
                                        @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("为用�?{} 分配角色 {}, 操作�? {}", userId, roleId, currentUser.getUsername());
        
        roleService.assignRoleToUser(userId, roleId, currentUser.getId());
        
        return Result.success();
    }

    /**
     * 为用户分配多个角�?
     */
    @PostMapping("/user/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignRolesToUser(@PathVariable Long userId,
                                         @RequestBody List<Long> roleIds,
                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("为用�?{} 分配角色列表: {}, 操作�? {}", userId, roleIds, currentUser.getUsername());
        
        roleService.assignRolesToUser(userId, roleIds, currentUser.getId());
        
        return Result.success();
    }

    /**
     * 移除用户的角�?
     */
    @DeleteMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> removeRoleFromUser(@PathVariable Long userId,
                                          @PathVariable Long roleId,
                                          @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("移除用户 {} 的角�?{}, 操作�? {}", userId, roleId, currentUser.getUsername());
        
        roleService.removeRoleFromUser(userId, roleId);
        
        return Result.success();
    }

    /**
     * 移除用户的所有角�?
     */
    @DeleteMapping("/user/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> removeAllRolesFromUser(@PathVariable Long userId,
                                              @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("移除用户 {} 的所有角�? 操作�? {}", userId, currentUser.getUsername());
        
        roleService.removeAllRolesFromUser(userId);
        
        return Result.success();
    }

    /**
     * 检查用户是否有指定角色
     */
    @GetMapping("/user/{userId}/has-role")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Result<Boolean> hasRole(@PathVariable Long userId,
                                  @RequestParam String roleCode) {
        boolean hasRole = roleService.hasRole(userId, roleCode);
        return Result.success(hasRole);
    }

    /**
     * 获取用户的权限列�?
     */
    @GetMapping("/user/{userId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
        List<String> permissions = roleService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    /**
     * 检查用户是否有指定权限
     */
    @GetMapping("/user/{userId}/has-permission")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Result<Boolean> hasPermission(@PathVariable Long userId,
                                        @RequestParam String permission) {
        boolean hasPermission = roleService.hasPermission(userId, permission);
        return Result.success(hasPermission);
    }

    /**
     * 获取当前用户的角色列�?
     */
    @GetMapping("/current/roles")
    public Result<List<Role>> getCurrentUserRoles(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Role> roles = roleService.findRolesByUserId(currentUser.getId());
        return Result.success(roles);
    }

    /**
     * 获取当前用户的权限列�?
     */
    @GetMapping("/current/permissions")
    public Result<List<String>> getCurrentUserPermissions(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<String> permissions = roleService.getUserPermissions(currentUser.getId());
        return Result.success(permissions);
    }
}
