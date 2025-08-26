package com.smartoa.controller;

import com.smartoa.common.Result;
import com.smartoa.entity.Department;
import com.smartoa.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制�?
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Department> createDepartment(@Validated @RequestBody Department department) {
        try {
            Department created = departmentService.createDepartment(department);
            return Result.success("部门创建成功", created);
        } catch (Exception e) {
            log.error("创建部门失败", e);
            return Result.error("创建部门失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Department> updateDepartment(@PathVariable Long id, @Validated @RequestBody Department department) {
        try {
            Department updated = departmentService.updateDepartment(id, department);
            return Result.success("部门更新成功", updated);
        } catch (Exception e) {
            log.error("更新部门失败", e);
            return Result.error("更新部门失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return Result.success("部门删除成功", null);
        } catch (Exception e) {
            log.error("删除部门失败", e);
            return Result.error("删除部门失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Department> getDepartment(@PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return Result.success(department);
        } catch (Exception e) {
            log.error("获取部门详情失败", e);
            return Result.error("获取部门详情失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    public Result<Page<Department>> getDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Department> departments = departmentService.getDepartmentsByPage(pageable);
            return Result.success(departments);
        } catch (Exception e) {
            log.error("获取部门列表失败", e);
            return Result.error("获取部门列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public Result<List<Department>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return Result.success(departments);
        } catch (Exception e) {
            log.error("获取所有部门失�?, e);
            return Result.error("获取所有部门失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/tree")
    public Result<List<Department>> getDepartmentTree() {
        try {
            List<Department> tree = departmentService.getDepartmentTree();
            return Result.success(tree);
        } catch (Exception e) {
            log.error("获取部门树失�?, e);
            return Result.error("获取部门树失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/parent/{parentId}")
    public Result<List<Department>> getDepartmentsByParentId(@PathVariable Long parentId) {
        try {
            List<Department> departments = departmentService.getDepartmentsByParentId(parentId);
            return Result.success(departments);
        } catch (Exception e) {
            log.error("获取子部门失�?, e);
            return Result.error("获取子部门失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public Result<List<Department>> searchDepartments(@RequestParam String name) {
        try {
            List<Department> departments = departmentService.searchDepartmentsByName(name);
            return Result.success(departments);
        } catch (Exception e) {
            log.error("搜索部门失败", e);
            return Result.error("搜索部门失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Department> updateDepartmentStatus(@PathVariable Long id, @RequestParam Department.Status status) {
        try {
            Department department = departmentService.updateDepartmentStatus(id, status);
            return Result.success("部门状态更新成�?, department);
        } catch (Exception e) {
            log.error("更新部门状态失�?, e);
            return Result.error("更新部门状态失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/path")
    public Result<List<Department>> getDepartmentPath(@PathVariable Long id) {
        try {
            List<Department> path = departmentService.getDepartmentPath(id);
            return Result.success(path);
        } catch (Exception e) {
            log.error("获取部门路径失败", e);
            return Result.error("获取部门路径失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/code/{deptCode}")
    public Result<Department> getDepartmentByCode(@PathVariable String deptCode) {
        try {
            Department department = departmentService.getDepartmentByCode(deptCode);
            if (department == null) {
                return Result.error("部门不存�?);
            }
            return Result.success(department);
        } catch (Exception e) {
            log.error("根据编码获取部门失败", e);
            return Result.error("根据编码获取部门失败: " + e.getMessage());
        }
    }
} 
