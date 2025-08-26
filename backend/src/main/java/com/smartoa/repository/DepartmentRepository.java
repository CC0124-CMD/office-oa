package com.smartoa.repository;

import com.smartoa.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门Repository
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    /**
     * 根据父部门ID查询子部�?
     */
    List<Department> findByParentIdOrderByCreateTimeAsc(Long parentId);
    
    /**
     * 根据状态查询部�?
     */
    List<Department> findByStatusOrderByCreateTimeAsc(Department.Status status);
    
    /**
     * 根据部门编码查询
     */
    Department findByDeptCode(String deptCode);
    
    /**
     * 查询所有启用的部门
     */
    @Query("SELECT d FROM Department d WHERE d.status = 1 ORDER BY d.createTime ASC")
    List<Department> findAllEnabled();
    
    /**
     * 根据部门名称模糊查询
     */
    List<Department> findByDeptNameContainingAndStatus(String deptName, Department.Status status);
    
    /**
     * 检查部门编码是否存�?
     */
    boolean existsByDeptCode(String deptCode);
    
    /**
     * 统计子部门数�?
     */
    long countByParentId(Long parentId);
} 
