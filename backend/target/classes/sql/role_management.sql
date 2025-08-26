-- 角色管理系统数据库表结构
-- 创建时间: 2025-08-06

-- 1. 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description TEXT COMMENT '角色描述',
    permissions TEXT COMMENT '权限列表（JSON格式）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 2. 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    assigned_by BIGINT COMMENT '分配人ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 3. 权限表（可选，用于更细粒度的权限控制）
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource VARCHAR(100) COMMENT '资源标识',
    action VARCHAR(50) COMMENT '操作类型',
    description TEXT COMMENT '权限描述',
    parent_id BIGINT COMMENT '父权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 4. 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    granted_by BIGINT COMMENT '授权人ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 5. 插入默认角色数据
INSERT INTO roles (role_code, role_name, description, permissions, created_by) VALUES
('ROLE_ADMIN', '系统管理员', '拥有系统最高权限，负责系统配置、用户管理、角色管理等', 
 '["system:*", "user:*", "role:*", "permission:*", "audit:*", "config:*"]', 1),
('ROLE_MANAGER', '部门主管', '管理本部门事务，负责审批流程、部门人员管理等', 
 '["dept:*", "approval:*", "employee:read", "employee:update", "report:*"]', 1),
('ROLE_EMPLOYEE', '普通员工', '日常办公操作，可以发起流程、查看个人信息等', 
 '["profile:*", "workflow:create", "workflow:read", "attendance:*", "document:read"]', 1);

-- 6. 插入默认权限数据
INSERT INTO permissions (permission_code, permission_name, resource, action, description, parent_id, sort_order) VALUES
-- 系统管理权限
('system:read', '系统查看', 'system', 'read', '查看系统信息', NULL, 1),
('system:write', '系统配置', 'system', 'write', '修改系统配置', NULL, 2),
('system:delete', '系统删除', 'system', 'delete', '删除系统数据', NULL, 3),

-- 用户管理权限
('user:read', '用户查看', 'user', 'read', '查看用户信息', NULL, 10),
('user:create', '用户创建', 'user', 'create', '创建新用户', NULL, 11),
('user:update', '用户更新', 'user', 'update', '更新用户信息', NULL, 12),
('user:delete', '用户删除', 'user', 'delete', '删除用户', NULL, 13),

-- 角色管理权限
('role:read', '角色查看', 'role', 'read', '查看角色信息', NULL, 20),
('role:create', '角色创建', 'role', 'create', '创建新角色', NULL, 21),
('role:update', '角色更新', 'role', 'update', '更新角色信息', NULL, 22),
('role:delete', '角色删除', 'role', 'delete', '删除角色', NULL, 23),

-- 部门管理权限
('dept:read', '部门查看', 'dept', 'read', '查看部门信息', NULL, 30),
('dept:create', '部门创建', 'dept', 'create', '创建新部门', NULL, 31),
('dept:update', '部门更新', 'dept', 'update', '更新部门信息', NULL, 32),
('dept:delete', '部门删除', 'dept', 'delete', '删除部门', NULL, 33),

-- 审批流程权限
('approval:read', '审批查看', 'approval', 'read', '查看审批流程', NULL, 40),
('approval:create', '审批创建', 'approval', 'create', '创建审批流程', NULL, 41),
('approval:update', '审批处理', 'approval', 'update', '处理审批流程', NULL, 42),
('approval:delete', '审批删除', 'approval', 'delete', '删除审批流程', NULL, 43),

-- 工作流权限
('workflow:read', '工作流查看', 'workflow', 'read', '查看工作流', NULL, 50),
('workflow:create', '工作流创建', 'workflow', 'create', '创建工作流', NULL, 51),
('workflow:update', '工作流更新', 'workflow', 'update', '更新工作流', NULL, 52),

-- 考勤管理权限
('attendance:read', '考勤查看', 'attendance', 'read', '查看考勤信息', NULL, 60),
('attendance:create', '考勤打卡', 'attendance', 'create', '考勤打卡', NULL, 61),
('attendance:update', '考勤更新', 'attendance', 'update', '更新考勤信息', NULL, 62),

-- 个人信息权限
('profile:read', '个人信息查看', 'profile', 'read', '查看个人信息', NULL, 70),
('profile:update', '个人信息更新', 'profile', 'update', '更新个人信息', NULL, 71),

-- 文档管理权限
('document:read', '文档查看', 'document', 'read', '查看文档', NULL, 80),
('document:create', '文档创建', 'document', 'create', '创建文档', NULL, 81),
('document:update', '文档更新', 'document', 'update', '更新文档', NULL, 82),
('document:delete', '文档删除', 'document', 'delete', '删除文档', NULL, 83);

-- 7. 为默认角色分配权限
-- 系统管理员权限（所有权限）
INSERT INTO role_permissions (role_id, permission_id, granted_by) 
SELECT 1, id, 1 FROM permissions;

-- 部门主管权限
INSERT INTO role_permissions (role_id, permission_id, granted_by) 
SELECT 2, id, 1 FROM permissions 
WHERE permission_code IN (
    'dept:read', 'dept:update', 
    'approval:read', 'approval:create', 'approval:update',
    'user:read', 'user:update',
    'workflow:read', 'workflow:create', 'workflow:update',
    'attendance:read', 'attendance:update',
    'profile:read', 'profile:update',
    'document:read', 'document:create', 'document:update'
);

-- 普通员工权限
INSERT INTO role_permissions (role_id, permission_id, granted_by) 
SELECT 3, id, 1 FROM permissions 
WHERE permission_code IN (
    'profile:read', 'profile:update',
    'workflow:read', 'workflow:create',
    'attendance:read', 'attendance:create',
    'document:read'
);

-- 8. 创建索引以提高查询性能
CREATE INDEX idx_roles_code ON roles(role_code);
CREATE INDEX idx_roles_status ON roles(status);
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_user_roles_status ON user_roles(status);
CREATE INDEX idx_permissions_code ON permissions(permission_code);
CREATE INDEX idx_permissions_resource ON permissions(resource);
CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);