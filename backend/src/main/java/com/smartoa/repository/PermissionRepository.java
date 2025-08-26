package com.smartoa.repository;

import com.smartoa.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限代码查找权限
     */
    Optional<Permission> findByPermissionCode(String permissionCode);

    /**
     * 根据权限代码列表查找权限
     */
    List<Permission> findByPermissionCodeIn(List<String> permissionCodes);

    /**
     * 根据资源查找权限
     */
    List<Permission> findByResource(String resource);

    /**
     * 根据资源和操作查找权�?
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * 根据状态查找权�?
     */
    List<Permission> findByStatus(Integer status);

    /**
     * 查找启用的权�?
     */
    @Query("SELECT p FROM Permission p WHERE p.status = 1 ORDER BY p.sortOrder, p.id")
    List<Permission> findEnabledPermissions();

    /**
     * 根据父权限ID查找子权�?
     */
    List<Permission> findByParentIdAndStatus(Long parentId, Integer status);

    /**
     * 查找根权限（没有父权限的权限�?
     */
    @Query("SELECT p FROM Permission p WHERE p.parentId IS NULL AND p.status = 1 ORDER BY p.sortOrder")
    List<Permission> findRootPermissions();

    /**
     * 根据角色ID查找权限
     */
    @Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.id = rp.permissionId " +
           "WHERE rp.roleId = :roleId AND p.status = 1")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查找权限（通过角色�?
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND ur.status = 1 AND p.status = 1")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 检查权限代码是否存�?
     */
    boolean existsByPermissionCode(String permissionCode);

    /**
     * 统计权限数量
     */
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.status = 1")
    long countEnabledPermissions();
}
