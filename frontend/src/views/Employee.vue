<template>
  <div class="employee-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="team" class="title-icon" />
            员工管理
          </h1>
          <p class="page-subtitle">管理公司员工信息、部门分配和权限设置</p>
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
    
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-number">156</div>
          <div class="stat-label">员工总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🆕</div>
        <div class="stat-content">
          <div class="stat-number">12</div>
          <div class="stat-label">本月新增</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-content">
          <div class="stat-number">8</div>
          <div class="stat-label">本月晋升</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏰</div>
        <div class="stat-content">
          <div class="stat-number">3.2</div>
          <div class="stat-label">平均工龄(年)</div>
        </div>
      </div>
    </div>
    
    <div class="employee-list">
      <div class="list-header">
        <h2>员工列表</h2>
        <button class="btn-add" @click="showAddModal">+ 添加员工</button>
      </div>
      
      <div class="employee-table">
        <table>
          <thead>
            <tr>
              <th>员工ID</th>
              <th>姓名</th>
              <th>部门</th>
              <th>职位</th>
              <th>入职日期</th>
              <th>工龄</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="employee in employees" :key="employee.id">
              <td>{{ employee.id }}</td>
              <td>
                <div class="employee-info">
                  <img :src="employee.avatar" :alt="employee.name" class="avatar">
                  <span>{{ employee.name }}</span>
                </div>
              </td>
              <td>{{ employee.department }}</td>
              <td>{{ employee.position }}</td>
              <td>{{ employee.joinDate }}</td>
              <td>{{ employee.workYears }}年</td>
              <td>
                <span class="status" :class="employee.status">{{ employee.statusText }}</span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-edit" @click="showEditModal(employee)">编辑</button>
                  <button class="btn-delete" @click="showDeleteConfirm(employee)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 添加/编辑员工模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑员工' : '添加员工' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm" class="employee-form">
            <div class="form-row">
              <div class="form-group">
                <label>员工姓名 *</label>
                <input 
                  v-model="formData.name" 
                  type="text" 
                  required 
                  placeholder="请输入员工姓名"
                  :class="{ 'error': formErrors.name }"
                >
                <span v-if="formErrors.name" class="error-text">{{ formErrors.name }}</span>
              </div>
              <div class="form-group">
                <label>员工ID *</label>
                <input 
                  v-model="formData.id" 
                  type="text" 
                  required 
                  placeholder="请输入员工ID"
                  :class="{ 'error': formErrors.id }"
                  :disabled="isEditing"
                >
                <span v-if="formErrors.id" class="error-text">{{ formErrors.id }}</span>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>部门 *</label>
                <select v-model="formData.department" required :class="{ 'error': formErrors.department }">
                  <option value="">请选择部门</option>
                  <option value="技术部">技术部</option>
                  <option value="人事部">人事部</option>
                  <option value="财务部">财务部</option>
                  <option value="市场部">市场部</option>
                  <option value="运营部">运营部</option>
                  <option value="客服部">客服部</option>
                </select>
                <span v-if="formErrors.department" class="error-text">{{ formErrors.department }}</span>
              </div>
              <div class="form-group">
                <label>职位 *</label>
                <input 
                  v-model="formData.position" 
                  type="text" 
                  required 
                  placeholder="请输入职位"
                  :class="{ 'error': formErrors.position }"
                >
                <span v-if="formErrors.position" class="error-text">{{ formErrors.position }}</span>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>入职日期 *</label>
                <input 
                  v-model="formData.joinDate" 
                  type="date" 
                  required 
                  :class="{ 'error': formErrors.joinDate }"
                >
                <span v-if="formErrors.joinDate" class="error-text">{{ formErrors.joinDate }}</span>
              </div>
              <div class="form-group">
                <label>状态 *</label>
                <select v-model="formData.status" required :class="{ 'error': formErrors.status }">
                  <option value="">请选择状态</option>
                  <option value="active">在职</option>
                  <option value="inactive">离职</option>
                  <option value="probation">试用期</option>
                  <option value="suspended">停职</option>
                </select>
                <span v-if="formErrors.status" class="error-text">{{ formErrors.status }}</span>
              </div>
            </div>
            
            <div class="form-group">
              <label>头像URL</label>
              <input 
                v-model="formData.avatar" 
                type="text" 
                placeholder="请输入头像URL（可选）"
              >
              <small>留空将使用默认头像</small>
            </div>
            
            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="closeModal">取消</button>
              <button type="submit" class="btn-submit">{{ isEditing ? '保存' : '添加' }}</button>
            </div>
          </form>
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
          <p>您确定要删除员工 <strong>{{ employeeToDelete?.name }}</strong> 吗？</p>
          <p class="warning-text">此操作不可撤销！</p>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeDeleteModal">取消</button>
            <button type="button" class="btn-delete-confirm" @click="confirmDelete">确认删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'Employee',
  setup() {
const router = useRouter()

    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee1' // 默认头像
    })
    
    // 员工数据
    const employees = ref([
      { id: 'EMP001', name: '张三', department: '技术部', position: '高级工程师', joinDate: '2020-03-15', workYears: 3.5, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee1' },
      { id: 'EMP002', name: '李四', department: '人事部', position: 'HR专员', joinDate: '2021-06-20', workYears: 2.2, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee2' },
      { id: 'EMP003', name: '王五', department: '财务部', position: '会计', joinDate: '2019-11-08', workYears: 4.1, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee3' },
      { id: 'EMP004', name: '赵六', department: '技术部', position: '前端开发', joinDate: '2022-01-10', workYears: 1.8, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee4' },
      { id: 'EMP005', name: '钱七', department: '市场部', position: '市场专员', joinDate: '2018-09-12', workYears: 5.2, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee5' },
      { id: 'EMP006', name: '孙八', department: '技术部', position: '后端开发', joinDate: '2021-03-25', workYears: 2.6, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee6' },
      { id: 'EMP007', name: '周九', department: '人事部', position: '人事经理', joinDate: '2017-12-01', workYears: 6.0, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee7' },
      { id: 'EMP008', name: '吴十', department: '财务部', position: '财务主管', joinDate: '2019-05-18', workYears: 4.4, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee8' },
      { id: 'EMP009', name: '郑十一', department: '技术部', position: 'UI设计师', joinDate: '2022-07-03', workYears: 1.3, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee9' },
      { id: 'EMP010', name: '王十二', department: '市场部', position: '市场经理', joinDate: '2016-08-22', workYears: 7.1, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee10' },
      { id: 'EMP011', name: '李十三', department: '技术部', position: '测试工程师', joinDate: '2021-09-14', workYears: 2.1, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee11' },
      { id: 'EMP012', name: '张十四', department: '人事部', position: '招聘专员', joinDate: '2020-12-05', workYears: 2.8, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee12' },
      { id: 'EMP013', name: '刘十五', department: '财务部', position: '出纳', joinDate: '2021-02-18', workYears: 2.6, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee13' },
      { id: 'EMP014', name: '陈十六', department: '技术部', position: '运维工程师', joinDate: '2019-07-30', workYears: 4.2, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee14' },
      { id: 'EMP015', name: '杨十七', department: '市场部', position: '品牌专员', joinDate: '2020-04-12', workYears: 3.4, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee15' },
      { id: 'EMP016', name: '黄十八', department: '技术部', position: '产品经理', joinDate: '2018-11-25', workYears: 5.0, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee16' },
      { id: 'EMP017', name: '赵十九', department: '人事部', position: '培训专员', joinDate: '2021-08-08', workYears: 2.0, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee17' },
      { id: 'EMP018', name: '吴二十', department: '财务部', position: '审计员', joinDate: '2020-06-15', workYears: 3.2, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee18' },
      { id: 'EMP019', name: '徐二一', department: '技术部', position: '算法工程师', joinDate: '2022-03-20', workYears: 1.5, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee19' },
      { id: 'EMP020', name: '孙二二', department: '市场部', position: '销售代表', joinDate: '2021-01-10', workYears: 2.7, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee20' },
      { id: 'EMP021', name: '马二三', department: '技术部', position: '数据分析师', joinDate: '2020-09-18', workYears: 3.0, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee21' },
      { id: 'EMP022', name: '朱二四', department: '人事部', position: '薪酬专员', joinDate: '2019-12-03', workYears: 3.8, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee22' },
      { id: 'EMP023', name: '胡二五', department: '财务部', position: '成本会计', joinDate: '2021-05-22', workYears: 2.3, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee23' },
      { id: 'EMP024', name: '郭二六', department: '技术部', position: '移动开发', joinDate: '2022-02-14', workYears: 1.6, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee24' },
      { id: 'EMP025', name: '何二七', department: '市场部', position: '公关专员', joinDate: '2020-07-28', workYears: 3.1, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee25' },
      { id: 'EMP026', name: '高二八', department: '技术部', position: '安全工程师', joinDate: '2019-04-16', workYears: 4.4, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee26' },
      { id: 'EMP027', name: '林二九', department: '人事部', position: '绩效专员', joinDate: '2021-10-09', workYears: 1.9, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee27' },
      { id: 'EMP028', name: '罗三十', department: '财务部', position: '税务专员', joinDate: '2020-01-25', workYears: 3.7, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee28' },
      { id: 'EMP029', name: '梁三一', department: '技术部', position: '架构师', joinDate: '2018-06-12', workYears: 5.2, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee29' },
      { id: 'EMP030', name: '谢三二', department: '市场部', position: '渠道经理', joinDate: '2019-03-08', workYears: 4.5, status: 'active', statusText: '在职', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Employee30' }
    ])

    // 模态框状态
    const showModal = ref(false)
    const isEditing = ref(false)
    const formData = ref({
      id: '',
      name: '',
  department: '',
      position: '',
      joinDate: '',
      status: '',
  avatar: ''
})
    const formErrors = ref({
      id: '',
      name: '',
      department: '',
      position: '',
      joinDate: '',
      status: ''
    })

    // 删除确认模态框状态
    const showDeleteModal = ref(false)
    const employeeToDelete = ref(null)

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
                avatar: parsedSavedInfo.avatar || userInfo.value.avatar // 合并头像
              }
            } catch (error) {
              console.error('解析保存的用户信息失败:', error)
              userInfo.value = {
                ...parsedUser,
                username: parsedUser.username || '',
                avatar: userInfo.value.avatar // 头像不合并
              }
            }
          } else {
            userInfo.value = {
              ...parsedUser,
              username: parsedUser.username || '',
              avatar: userInfo.value.avatar // 头像不合并
            }
          }
  } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      }
    }

    // 打开添加模态框
    const showAddModal = () => {
      isEditing.value = false
      formData.value = {
        id: '',
        name: '',
        department: '',
        position: '',
        joinDate: '',
        status: '',
        avatar: ''
      }
      formErrors.value = {
        id: '',
        name: '',
        department: '',
        position: '',
        joinDate: '',
        status: ''
      }
      showModal.value = true
    }

    // 打开编辑模态框
    const showEditModal = (employee) => {
      isEditing.value = true
      formData.value = { ...employee }
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
        department: '',
        position: '',
        joinDate: '',
        status: ''
      }

      if (!formData.value.id) {
        formErrors.value.id = '员工ID不能为空'
        isValid = false
      }
      if (!formData.value.name) {
        formErrors.value.name = '员工姓名不能为空'
        isValid = false
      }
      if (!formData.value.department) {
        formErrors.value.department = '部门不能为空'
        isValid = false
      }
      if (!formData.value.position) {
        formErrors.value.position = '职位不能为空'
        isValid = false
      }
      if (!formData.value.joinDate) {
        formErrors.value.joinDate = '入职日期不能为空'
        isValid = false
      }
      if (!formData.value.status) {
        formErrors.value.status = '状态不能为空'
        isValid = false
      }

      return isValid
}

// 提交表单
    const submitForm = () => {
      if (validateForm()) {
        if (isEditing.value) {
          // 编辑员工
          const index = employees.value.findIndex(emp => emp.id === formData.value.id)
          if (index !== -1) {
            employees.value[index] = { ...employees.value[index], ...formData.value }
            // 更新头像
            if (formData.value.avatar) {
              employees.value[index].avatar = formData.value.avatar
            } else {
              employees.value[index].avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + employees.value[index].id
            }
            closeModal()
            alert('员工信息已更新')
          } else {
            alert('未找到该员工，无法更新')
          }
        } else {
          // 添加员工
          const newId = 'EMP' + (Math.max(...employees.value.map(emp => parseInt(emp.id.substring(3)))) + 1).toString().padStart(3, '0')
          employees.value.push({
            id: newId,
            name: formData.value.name,
            department: formData.value.department,
            position: formData.value.position,
            joinDate: formData.value.joinDate,
            status: formData.value.status,
            statusText: formData.value.status, // 新增状态文本
            workYears: calculateWorkYears(formData.value.joinDate), // 计算工龄
            avatar: formData.value.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + newId
          })
          closeModal()
          alert('员工已添加')
        }
      }
    }

    // 计算工龄
    const calculateWorkYears = (joinDate) => {
      const join = new Date(joinDate)
      const today = new Date()
      let years = today.getFullYear() - join.getFullYear()
      const monthDiff = today.getMonth() - join.getMonth()
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < join.getDate())) {
        years--
      }
      return years
    }

    // 打开删除确认模态框
    const showDeleteConfirm = (employee) => {
      employeeToDelete.value = employee
      showDeleteModal.value = true
    }

    // 关闭删除确认模态框
    const closeDeleteModal = () => {
      showDeleteModal.value = false
      employeeToDelete.value = null
    }

    // 确认删除
    const confirmDelete = () => {
      if (employeeToDelete.value) {
        const index = employees.value.findIndex(emp => emp.id === employeeToDelete.value.id)
        if (index !== -1) {
          employees.value.splice(index, 1)
          closeDeleteModal()
          alert('员工已删除')
        } else {
          alert('未找到该员工，无法删除')
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
      employees,
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
      calculateWorkYears,
      showDeleteConfirm,
      closeDeleteModal,
      confirmDelete,
      employeeToDelete
    }
  }
})
</script>

<style scoped>
.employee-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
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

.user-info {
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info span {
  color: rgba(255, 255, 255, 0.9);
}

.user-avatar {
  background-color: #00a8ff; /* 默认头像背景色 */
  color: white; /* 默认头像文字颜色 */
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
  border-radius: 8px; /* 添加圆角 */
  padding: 8px 16px; /* 调整内边距 */
  background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); /* 蓝色发光背景 */
  box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3); /* 发光阴影 */
}

.home-btn:hover {
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #4f46e5 100%); /* 鼠标悬停时改变方向 */
  box-shadow: 0 6px 20px rgba(79, 70, 229, 0.5); /* 悬停时增强阴影 */
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.6);
  backdrop-filter: blur(20px);
  transition: all 0.3s ease;
  text-align: center;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  font-weight: 600;
}

.employee-list {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.btn-add {
  padding: 12px 24px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
}

.employee-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

th {
  background: #f8fafc;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status.active {
  background: #dcfce7;
  color: #166534;
}

.status.inactive {
  background: #fef2f2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
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
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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

@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .list-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .btn-add {
    width: 100%;
  }
  
  .employee-table {
    font-size: 12px;
  }
  
  th, td {
    padding: 12px 8px;
  }
}
</style> 
