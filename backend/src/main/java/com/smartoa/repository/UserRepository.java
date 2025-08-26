package com.smartoa.repository;

import com.smartoa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用�?
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用�?
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据状态查找用�?
     */
    List<User> findByStatus(Integer status);

    /**
     * 根据部门ID查找用户
     */
    List<User> findByDeptId(Long deptId);

    /**
     * 根据真实姓名模糊查询
     */
    List<User> findByRealNameContaining(String realName);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存�?
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查用户名是否存在（排除指定ID�?
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :id")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);

    /**
     * 检查邮箱是否存在（排除指定ID�?
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    /**
     * 检查手机号是否存在（排除指定ID�?
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.id != :id")
    boolean existsByPhoneAndIdNot(@Param("phone") String phone, @Param("id") Long id);

    /**
     * 查找启用的用�?
     */
    @Query("SELECT u FROM User u WHERE u.status = 1 ORDER BY u.id")
    List<User> findEnabledUsers();

    /**
     * 统计启用的用户数�?
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    long countEnabledUsers();

    /**
     * 根据部门ID统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deptId = :deptId AND u.status = 1")
    long countUsersByDeptId(@Param("deptId") Long deptId);
}
