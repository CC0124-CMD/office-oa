<template>
  <div class="user-management-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="user" class="title-icon" />
            用户管理
          </h1>
          <p class="page-subtitle">管理系统用户账户、权限和角色分配</p>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info">
          <a-avatar :size="32" class="user-avatar" :src="userInfo.avatar">
            <a-icon type="user" />
          </a-avatar>
          <span>欢迎，{{ userInfo.username }}</span>
        </div>
        <a-button type="link" @click="goHome" class="home-btn">
          <a-icon type="home" /> 返回首页
        </a-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-number">156</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">142</div>
          <div class="stat-label">活跃用户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🔒</div>
        <div class="stat-content">
          <div class="stat-number">8</div>
          <div class="stat-label">管理员</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-number">12</div>
          <div class="stat-label">本月新增</div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="user-list">
      <div class="list-header">
        <h2>用户列表</h2>
        <button class="btn-add" @click="showAddModal">+ 添加用户</button>
      </div>
      
      <div class="user-table">
        <table>
          <thead>
            <tr>
              <th>用户ID</th>
              <th>用户信息</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>
                <div class="user-info">
                  <img :src="user.avatar" :alt="user.name" class="avatar">
                  <span>{{ user.name }}</span>
                </div>
              </td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span class="role" :class="user.role">{{ user.roleText }}</span>
              </td>
              <td>
                <span class="status" :class="user.status">{{ user.statusText }}</span>
              </td>
              <td>{{ user.createTime }}</td>
              <td>
                <div class="actions">
                  <button class="btn-edit" @click="showEditModal(user)">编辑</button>
                  <button class="btn-delete" @click="showDeleteConfirm(user)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 添加/编辑用户模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑用户' : '添加用户' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label>用户ID</label>
              <input 
                type="text" 
                v-model="formData.id" 
                :disabled="isEditing"
                placeholder="请输入用户ID"
              >
              <div v-if="formErrors.id" class="form-error">{{ formErrors.id }}</div>
            </div>
            <div class="form-group">
              <label>姓名</label>
              <input 
                type="text" 
                v-model="formData.name" 
                placeholder="请输入姓名"
              >
              <div v-if="formErrors.name" class="form-error">{{ formErrors.name }}</div>
            </div>
            <div class="form-group">
              <label>用户名</label>
              <input 
                type="text" 
                v-model="formData.username" 
                placeholder="请输入用户名"
              >
              <div v-if="formErrors.username" class="form-error">{{ formErrors.username }}</div>
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input 
                type="email" 
                v-model="formData.email" 
                placeholder="请输入邮箱"
              >
              <div v-if="formErrors.email" class="form-error">{{ formErrors.email }}</div>
            </div>
            <div class="form-group">
              <label>角色</label>
              <select v-model="formData.role">
                <option value="">请选择角色</option>
                <option value="admin">管理员</option>
                <option value="employee">员工</option>
              </select>
              <div v-if="formErrors.role" class="form-error">{{ formErrors.role }}</div>
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="formData.status">
                <option value="">请选择状态</option>
                <option value="active">启用</option>
                <option value="inactive">禁用</option>
              </select>
              <div v-if="formErrors.status" class="form-error">{{ formErrors.status }}</div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeModal">取消</button>
          <button type="button" class="btn-submit" @click="submitForm">{{ isEditing ? '更新' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="modal-close" @click="closeDeleteModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>确定要删除用户 <strong>{{ userToDelete?.name }}</strong> 吗？</p>
          <p>此操作无法撤销。</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeDeleteModal">取消</button>
          <button type="button" class="btn-delete-confirm" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'UserManagement',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=UserManagement1'
    })
    
    // 用户数据
    const users = ref([
      {
        id: 'USR001',
        name: '张三',
        username: 'zhangsan',
        email: 'zhangsan@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User1',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-15'
      },
      {
        id: 'USR002',
        name: '李四',
        username: 'lisi',
        email: 'lisi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User2',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-14'
      },
      {
        id: 'USR003',
        name: '王五',
        username: 'wangwu',
        email: 'wangwu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User3',
        role: 'employee',
        roleText: '员工',
        status: 'inactive',
        statusText: '禁用',
        createTime: '2024-01-13'
      },
      {
        id: 'USR004',
        name: '赵六',
        username: 'zhaoliu',
        email: 'zhaoliu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User4',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-12'
      },
      {
        id: 'USR005',
        name: '钱七',
        username: 'qianqi',
        email: 'qianqi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User5',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-11'
      },
      {
        id: 'USR006',
        name: '孙八',
        username: 'sunba',
        email: 'sunba@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User6',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-10'
      },
      {
        id: 'USR007',
        name: '周九',
        username: 'zhoujiu',
        email: 'zhoujiu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User7',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-09'
      },
      {
        id: 'USR008',
        name: '吴十',
        username: 'wushi',
        email: 'wushi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User8',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-08'
      },
      {
        id: 'USR009',
        name: '郑十一',
        username: 'zhengshiyi',
        email: 'zhengshiyi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User9',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-07'
      },
      {
        id: 'USR010',
        name: '王十二',
        username: 'wangshier',
        email: 'wangshier@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User10',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-06'
      },
      {
        id: 'USR011',
        name: '李十三',
        username: 'lishisan',
        email: 'lishisan@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User11',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-05'
      },
      {
        id: 'USR012',
        name: '张十四',
        username: 'zhangshisi',
        email: 'zhangshisi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User12',
        role: 'employee',
        roleText: '员工',
        status: 'inactive',
        statusText: '禁用',
        createTime: '2024-01-04'
      },
      {
        id: 'USR013',
        name: '刘十五',
        username: 'liushiwu',
        email: 'liushiwu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User13',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-03'
      },
      {
        id: 'USR014',
        name: '陈十六',
        username: 'chenshiliu',
        email: 'chenshiliu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User14',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-02'
      },
      {
        id: 'USR015',
        name: '杨十七',
        username: 'yangshiqi',
        email: 'yangshiqi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User15',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2024-01-01'
      },
      {
        id: 'USR016',
        name: '黄十八',
        username: 'huangshiba',
        email: 'huangshiba@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User16',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-31'
      },
      {
        id: 'USR017',
        name: '赵十九',
        username: 'zhaoshijiu',
        email: 'zhaoshijiu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User17',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-30'
      },
      {
        id: 'USR018',
        name: '吴二十',
        username: 'wuershi',
        email: 'wuershi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User18',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-29'
      },
      {
        id: 'USR019',
        name: '徐二一',
        username: 'xuerer',
        email: 'xuerer@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User19',
        role: 'employee',
        roleText: '员工',
        status: 'inactive',
        statusText: '禁用',
        createTime: '2023-12-28'
      },
      {
        id: 'USR020',
        name: '孙二二',
        username: 'sunerr',
        email: 'sunerr@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User20',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-27'
      },
      {
        id: 'USR021',
        name: '马二三',
        username: 'maersan',
        email: 'maersan@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User21',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-26'
      },
      {
        id: 'USR022',
        name: '朱二四',
        username: 'zhuersi',
        email: 'zhuersi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User22',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-25'
      },
      {
        id: 'USR023',
        name: '胡二五',
        username: 'huerwu',
        email: 'huerwu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User23',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-24'
      },
      {
        id: 'USR024',
        name: '郭二六',
        username: 'guoerliu',
        email: 'guoerliu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User24',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-23'
      },
      {
        id: 'USR025',
        name: '何二七',
        username: 'heerqi',
        email: 'heerqi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User25',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-22'
      },
      {
        id: 'USR026',
        name: '高二八',
        username: 'gaoerba',
        email: 'gaoerba@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User26',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-21'
      },
      {
        id: 'USR027',
        name: '林二九',
        username: 'linerjiu',
        email: 'linerjiu@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User27',
        role: 'admin',
        roleText: '管理员',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-20'
      },
      {
        id: 'USR028',
        name: '罗三十',
        username: 'luosanshi',
        email: 'luosanshi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User28',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-19'
      },
      {
        id: 'USR029',
        name: '梁三一',
        username: 'liangsanyi',
        email: 'liangsanyi@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User29',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-18'
      },
      {
        id: 'USR030',
        name: '谢三二',
        username: 'xiesaner',
        email: 'xiesaner@company.com',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=User30',
        role: 'employee',
        roleText: '员工',
        status: 'active',
        statusText: '启用',
        createTime: '2023-12-17'
      }
    ])

    // 模态框状态
    const showModal = ref(false)
    const isEditing = ref(false)
    const formData = ref({
      id: '',
      name: '',
      username: '',
      email: '',
      role: '',
      status: '',
      avatar: ''
    })
    const formErrors = ref({
      id: '',
      name: '',
      username: '',
      email: '',
      role: '',
      status: ''
    })

    // 删除确认模态框状态
    const showDeleteModal = ref(false)
    const userToDelete = ref(null)

    // 返回首页
    const goHome = () => {
      router.push('/')
    }
    
    // 获取用户信息
    const getUserInfo = () => {
      const user = localStorage.getItem('user')
      if (user) {
        try {
          const parsedUser = JSON.parse(user)
          
          // 检查是否有保存的个人信息，如果有则合并
          const savedUserInfo = localStorage.getItem('savedUserInfo')
          if (savedUserInfo) {
            try {
              const parsedSavedInfo = JSON.parse(savedUserInfo)
              userInfo.value = {
                ...parsedUser,
                ...parsedSavedInfo, // 保存的个人信息优先级更高
                username: parsedSavedInfo.username || parsedUser.username || '', // 优先使用保存的用户名
                avatar: parsedSavedInfo.avatar || userInfo.value.avatar
              }
            } catch (error) {
              console.error('解析保存的用户信息失败:', error)
              userInfo.value = {
                ...parsedUser,
                username: parsedUser.username || '',
                avatar: userInfo.value.avatar
              }
            }
          } else {
            userInfo.value = {
              ...parsedUser,
              username: parsedUser.username || '',
              avatar: userInfo.value.avatar
            }
          }
        } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      }
    }

    // 打开添加用户模态框
    const showAddModal = () => {
      isEditing.value = false
      formData.value = {
        id: '',
        name: '',
        username: '',
        email: '',
        role: '',
        status: '',
        avatar: ''
      }
      formErrors.value = {
        id: '',
        name: '',
        username: '',
        email: '',
        role: '',
        status: ''
      }
      showModal.value = true
    }

    // 打开编辑用户模态框
    const showEditModal = (user) => {
      isEditing.value = true
      formData.value = { 
        ...user,
        roleText: undefined,
        statusText: undefined
      }
      formErrors.value = {
        id: '',
        name: '',
        username: '',
        email: '',
        role: '',
        status: ''
      }
      showModal.value = true
    }

    // 关闭模态框
    const closeModal = () => {
      showModal.value = false
    }

    // 表单验证
    const validateForm = () => {
      let isValid = true
      formErrors.value = {
        id: '',
        name: '',
        username: '',
        email: '',
        role: '',
        status: ''
      }

      if (!formData.value.id) {
        formErrors.value.id = '用户ID不能为空'
        isValid = false
      }
      if (!formData.value.name) {
        formErrors.value.name = '姓名不能为空'
        isValid = false
      }
      if (!formData.value.username) {
        formErrors.value.username = '用户名不能为空'
        isValid = false
      }
      if (!formData.value.email) {
        formErrors.value.email = '邮箱不能为空'
        isValid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.value.email)) {
        formErrors.value.email = '邮箱格式不正确'
        isValid = false
      }
      if (!formData.value.role) {
        formErrors.value.role = '角色不能为空'
        isValid = false
      }
      if (!formData.value.status) {
        formErrors.value.status = '状态不能为空'
        isValid = false
      }

      // 检查用户ID和用户名是否重复（仅在添加或编辑时ID发生变化时检查）
      if (!isEditing.value || formData.value.id !== users.value.find(u => u.id === formData.value.id)?.id) {
        const existingUser = users.value.find(user => user.id === formData.value.id)
        if (existingUser) {
          formErrors.value.id = '用户ID已存在'
          isValid = false
        }
      }

      const existingUsername = users.value.find(user => 
        user.username === formData.value.username && 
        (!isEditing.value || user.id !== formData.value.id)
      )
      if (existingUsername) {
        formErrors.value.username = '用户名已存在'
        isValid = false
      }

      return isValid
    }

    // 提交表单
    const submitForm = () => {
      if (validateForm()) {
        if (isEditing.value) {
          // 编辑用户
          const index = users.value.findIndex(user => user.id === formData.value.id)
          if (index !== -1) {
            const updatedUser = {
              ...formData.value,
              roleText: formData.value.role === 'admin' ? '管理员' : '员工',
              statusText: formData.value.status === 'active' ? '启用' : '禁用',
              avatar: formData.value.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${formData.value.id}`
            }
            users.value[index] = updatedUser
            closeModal()
            alert('用户信息已更新')
          }
        } else {
          // 添加用户
          const newUser = {
            ...formData.value,
            roleText: formData.value.role === 'admin' ? '管理员' : '员工',
            statusText: formData.value.status === 'active' ? '启用' : '禁用',
            avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${formData.value.id}`,
            createTime: new Date().toISOString().split('T')[0]
          }
          users.value.unshift(newUser)
          closeModal()
          alert('用户添加成功')
        }
      }
    }

    // 打开删除确认模态框
    const showDeleteConfirm = (user) => {
      userToDelete.value = user
      showDeleteModal.value = true
    }

    // 关闭删除确认模态框
    const closeDeleteModal = () => {
      showDeleteModal.value = false
      userToDelete.value = null
    }

    // 确认删除
    const confirmDelete = () => {
      if (userToDelete.value) {
        const index = users.value.findIndex(user => user.id === userToDelete.value.id)
        if (index !== -1) {
          users.value.splice(index, 1)
          closeDeleteModal()
          alert('用户已删除')
        } else {
          alert('未找到该用户，无法删除')
        }
      }
    }

    // 监听用户信息更新事件，实现实时同步
    const handleUserInfoUpdate = () => {
      getUserInfo() // 重新加载用户信息
    }

    onMounted(() => {
      getUserInfo()
      // 监听用户信息更新事件
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    onUnmounted(() => {
      // 清理事件监听器
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    return {
      userInfo,
      users,
      goHome,
      showModal,
      isEditing,
      formData,
      formErrors,
      showAddModal,
      showEditModal,
      closeModal,
      submitForm,
      validateForm,
      showDeleteConfirm,
      closeDeleteModal,
      confirmDelete,
      showDeleteModal,
      userToDelete
    }
  }
})
</script>

<style scoped>
.user-management-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
}

/* 页面头部样式 */
.page-header {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 150, 255, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  margin: -24px -24px 40px -24px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  color: #00a8ff;
  font-size: 24px;
}

.page-subtitle {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-right .user-info {
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-right .user-info span {
  color: #fff;
}

.user-avatar {
  background-color: #00a8ff;
  color: white;
}

.home-btn {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  font-weight: 600;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
  transition: color 0.3s ease;
  border-radius: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%);
  box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3);
}

.home-btn:hover {
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #4f46e5 100%);
  box-shadow: 0 6px 20px rgba(79, 70, 229, 0.5);
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* 用户列表 */
.user-list {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.btn-add {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.user-table {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow-x: auto;
}

.user-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.user-table th,
.user-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.user-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.user-table tr:hover {
  background: #f8fafc;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info span {
  font-weight: 500;
  color: #1e293b;
}

.role, .status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.role.admin {
  background: #fef3c7;
  color: #d97706;
}

.role.employee {
  background: #dbeafe;
  color: #1d4ed8;
}

.status.active {
  background: #dcfce7;
  color: #166534;
}

.status.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit {
  background: #dbeafe;
  color: #1d4ed8;
}

.btn-edit:hover {
  background: #bfdbfe;
}

.btn-delete {
  background: #fee2e2;
  color: #dc2626;
}

.btn-delete:hover {
  background: #fecaca;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 0;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0 24px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 0 24px 24px 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #374151;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group input:disabled {
  background-color: #f9fafb;
  color: #6b7280;
  cursor: not-allowed;
}

.form-error {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  margin-top: 24px;
}

.btn-submit,
.btn-cancel {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-submit {
  background: #3b82f6;
  color: white;
}

.btn-submit:hover {
  background: #2563eb;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.delete-modal .modal-content {
  max-width: 400px;
}

.delete-modal .modal-body {
  text-align: center;
  padding: 32px 24px;
}

.delete-modal .modal-footer {
  justify-content: center;
}

.btn-delete-confirm {
  background: #dc2626;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-delete-confirm:hover {
  background: #b91c1c;
}

.role-tag, .status-tag {
  border-radius: 20px;
  padding: 4px 12px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.edit-btn, .delete-btn {
  border-radius: 6px;
  height: 28px;
  font-size: 12px;
}

.edit-btn {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  border: none;
}

.delete-btn {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .stats-section .ant-col {
    margin-bottom: 16px;
  }
  
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

/* 用户头像样式 */
.user-avatar {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style> 