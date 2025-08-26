package com.smartoa.repository;

import com.smartoa.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 角色权限关联数据访问接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * 根据角色ID查找角色权限关联
     */
    List<RolePermission> findByRoleId(Long roleId);

    /**
     * 根据权限ID查找角色权限关联
     */
    List<RolePermission> findByPermissionId(Long permissionId);

    /**
     * 根据角色ID和权限ID查找关联
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 检查角色是否有指定权限
     */
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 删除角色的所有权限关�?
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除权限的所有角色关�?
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计权限的角色数�?
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计角色的权限数�?
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.roleId = :roleId")
    long countPermissionsByRoleId(@Param("roleId") Long roleId);
}
