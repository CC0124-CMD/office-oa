package com.smartoa.security;

import com.smartoa.entity.Role;
import com.smartoa.entity.User;
import com.smartoa.repository.UserRepository;
import com.smartoa.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存�? " + username));

        // 检查用户状�?
        if (!user.isEnabled()) {
            throw new RuntimeException("用户已被禁用: " + username);
        }

        // 获取用户角色
        List<Role> roles = roleService.findRolesByUserId(user.getId());
        user.setRoles(roles);

        // 获取用户权限
        List<String> permissions = roleService.getUserPermissions(user.getId());
        user.setPermissions(permissions);

        // 构建权限列表
        Collection<? extends GrantedAuthority> authorities = getAuthorities(roles, permissions);

        return UserPrincipal.create(user, authorities);
    }

    /**
     * 构建用户权限列表
     */
    private Collection<? extends GrantedAuthority> getAuthorities(List<Role> roles, List<String> permissions) {
        // 角色权限（以ROLE_开头）
        List<GrantedAuthority> roleAuthorities = roles.stream()
                .filter(Role::isEnabled)
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());

        // 功能权限
        List<GrantedAuthority> permissionAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 合并权限
        roleAuthorities.addAll(permissionAuthorities);
        
        log.debug("用户权限列表: {}", roleAuthorities);
        return roleAuthorities;
    }
}
