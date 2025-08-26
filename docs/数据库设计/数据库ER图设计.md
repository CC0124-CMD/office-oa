# 智能OA系统数据库设计

## 1. 数据库ER图

### 1.1 实体关系图概述
智能OA系统的数据库设计包含以下主要实体：

- **用户管理模块**：用户表、角色表、权限表、部门表
- **审批流程模块**：流程定义表、流程实例表、任务表、表单表
- **考勤管理模块**：考勤记录表、考勤规则表、异常记录表
- **公告通知模块**：公告表、通知表、消息推送表
- **文件管理模块**：文件表、文件夹表、文件权限表

### 1.2 核心实体关系

```
用户(User) 1:N 角色(Role)
角色(Role) M:N 权限(Permission)
用户(User) N:1 部门(Department)
部门(Department) 1:N 用户(User)

流程定义(ProcessDefinition) 1:N 流程实例(ProcessInstance)
流程实例(ProcessInstance) 1:N 任务(Task)
任务(Task) 1:1 表单(Form)

用户(User) 1:N 考勤记录(AttendanceRecord)
考勤记录(AttendanceRecord) 1:N 异常记录(AttendanceException)

用户(User) 1:N 公告(Announcement)
用户(User) 1:N 通知(Notification)

用户(User) 1:N 文件(File)
文件(File) 1:N 文件权限(FilePermission)
```

### 1.3 实体属性设计

#### 用户表 (sys_user)
- id: 主键
- username: 用户名
- password: 密码
- real_name: 真实姓名
- email: 邮箱
- phone: 手机号
- avatar: 头像
- status: 状态(启用/禁用)
- dept_id: 部门ID
- create_time: 创建时间
- update_time: 更新时间

#### 角色表 (sys_role)
- id: 主键
- role_name: 角色名称
- role_code: 角色编码
- description: 描述
- status: 状态
- create_time: 创建时间

#### 权限表 (sys_permission)
- id: 主键
- permission_name: 权限名称
- permission_code: 权限编码
- permission_type: 权限类型(菜单/按钮)
- parent_id: 父权限ID
- path: 路径
- component: 组件
- icon: 图标
- sort_order: 排序

#### 部门表 (sys_department)
- id: 主键
- dept_name: 部门名称
- parent_id: 父部门ID
- dept_code: 部门编码
- leader: 负责人
- phone: 联系电话
- email: 邮箱
- status: 状态

#### 流程定义表 (act_process_definition)
- id: 主键
- process_key: 流程标识
- process_name: 流程名称
- process_version: 流程版本
- process_xml: 流程XML
- status: 状态
- create_time: 创建时间

#### 流程实例表 (act_process_instance)
- id: 主键
- process_definition_id: 流程定义ID
- business_key: 业务标识
- initiator: 发起人
- start_time: 开始时间
- end_time: 结束时间
- status: 状态

#### 任务表 (act_task)
- id: 主键
- process_instance_id: 流程实例ID
- task_name: 任务名称
- assignee: 执行人
- create_time: 创建时间
- due_date: 到期时间
- priority: 优先级
- status: 状态

#### 考勤记录表 (att_attendance_record)
- id: 主键
- user_id: 用户ID
- date: 日期
- check_in_time: 签到时间
- check_out_time: 签退时间
- work_hours: 工作时长
- status: 状态(正常/迟到/早退/缺勤)

#### 公告表 (sys_announcement)
- id: 主键
- title: 标题
- content: 内容
- publisher_id: 发布人ID
- publish_time: 发布时间
- status: 状态
- priority: 优先级

#### 文件表 (sys_file)
- id: 主键
- file_name: 文件名
- original_name: 原始文件名
- file_path: 文件路径
- file_size: 文件大小
- file_type: 文件类型
- upload_user_id: 上传用户ID
- upload_time: 上传时间
- folder_id: 文件夹ID

## 2. 数据库设计原则

### 2.1 命名规范
- 表名使用下划线分隔，全小写
- 字段名使用下划线分隔，全小写
- 主键统一使用id
- 外键使用表名_id格式
- 时间字段使用_time后缀

### 2.2 索引设计
- 主键自动创建聚集索引
- 外键字段创建普通索引
- 查询频繁的字段创建索引
- 复合查询创建复合索引

### 2.3 约束设计
- 主键约束：每个表必须有主键
- 外键约束：保证数据完整性
- 非空约束：重要字段不允许为空
- 唯一约束：用户名、邮箱等字段唯一

## 3. 数据库优化策略

### 3.1 分表策略
- 考勤记录表按月份分表
- 日志表按日期分表
- 文件表按文件类型分表

### 3.2 缓存策略
- 用户信息缓存到Redis
- 权限信息缓存到Redis
- 系统配置缓存到Redis

### 3.3 备份策略
- 每日全量备份
- 每小时增量备份
- 异地备份存储 