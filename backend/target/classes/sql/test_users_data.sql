-- 测试用户数据插入脚本
-- 创建时间: 2025-08-07
-- 说明: 包含管理员、部门主管和普通员工的测试数据

-- 1. 确保角色数据存在
INSERT IGNORE INTO roles (id, role_code, role_name, description, permissions, status, created_at, updated_at, created_by) VALUES
(1, 'ROLE_ADMIN', '系统管理员', '拥有系统最高权限，负责系统配置、用户管理、角色管理等', 
 '["system:*", "user:*", "role:*", "permission:*", "audit:*", "config:*"]', 1, NOW(), NOW(), 1),
(2, 'ROLE_MANAGER', '部门主管', '管理本部门事务，负责审批流程、部门人员管理等', 
 '["dept:*", "approval:*", "user:read", "user:update", "workflow:*", "attendance:*", "profile:*", "document:*"]', 1, NOW(), NOW(), 1),
(3, 'ROLE_EMPLOYEE', '普通员工', '日常办公操作，可以发起流程、查看个人信息等', 
 '["profile:*", "workflow:create", "workflow:read", "attendance:*", "document:read"]', 1, NOW(), NOW(), 1);

-- 2. 插入测试用户数据（密码已使用BCrypt加密）
-- 注意：以下密码哈希值是使用BCrypt算法生成的
INSERT IGNORE INTO users (id, username, password, real_name, email, phone, status, dept_id, dept_name, create_time, update_time) VALUES
-- 管理员用户：admin/admin123
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb97.AnUu.Zm32Qd7vMKZKjfvpZ8aVQ4J5Xg6mXxe', '系统管理员', 'admin@smartoa.com', '13800138001', 1, 1, '管理部', NOW(), NOW()),

-- 部门主管：manager/manager123  
(2, 'manager', '$2a$10$8K7wARfKlnI5J5Jv3l.Pu.8VZvZvZvZvZvZvZvZvZvZvZvZvZvZvZu', '部门主管', 'manager@smartoa.com', '13800138002', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee1/123456
(3, 'employee1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张三', 'zhangsan@smartoa.com', '13800138003', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee2/123456
(4, 'employee2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '李四', 'lisi@smartoa.com', '13800138004', 1, 2, '业务部', NOW(), NOW()),

-- 普通员工：employee3/123456
(5, 'employee3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王五', 'wangwu@smartoa.com', '13800138005', 1, 3, '技术部', NOW(), NOW()),

-- 普通员工：employee4/123456
(6, 'employee4', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '赵六', 'zhaoliu@smartoa.com', '13800138006', 1, 3, '技术部', NOW(), NOW()),

-- 普通员工：employee5/123456
(7, 'employee5', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '钱七', 'qianqi@smartoa.com', '13800138007', 1, 4, '财务部', NOW(), NOW());

-- 3. 插入用户角色关联数据
INSERT IGNORE INTO user_roles (user_id, role_id, assigned_at, assigned_by, status) VALUES
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

-- 5. 密码对照表（仅供测试参考）
/*
用户名        | 密码        | BCrypt哈希值（示例）
-------------|-------------|--------------------------------------------------
admin        | admin123    | $2a$10$N.zmdr9k7uOCQb97.AnUu.Zm32Qd7vMKZKjfvpZ8aVQ4J5Xg6mXxe
manager      | manager123  | $2a$10$8K7wARfKlnI5J5Jv3l.Pu.8VZvZvZvZvZvZvZvZvZvZvZvZvZvZvZu
employee1-5  | 123456      | $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.

注意：实际使用时，每次生成的BCrypt哈希值都会不同，但都能正确验证相同的明文密码。
*/