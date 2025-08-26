-- 修复测试用户数据脚本
-- 创建时间: 2025-08-12
-- 说明: 使用正确的BCrypt密码哈希值

-- 1. 删除现有的测试用户数据
DELETE FROM user_roles WHERE user_id IN (1, 2, 3, 4, 5, 6, 7);
DELETE FROM users WHERE id IN (1, 2, 3, 4, 5, 6, 7);

-- 2. 重新插入测试用户数据（使用正确的BCrypt密码哈希）
INSERT INTO users (id, username, password, real_name, email, phone, status, dept_id, dept_name, create_time, update_time) VALUES
-- 管理员用户：admin/admin123
(1, 'admin', '$2a$10$FMjxWMmtWLM79yqe3xv2S.RVmALJGpKp.qkBD5OgkACbk13kDuV3u', '系统管理员', 'admin@smartoa.com', '13800138001', 1, 1, '管理部', NOW(), NOW()),

-- 部门主管：manager/manager123  
(2, 'manager', '$2a$10$UsqZqKCoBPXV.iRwB8Ti2.ZBbsshnEFZd8ZyYXxm5wXOejl.PCd8i', '部门主管', 'manager@smartoa.com', '13800138002', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee1/123456
(3, 'employee1', '$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy', '张三', 'zhangsan@smartoa.com', '13800138003', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee2/123456
(4, 'employee2', '$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy', '李四', 'lisi@smartoa.com', '13800138004', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee3/123456
(5, 'employee3', '$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy', '王五', 'wangwu@smartoa.com', '13800138005', 1, 3, '技术部', NOW(), NOW()),

-- 普通员工：employee4/123456
(6, 'employee4', '$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy', '赵六', 'zhaoliu@smartoa.com', '13800138006', 1, 3, '技术部', NOW(), NOW()),

-- 普通员工：employee5/123456
(7, 'employee5', '$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy', '钱七', 'qianqi@smartoa.com', '13800138007', 1, 4, '财务部', NOW(), NOW());

-- 3. 插入用户角色关联数据
INSERT INTO user_roles (user_id, role_id, assigned_at, assigned_by, status) VALUES
-- 管理员角色
(1, 1, NOW(), 1, 1),

-- 部门主管角色
(2, 2, NOW(), 1, 1),

-- 普通员工角色
(3, 3, NOW(), 1, 1),
(4, 3, NOW(), 1, 1),
(5, 3, NOW(), 1, 1),
(6, 3, NOW(), 1, 1),
(7, 3, NOW(), 1, 1);

-- 4. 验证数据插入结果
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.email,
    u.phone,
    u.dept_name,
    r.role_name,
    r.role_code,
    u.status as user_status,
    ur.status as role_status
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id AND ur.status = 1
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username IN ('admin', 'manager', 'employee1', 'employee2', 'employee3', 'employee4', 'employee5')
ORDER BY u.id;

-- 5. 密码对照表（测试用）
/*
用户名        | 密码        | 说明
-------------|-------------|----------------------------------
admin        | admin123    | 系统管理员
manager      | manager123  | 部门主管  
employee1-5  | 123456      | 普通员工

注意：所有密码都使用正确的BCrypt哈希值
*/ 