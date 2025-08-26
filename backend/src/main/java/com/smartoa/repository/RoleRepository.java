package com.smartoa.repository;

import com.smartoa.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色代码查找角色
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 根据角色代码列表查找角色
     */
    List<Role> findByRoleCodeIn(List<String> roleCodes);

    /**
     * 根据状态查找角�?
     */
    List<Role> findByStatus(Integer status);

    /**
     * 查找启用的角�?
     */
    @Query("SELECT r FROM Role r WHERE r.status = 1 ORDER BY r.id")
    List<Role> findEnabledRoles();

    /**
     * 根据角色名称模糊查询
     */
    List<Role> findByRoleNameContaining(String roleName);

    /**
     * 检查角色代码是否存�?
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 检查角色名称是否存在（排除指定ID�?
     */
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.roleName = :roleName AND r.id != :id")
    boolean existsByRoleNameAndIdNot(@Param("roleName") String roleName, @Param("id") Long id);

    /**
     * 根据用户ID查找用户的角�?
     */
    @Query("SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.roleId " +
           "WHERE ur.userId = :userId AND ur.status = 1 AND r.status = 1")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色数量
     */
    @Query("SELECT COUNT(r) FROM Role r WHERE r.status = 1")
    long countEnabledRoles();
}
