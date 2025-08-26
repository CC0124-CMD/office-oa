-- 智能OA系统数据库脚本
-- 作者：陈志培
-- 创建时间：2024-01-01

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_oa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_oa;

-- 1. 部门表
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_code VARCHAR(50) COMMENT '部门编码',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_code (dept_code)
) COMMENT '部门表';

-- 2. 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    dept_id BIGINT COMMENT '部门ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (dept_id) REFERENCES sys_department(id),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_dept_id (dept_id)
) COMMENT '用户表';

-- 3. 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_code (role_code)
) COMMENT '角色表';

-- 4. 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_type TINYINT NOT NULL COMMENT '权限类型(1:菜单 2:按钮)',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) COMMENT '路径',
    component VARCHAR(200) COMMENT '组件',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_code (permission_code)
) COMMENT '权限表';

-- 5. 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 6. 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 7. 流程定义表
CREATE TABLE act_process_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流程定义ID',
    process_key VARCHAR(100) NOT NULL COMMENT '流程标识',
    process_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    process_version INT DEFAULT 1 COMMENT '流程版本',
    process_xml TEXT COMMENT '流程XML',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_process_key (process_key)
) COMMENT '流程定义表';

-- 8. 流程实例表
CREATE TABLE act_process_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流程实例ID',
    process_definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    business_key VARCHAR(100) COMMENT '业务标识',
    initiator BIGINT NOT NULL COMMENT '发起人',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态(1:进行中 2:已完成 3:已终止)',
    FOREIGN KEY (process_definition_id) REFERENCES act_process_definition(id),
    FOREIGN KEY (initiator) REFERENCES sys_user(id),
    INDEX idx_business_key (business_key),
    INDEX idx_initiator (initiator)
) COMMENT '流程实例表';

-- 9. 任务表
CREATE TABLE act_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    process_instance_id BIGINT NOT NULL COMMENT '流程实例ID',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    assignee BIGINT COMMENT '执行人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    due_date DATETIME COMMENT '到期时间',
    priority TINYINT DEFAULT 0 COMMENT '优先级(0:普通 1:重要 2:紧急)',
    status TINYINT DEFAULT 1 COMMENT '状态(1:待办 2:进行中 3:已完成)',
    FOREIGN KEY (process_instance_id) REFERENCES act_process_instance(id),
    FOREIGN KEY (assignee) REFERENCES sys_user(id),
    INDEX idx_assignee (assignee),
    INDEX idx_status (status)
) COMMENT '任务表';

-- 10. 表单表
CREATE TABLE act_form (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '表单ID',
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_type VARCHAR(50) NOT NULL COMMENT '表单类型',
    form_data JSON COMMENT '表单数据',
    task_id BIGINT COMMENT '任务ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES act_task(id),
    INDEX idx_task_id (task_id)
) COMMENT '表单表';

-- 11. 考勤记录表
CREATE TABLE att_attendance_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考勤记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    date DATE NOT NULL COMMENT '日期',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    work_hours DECIMAL(4,2) COMMENT '工作时长(小时)',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常 2:迟到 3:早退 4:缺勤)',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    UNIQUE KEY uk_user_date (user_id, date),
    INDEX idx_date (date),
    INDEX idx_status (status)
) COMMENT '考勤记录表';

-- 12. 考勤异常记录表
CREATE TABLE att_attendance_exception (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '异常记录ID',
    attendance_record_id BIGINT NOT NULL COMMENT '考勤记录ID',
    exception_type TINYINT NOT NULL COMMENT '异常类型(1:迟到 2:早退 3:缺勤 4:加班)',
    exception_reason VARCHAR(200) COMMENT '异常原因',
    apply_time DATETIME COMMENT '申请时间',
    approve_time DATETIME COMMENT '审批时间',
    approver_id BIGINT COMMENT '审批人ID',
    status TINYINT DEFAULT 1 COMMENT '状态(1:待审批 2:已通过 3:已拒绝)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (attendance_record_id) REFERENCES att_attendance_record(id),
    FOREIGN KEY (approver_id) REFERENCES sys_user(id),
    INDEX idx_attendance_record_id (attendance_record_id),
    INDEX idx_status (status)
) COMMENT '考勤异常记录表';

-- 13. 公告表
CREATE TABLE sys_announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    publisher_id BIGINT NOT NULL COMMENT '发布人ID',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status TINYINT DEFAULT 1 COMMENT '状态(0:草稿 1:已发布 2:已撤回)',
    priority TINYINT DEFAULT 0 COMMENT '优先级(0:普通 1:重要 2:紧急)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (publisher_id) REFERENCES sys_user(id),
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) COMMENT '公告表';

-- 14. 通知表
CREATE TABLE sys_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT DEFAULT 1 COMMENT '类型(1:系统通知 2:审批通知 3:考勤通知)',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读 1:已读)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type)
) COMMENT '通知表';

-- 15. 文件夹表
CREATE TABLE sys_folder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件夹ID',
    folder_name VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父文件夹ID',
    owner_id BIGINT NOT NULL COMMENT '所有者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (owner_id) REFERENCES sys_user(id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_owner_id (owner_id)
) COMMENT '文件夹表';

-- 16. 文件表
CREATE TABLE sys_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件类型',
    upload_user_id BIGINT NOT NULL COMMENT '上传用户ID',
    folder_id BIGINT COMMENT '文件夹ID',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status TINYINT DEFAULT 1 COMMENT '状态(0:删除 1:正常)',
    FOREIGN KEY (upload_user_id) REFERENCES sys_user(id),
    FOREIGN KEY (folder_id) REFERENCES sys_folder(id),
    INDEX idx_upload_user_id (upload_user_id),
    INDEX idx_folder_id (folder_id),
    INDEX idx_file_type (file_type)
) COMMENT '文件表';

-- 17. 文件权限表
CREATE TABLE sys_file_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    user_id BIGINT COMMENT '用户ID',
    permission_type TINYINT NOT NULL COMMENT '权限类型(1:查看 2:下载 3:编辑 4:删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (file_id) REFERENCES sys_file(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    UNIQUE KEY uk_file_user_permission (file_id, user_id, permission_type)
) COMMENT '文件权限表';

-- 18. 系统日志表
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法名',
    params TEXT COMMENT '参数',
    time BIGINT COMMENT '执行时长(毫秒)',
    ip VARCHAR(64) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT '系统日志表';

-- 插入初始数据

-- 插入部门数据
INSERT INTO sys_department (id, dept_name, parent_id, dept_code, leader, phone, email) VALUES
(1, '总经理办公室', 0, 'GM', '张三', '13800138001', 'gm@company.com'),
(2, '技术部', 0, 'TECH', '李四', '13800138002', 'tech@company.com'),
(3, '人事部', 0, 'HR', '王五', '13800138003', 'hr@company.com'),
(4, '财务部', 0, 'FINANCE', '赵六', '13800138004', 'finance@company.com'),
(5, '前端组', 2, 'TECH-FRONTEND', '孙七', '13800138005', 'frontend@company.com'),
(6, '后端组', 2, 'TECH-BACKEND', '周八', '13800138006', 'backend@company.com');

-- 插入角色数据
INSERT INTO sys_role (id, role_name, role_code, description) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员'),
(2, '管理员', 'ADMIN', '系统管理员'),
(3, '部门经理', 'MANAGER', '部门经理'),
(4, '普通员工', 'EMPLOYEE', '普通员工');

-- 插入权限数据
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order) VALUES
(1, '系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 1),
(2, '用户管理', 'system:user', 1, 1, '/system/user', 'system/user/index', 'user', 1),
(3, '角色管理', 'system:role', 1, 1, '/system/role', 'system/role/index', 'peoples', 2),
(4, '部门管理', 'system:dept', 1, 1, '/system/dept', 'system/dept/index', 'tree', 3),
(5, '权限管理', 'system:permission', 1, 1, '/system/permission', 'system/permission/index', 'lock', 4),
(6, '审批管理', 'approval', 1, 0, '/approval', 'Layout', 'documentation', 2),
(7, '流程管理', 'approval:process', 1, 6, '/approval/process', 'approval/process/index', 'edit', 1),
(8, '我的审批', 'approval:my', 1, 6, '/approval/my', 'approval/my/index', 'form', 2),
(9, '考勤管理', 'attendance', 1, 0, '/attendance', 'Layout', 'time', 3),
(10, '考勤记录', 'attendance:record', 1, 9, '/attendance/record', 'attendance/record/index', 'list', 1),
(11, '考勤统计', 'attendance:statistics', 1, 9, '/attendance/statistics', 'attendance/statistics/index', 'chart', 2),
(12, '公告管理', 'announcement', 1, 0, '/announcement', 'Layout', 'message', 4),
(13, '公告列表', 'announcement:list', 1, 12, '/announcement/list', 'announcement/list/index', 'list', 1),
(14, '发布公告', 'announcement:publish', 1, 12, '/announcement/publish', 'announcement/publish/index', 'edit', 2),
(15, '文件管理', 'file', 1, 0, '/file', 'Layout', 'folder', 5),
(16, '文件列表', 'file:list', 1, 15, '/file/list', 'file/list/index', 'list', 1),
(17, '文件上传', 'file:upload', 1, 15, '/file/upload', 'file/upload/index', 'upload', 2);

-- 插入用户数据
INSERT INTO sys_user (id, username, password, real_name, email, phone, dept_id) VALUES
(1, 'admin', '$2a$10$7JB720yubVSOfvVWdBYoOeymFwJwQrK6qMxZxKjzWAg3vKGF9K3tG', '系统管理员', 'admin@company.com', '13800138000', 1),
(2, 'zhangsan', '$2a$10$7JB720yubVSOfvVWdBYoOeymFwJwQrK6qMxZxKjzWAg3vKGF9K3tG', '张三', 'zhangsan@company.com', '13800138001', 1),
(3, 'lisi', '$2a$10$7JB720yubVSOfvVWdBYoOeymFwJwQrK6qMxZxKjzWAg3vKGF9K3tG', '李四', 'lisi@company.com', '13800138002', 2),
(4, 'wangwu', '$2a$10$7JB720yubVSOfvVWdBYoOeymFwJwQrK6qMxZxKjzWAg3vKGF9K3tG', '王五', 'wangwu@company.com', '13800138003', 3),
(5, 'zhaoliu', '$2a$10$7JB720yubVSOfvVWdBYoOeymFwJwQrK6qMxZxKjzWAg3vKGF9K3tG', '赵六', 'zhaoliu@company.com', '13800138004', 4);

-- 插入用户角色关联数据
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin -> 超级管理员
(2, 3), -- zhangsan -> 部门经理
(3, 3), -- lisi -> 部门经理
(4, 3), -- wangwu -> 部门经理
(5, 4); -- zhaoliu -> 普通员工

-- 插入角色权限关联数据
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
-- 超级管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17),
-- 管理员拥有大部分权限
(2, 2), (2, 3), (2, 4), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17),
-- 部门经理拥有审批和考勤权限
(3, 6), (3, 7), (3, 8), (3, 9), (3, 10), (3, 11), (3, 12), (3, 13), (3, 14), (3, 15), (3, 16), (3, 17),
-- 普通员工拥有基本权限
(4, 8), (4, 9), (4, 10), (4, 12), (4, 13), (4, 15), (4, 16), (4, 17);

-- 插入流程定义数据
INSERT INTO act_process_definition (id, process_key, process_name, process_version, status) VALUES
(1, 'leave_approval', '请假审批流程', 1, 1),
(2, 'overtime_approval', '加班审批流程', 1, 1),
(3, 'expense_approval', '报销审批流程', 1, 1);

-- 插入公告数据
INSERT INTO sys_announcement (id, title, content, publisher_id, priority) VALUES
(1, '系统上线通知', '智能OA系统正式上线，请各位同事及时登录使用。', 1, 2),
(2, '考勤制度调整', '从下周开始，考勤时间调整为9:00-18:00，请各位同事注意。', 4, 1),
(3, '年终总结会议', '定于本周五下午2点召开年终总结会议，请各部门负责人准时参加。', 2, 1);

-- 插入文件夹数据
INSERT INTO sys_folder (id, folder_name, parent_id, owner_id) VALUES
(1, '公共文件夹', 0, 1),
(2, '技术文档', 1, 3),
(3, '人事资料', 1, 4),
(4, '财务资料', 1, 5);

COMMIT; 