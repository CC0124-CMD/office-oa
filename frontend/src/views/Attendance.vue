<template>
  <div class="attendance-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="clock-circle" class="title-icon" />
            考勤管理
          </h1>
          <p class="page-subtitle">管理员工考勤记录、出勤统计和工时管理</p>
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
          <div class="stat-number">142</div>
          <div class="stat-label">今日出勤</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏰</div>
        <div class="stat-content">
          <div class="stat-number">3</div>
          <div class="stat-label">迟到人数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-number">5</div>
          <div class="stat-label">请假人数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💼</div>
        <div class="stat-content">
          <div class="stat-number">28</div>
          <div class="stat-label">加班小时</div>
        </div>
      </div>
    </div>

    <!-- 考勤列表 -->
    <div class="attendance-list">
      <div class="list-header">
        <h2>考勤记录</h2>
        <button class="btn-add" @click="showAddModal">+ 添加记录</button>
      </div>
      
      <div class="attendance-table">
        <table>
          <thead>
            <tr>
              <th>员工ID</th>
              <th>员工姓名</th>
              <th>日期</th>
              <th>上班时间</th>
              <th>下班时间</th>
              <th>工作时长</th>
              <th>状态</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in attendanceRecords" :key="record.id">
              <td>{{ record.id }}</td>
              <td>
                <div class="employee-info">
                  <img :src="record.employeeAvatar" :alt="record.employeeName" class="avatar">
                  <span>{{ record.employeeName }}</span>
                </div>
              </td>
              <td>{{ record.date }}</td>
              <td>{{ record.checkIn }}</td>
              <td>{{ record.checkOut }}</td>
              <td>{{ record.workHours }}小时</td>
              <td>
                <span class="status" :class="record.status">{{ record.statusText }}</span>
              </td>
              <td>{{ record.remark }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 添加考勤记录模态框 -->
    <div v-if="showAddAttendanceModal" class="modal-overlay" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加考勤记录</h3>
          <button class="modal-close" @click="closeAddModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitAttendance">
            <div class="form-group">
              <label>员工姓名 <span class="required">*</span></label>
              <select v-model="attendanceForm.employeeId" required>
                <option value="">请选择员工</option>
                <option v-for="employee in employeeList" :key="employee.id" :value="employee.id">
                  {{ employee.name }}
                </option>
              </select>
              <div v-if="formErrors.employeeId" class="form-error">{{ formErrors.employeeId }}</div>
            </div>
            
            <div class="form-group">
              <label>考勤日期 <span class="required">*</span></label>
              <input type="date" v-model="attendanceForm.date" required>
              <div v-if="formErrors.date" class="form-error">{{ formErrors.date }}</div>
            </div>

            <div class="form-group">
              <label>上班时间 <span class="required">*</span></label>
              <input type="time" v-model="attendanceForm.checkIn" required>
              <div v-if="formErrors.checkIn" class="form-error">{{ formErrors.checkIn }}</div>
            </div>

            <div class="form-group">
              <label>下班时间</label>
              <input type="time" v-model="attendanceForm.checkOut">
            </div>

            <div class="form-group">
              <label>考勤状态 <span class="required">*</span></label>
              <select v-model="attendanceForm.status" required>
                <option value="">请选择状态</option>
                <option value="normal">正常出勤</option>
                <option value="late">迟到</option>
                <option value="early">早退</option>
                <option value="absent">缺勤</option>
                <option value="leave">请假</option>
                <option value="overtime">加班</option>
              </select>
              <div v-if="formErrors.status" class="form-error">{{ formErrors.status }}</div>
            </div>

            <div class="form-group">
              <label>工作时长</label>
              <input type="number" v-model="attendanceForm.workHours" placeholder="请输入工作时长（小时）" step="0.5" min="0" max="24">
            </div>

            <div class="form-group">
              <label>备注</label>
              <textarea 
                v-model="attendanceForm.remark" 
                placeholder="请输入备注信息..."
                rows="3"
                maxlength="200"
              ></textarea>
              <div class="char-count">{{ attendanceForm.remark.length }}/200</div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeAddModal">取消</button>
          <button type="button" class="btn-submit" @click="submitAttendance" :disabled="submitting">
            <span v-if="submitting" class="loading-spinner"></span>
            {{ submitting ? '提交中...' : '添加记录' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'Attendance',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=AttendanceUser1'
    })
    
    // 模态框状态
    const showAddAttendanceModal = ref(false)
    const submitting = ref(false)

    // 表单数据
    const attendanceForm = ref({
      employeeId: '',
      date: '',
      checkIn: '',
      checkOut: '',
      status: '',
      workHours: '',
      remark: ''
    })

    // 表单错误
    const formErrors = ref({
      employeeId: '',
      date: '',
      checkIn: '',
      status: ''
    })

    // 员工列表
    const employeeList = ref([
      { id: 'EMP001', name: '张三' },
      { id: 'EMP002', name: '李四' },
      { id: 'EMP003', name: '王五' },
      { id: 'EMP004', name: '赵六' },
      { id: 'EMP005', name: '钱七' },
      { id: 'EMP006', name: '孙八' },
      { id: 'EMP007', name: '周九' },
      { id: 'EMP008', name: '吴十' },
      { id: 'EMP009', name: '郑十一' },
      { id: 'EMP010', name: '王十二' },
      { id: 'EMP011', name: '李十三' },
      { id: 'EMP012', name: '张十四' },
      { id: 'EMP013', name: '刘十五' },
      { id: 'EMP014', name: '陈十六' },
      { id: 'EMP015', name: '杨十七' },
      { id: 'EMP016', name: '黄十八' },
      { id: 'EMP017', name: '赵十九' },
      { id: 'EMP018', name: '吴二十' },
      { id: 'EMP019', name: '徐二一' },
      { id: 'EMP020', name: '孙二二' },
      { id: 'EMP021', name: '马二三' },
      { id: 'EMP022', name: '朱二四' },
      { id: 'EMP023', name: '胡二五' },
      { id: 'EMP024', name: '郭二六' },
      { id: 'EMP025', name: '何二七' },
      { id: 'EMP026', name: '高二八' },
      { id: 'EMP027', name: '林二九' },
      { id: 'EMP028', name: '罗三十' },
      { id: 'EMP029', name: '梁三一' },
      { id: 'EMP030', name: '谢三二' }
    ])

    // 考勤数据
    const attendanceRecords = ref([
      {
        id: 'AT001',
        employeeName: '张三',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance1',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT002',
        employeeName: '李四',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance2',
        date: '2024-01-15',
        checkIn: '09:15',
        checkOut: '18:00',
        workHours: 8.75,
        status: 'late',
        statusText: '迟到',
        remark: '迟到15分钟'
      },
      {
        id: 'AT003',
        employeeName: '王五',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance3',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '19:00',
        workHours: 10,
        status: 'overtime',
        statusText: '加班',
        remark: '加班1小时'
      },
      {
        id: 'AT004',
        employeeName: '赵六',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance4',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '19:30',
        workHours: 10,
        status: 'overtime',
        statusText: '加班',
        remark: '加班2小时'
      },
      {
        id: 'AT005',
        employeeName: '钱七',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance5',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT006',
        employeeName: '孙八',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance6',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT007',
        employeeName: '周九',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance7',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT008',
        employeeName: '吴十',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance8',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '17:30',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT009',
        employeeName: '郑十一',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance9',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '19:00',
        workHours: 9,
        status: 'overtime',
        statusText: '加班',
        remark: '加班1小时'
      },
      {
        id: 'AT010',
        employeeName: '王十二',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance10',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT011',
        employeeName: '李十三',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance11',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT012',
        employeeName: '张十四',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance12',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '17:30',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT013',
        employeeName: '刘十五',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance13',
        date: '2024-01-15',
        checkIn: '09:15',
        checkOut: '18:15',
        workHours: 8.75,
        status: 'late',
        statusText: '迟到',
        remark: '迟到15分钟'
      },
      {
        id: 'AT014',
        employeeName: '陈十六',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance14',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '19:45',
        workHours: 10,
        status: 'overtime',
        statusText: '加班',
        remark: '加班2小时'
      },
      {
        id: 'AT015',
        employeeName: '杨十七',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance15',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT016',
        employeeName: '黄十八',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance16',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '17:30',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT017',
        employeeName: '赵十九',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance17',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT018',
        employeeName: '吴二十',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance18',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT019',
        employeeName: '徐二一',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance19',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '19:00',
        workHours: 9,
        status: 'overtime',
        statusText: '加班',
        remark: '加班1小时'
      },
      {
        id: 'AT020',
        employeeName: '孙二二',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance20',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '17:30',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT021',
        employeeName: '马二三',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance21',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT022',
        employeeName: '朱二四',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance22',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT023',
        employeeName: '胡二五',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance23',
        date: '2024-01-15',
        checkIn: '09:15',
        checkOut: '18:15',
        workHours: 8.75,
        status: 'late',
        statusText: '迟到',
        remark: '迟到15分钟'
      },
      {
        id: 'AT024',
        employeeName: '郭二六',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance24',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '19:30',
        workHours: 10,
        status: 'overtime',
        statusText: '加班',
        remark: '加班2小时'
      },
      {
        id: 'AT025',
        employeeName: '何二七',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance25',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT026',
        employeeName: '高二八',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance26',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT027',
        employeeName: '林二九',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance27',
        date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '18:00',
        workHours: 9,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT028',
        employeeName: '罗三十',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance28',
        date: '2024-01-15',
        checkIn: '08:30',
        checkOut: '17:30',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      },
      {
        id: 'AT029',
        employeeName: '梁三一',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance29',
          date: '2024-01-15',
        checkIn: '09:00',
        checkOut: '19:00',
        workHours: 9,
        status: 'overtime',
        statusText: '加班',
        remark: '加班1小时'
      },
      {
        id: 'AT030',
        employeeName: '谢三二',
        employeeAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Attendance30',
        date: '2024-01-15',
        checkIn: '08:45',
        checkOut: '17:45',
        workHours: 8,
        status: 'normal',
        statusText: '正常',
        remark: '无'
      }
    ])

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

    // 显示添加考勤记录模态框
    const showAddModal = () => {
      showAddAttendanceModal.value = true
      attendanceForm.value = {
        employeeId: '',
        date: new Date().toISOString().split('T')[0], // 默认今天
        checkIn: '09:00',
        checkOut: '18:00',
        status: '',
        workHours: '',
        remark: ''
      }
      formErrors.value = {
        employeeId: '',
        date: '',
        checkIn: '',
        status: ''
      }
    }

    // 关闭添加考勤记录模态框
    const closeAddModal = () => {
      showAddAttendanceModal.value = false
      attendanceForm.value = {
        employeeId: '',
        date: '',
        checkIn: '',
        checkOut: '',
        status: '',
        workHours: '',
        remark: ''
      }
      formErrors.value = {
        employeeId: '',
        date: '',
        checkIn: '',
        status: ''
      }
    }

    // 提交考勤记录
    const submitAttendance = async () => {
      // 表单验证
      let hasError = false
      if (!attendanceForm.value.employeeId) {
        formErrors.value.employeeId = '请选择员工'
        hasError = true
      } else {
        formErrors.value.employeeId = ''
      }

      if (!attendanceForm.value.date) {
        formErrors.value.date = '请选择考勤日期'
        hasError = true
      } else {
        formErrors.value.date = ''
      }

      if (!attendanceForm.value.checkIn) {
        formErrors.value.checkIn = '请选择上班时间'
        hasError = true
      } else {
        formErrors.value.checkIn = ''
      }

      if (!attendanceForm.value.status) {
        formErrors.value.status = '请选择考勤状态'
        hasError = true
      } else {
        formErrors.value.status = ''
      }

      if (hasError) return

      submitting.value = true

      try {
        // 模拟提交过程
        await new Promise(resolve => setTimeout(resolve, 2000))

        // 获取员工信息
        const selectedEmployee = employeeList.value.find(emp => emp.id === attendanceForm.value.employeeId)
        if (!selectedEmployee) {
          alert('员工信息不存在')
          return
        }

        // 计算工作时长
        let workHours = 0
        if (attendanceForm.value.checkIn && attendanceForm.value.checkOut) {
          const checkInTime = new Date(`2000-01-01 ${attendanceForm.value.checkIn}`)
          const checkOutTime = new Date(`2000-01-01 ${attendanceForm.value.checkOut}`)
          workHours = Math.max(0, (checkOutTime - checkInTime) / (1000 * 60 * 60))
        } else if (attendanceForm.value.workHours) {
          workHours = parseFloat(attendanceForm.value.workHours)
        }

        // 获取状态文本
        const statusTextMap = {
          'normal': '正常',
          'late': '迟到',
          'early': '早退',
          'absent': '缺勤',
          'leave': '请假',
          'overtime': '加班'
        }

        // 创建新的考勤记录
        const newRecord = {
          id: `AT${String(attendanceRecords.value.length + 1).padStart(3, '0')}`,
          employeeName: selectedEmployee.name,
          employeeAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${selectedEmployee.id}`,
          date: attendanceForm.value.date,
          checkIn: attendanceForm.value.checkIn,
          checkOut: attendanceForm.value.checkOut || '',
          workHours: workHours.toFixed(1),
          status: attendanceForm.value.status,
          statusText: statusTextMap[attendanceForm.value.status] || '未知',
          remark: attendanceForm.value.remark || '无'
        }

        // 添加到考勤记录列表顶部
        attendanceRecords.value.unshift(newRecord)
        
        closeAddModal()
        alert('考勤记录添加成功！')
      } catch (error) {
        alert('添加失败，请重试')
        console.error('添加错误:', error)
      } finally {
        submitting.value = false
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
      attendanceRecords,
      goHome,
      showAddAttendanceModal,
      submitting,
      attendanceForm,
      formErrors,
      employeeList,
      showAddModal,
      closeAddModal,
      submitAttendance
    }
  }
})
</script>

<style scoped>
.attendance-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
}

/* 返回首页按钮 */
.back-btn {
  position: fixed !important;
  top: 24px !important;
  left: 24px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px 22px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4), 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 999999 !important;
  width: 130px;
  height: 48px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transform: translateZ(0) !important;
  will-change: transform !important;
  pointer-events: auto !important;
  isolation: isolate !important;
  contain: layout style paint !important;
}

.back-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.back-btn:hover::before {
  left: 100%;
}

.back-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.5), 0 8px 15px rgba(0, 0, 0, 0.15);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.back-btn:active {
  transform: translateY(-1px) scale(1.02);
}


/* 添加按钮样式 */
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-add::before {
  content: '📊';
  font-size: 18px;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
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

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
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

.attendance-list {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-filter, .btn-export {
  padding: 10px 20px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-filter:hover, .btn-export:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.attendance-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  padding: 16px 12px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
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
  width: 32px;
  height: 32px;
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

.status.normal {
  background: #d1fae5;
  color: #059669;
}

.status.late {
  background: #fef3c7;
  color: #d97706;
}

.status.overtime {
  background: #dbeafe;
  color: #1d4ed8;
}

.status.absent {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-view {
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

.btn-view {
  background: #f3f4f6;
  color: #374151;
}

.btn-view:hover {
  background: #e5e7eb;
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
  
  .header-actions {
    justify-content: center;
  }
  
  .attendance-table {
    font-size: 12px;
  }
  
  th, td {
    padding: 12px 8px;
  }
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
  max-width: 600px;
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
  border-radius: 50%;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 0 24px;
}

.modal-footer {
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.required {
  color: #dc2626;
}

.form-error {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
}

.btn-cancel,
.btn-submit {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-submit {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style> 
