package com.smartoa.repository;

import com.smartoa.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色关联数据访问接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * 根据用户ID查找用户角色关联
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * 根据角色ID查找用户角色关联
     */
    List<UserRole> findByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID查找关联
     */
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 根据用户ID查找有效的角色关�?
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.status = 1")
    List<UserRole> findActiveByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查找有效的用户关�?
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :roleId AND ur.status = 1")
    List<UserRole> findActiveByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否有指定角色
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId AND ur.status = 1")
    boolean existsByUserIdAndRoleIdAndStatus(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色关�?
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关�?
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 停用用户的所有角色关�?
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.status = 0 WHERE ur.userId = :userId")
    void deactivateByUserId(@Param("userId") Long userId);

    /**
     * 停用角色的所有用户关�?
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.status = 0 WHERE ur.roleId = :roleId")
    void deactivateByRoleId(@Param("roleId") Long roleId);

    /**
     * 激活用户角色关�?
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.status = 1 WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void activateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 统计角色的用户数�?
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.roleId = :roleId AND ur.status = 1")
    long countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计用户的角色数�?
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.userId = :userId AND ur.status = 1")
    long countRolesByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id, assigned_by, status) VALUES (:userId, :roleId, :assignedBy, 1)", 
           nativeQuery = true)
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId, @Param("assignedBy") Long assignedBy);
}
