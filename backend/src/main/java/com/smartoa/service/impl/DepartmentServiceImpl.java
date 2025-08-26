package com.smartoa.service.impl;

import com.smartoa.entity.Department;
import com.smartoa.repository.DepartmentRepository;
import com.smartoa.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 部门服务实现�?
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    @Override
    public Department createDepartment(Department department) {
        // 检查部门编码是否已存在
        if (departmentRepository.existsByDeptCode(department.getDeptCode())) {
            throw new RuntimeException("部门编码已存�?);
        }
        
        // 检查父部门是否存在
        if (department.getParentId() != null && department.getParentId() > 0) {
            Optional<Department> parentDept = departmentRepository.findById(department.getParentId());
            if (parentDept.isEmpty()) {
                throw new RuntimeException("父部门不存在");
            }
        }
        
        department.setStatus(Department.Status.ENABLED);
        return departmentRepository.save(department);
    }
    
    @Override
    public Department updateDepartment(Long id, Department department) {
        Department existingDept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存�?));
        
        // 检查部门编码是否重复（排除自己�?
        if (!existingDept.getDeptCode().equals(department.getDeptCode()) &&
                departmentRepository.existsByDeptCode(department.getDeptCode())) {
            throw new RuntimeException("部门编码已存�?);
        }
        
        // 检查是否将部门设置为自己的子部�?
        if (department.getParentId() != null && department.getParentId().equals(id)) {
            throw new RuntimeException("不能将部门设置为自己的子部门");
        }
        
        existingDept.setDeptName(department.getDeptName());
        existingDept.setDeptCode(department.getDeptCode());
        existingDept.setParentId(department.getParentId());
        existingDept.setLeader(department.getLeader());
        existingDept.setPhone(department.getPhone());
        existingDept.setEmail(department.getEmail());
        
        return departmentRepository.save(existingDept);
    }
    
    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存�?));
        
        // 检查是否有子部�?
        long childCount = departmentRepository.countByParentId(id);
        if (childCount > 0) {
            throw new RuntimeException("该部门下还有子部门，无法删除");
        }
        
        departmentRepository.deleteById(id);
    }
    
    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存�?));
    }
    
    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAllEnabled();
    }
    
    @Override
    public Page<Department> getDepartmentsByPage(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }
    
    @Override
    public List<Department> getDepartmentsByParentId(Long parentId) {
        return departmentRepository.findByParentIdOrderByCreateTimeAsc(parentId);
    }
    
    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepts = departmentRepository.findAllEnabled();
        return buildDepartmentTree(allDepts, 0L);
    }
    
    @Override
    public List<Department> getDepartmentsByStatus(Department.Status status) {
        return departmentRepository.findByStatusOrderByCreateTimeAsc(status);
    }
    
    @Override
    public Department getDepartmentByCode(String deptCode) {
        return departmentRepository.findByDeptCode(deptCode);
    }
    
    @Override
    public boolean existsByDeptCode(String deptCode) {
        return departmentRepository.existsByDeptCode(deptCode);
    }
    
    @Override
    public List<Department> searchDepartmentsByName(String deptName) {
        return departmentRepository.findByDeptNameContainingAndStatus(deptName, Department.Status.ENABLED);
    }
    
    @Override
    public Department updateDepartmentStatus(Long id, Department.Status status) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存�?));
        department.setStatus(status);
        return departmentRepository.save(department);
    }
    
    @Override
    public List<Department> getDepartmentPath(Long deptId) {
        List<Department> path = new ArrayList<>();
        Department current = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("部门不存�?));
        
        // 从当前部门向上查找，构建路径
        while (current != null) {
            path.add(0, current);
            if (current.getParentId() != null && current.getParentId() > 0) {
                current = departmentRepository.findById(current.getParentId()).orElse(null);
            } else {
                current = null;
            }
        }
        
        return path;
    }
    
    /**
     * 构建部门树结�?
     */
    private List<Department> buildDepartmentTree(List<Department> departments, Long parentId) {
        List<Department> tree = new ArrayList<>();
        
        for (Department dept : departments) {
            if (dept.getParentId().equals(parentId)) {
                // 递归构建子部�?
                List<Department> children = buildDepartmentTree(departments, dept.getId());
                // 这里可以设置子部门，如果需要的�?
                tree.add(dept);
            }
        }
        
        return tree;
    }
} 
