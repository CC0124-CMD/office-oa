package com.smartoa.service;

import com.smartoa.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService {
    
    /**
     * 创建部门
     */
    Department createDepartment(Department department);
    
    /**
     * 更新部门
     */
    Department updateDepartment(Long id, Department department);
    
    /**
     * 删除部门
     */
    void deleteDepartment(Long id);
    
    /**
     * 根据ID查询部门
     */
    Department getDepartmentById(Long id);
    
    /**
     * 查询所有部�?
     */
    List<Department> getAllDepartments();
    
    /**
     * 分页查询部门
     */
    Page<Department> getDepartmentsByPage(Pageable pageable);
    
    /**
     * 根据父部门ID查询子部�?
     */
    List<Department> getDepartmentsByParentId(Long parentId);
    
    /**
     * 查询部门树结�?
     */
    List<Department> getDepartmentTree();
    
    /**
     * 根据状态查询部�?
     */
    List<Department> getDepartmentsByStatus(Department.Status status);
    
    /**
     * 根据部门编码查询部门
     */
    Department getDepartmentByCode(String deptCode);
    
    /**
     * 检查部门编码是否存�?
     */
    boolean existsByDeptCode(String deptCode);
    
    /**
     * 根据部门名称模糊查询
     */
    List<Department> searchDepartmentsByName(String deptName);
    
    /**
     * 启用/禁用部门
     */
    Department updateDepartmentStatus(Long id, Department.Status status);
    
    /**
     * 获取部门层级路径
     */
    List<Department> getDepartmentPath(Long deptId);
} 
