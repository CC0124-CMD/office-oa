<template>
  <div class="my-attendance-container">
    <!-- 公共头部 -->
    <CommonHeader 
      title="我的考勤"
      subtitle="查看和管理您的考勤记录"
      icon-type="clock-circle"
      :isUserSide="true"
    />
    
    <!-- 内容区域 -->
    <div class="attendance-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <!-- 考勤统计 -->
        <div class="stats-overview">
          <div class="stat-card">
            <div class="stat-icon">📅</div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.totalDays }}</div>
              <div class="stat-label">本月出勤天数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⏰</div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.avgWorkHours }}</div>
              <div class="stat-label">平均工作时长</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.onTimeCount }}</div>
              <div class="stat-label">准时打卡次数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⚠️</div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.lateCount }}</div>
              <div class="stat-label">迟到次数</div>
            </div>
          </div>
        </div>

        <!-- 考勤记录 -->
        <div class="attendance-list">
          <div class="list-header">
            <h2>考勤记录</h2>
            <button class="btn-checkin" @click="handleCheckin">打卡签到</button>
          </div>
          
          <div class="attendance-table">
            <table>
              <thead>
                <tr>
                  <th>日期</th>
                  <th>星期</th>
                  <th>上班时间</th>
                  <th>下班时间</th>
                  <th>工作时长</th>
                  <th>状态</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in attendanceRecords" :key="record.id">
                  <td>{{ record.date }}</td>
                  <td>{{ record.weekday }}</td>
                  <td>{{ record.checkInTime || '-' }}</td>
                  <td>{{ record.checkOutTime || '-' }}</td>
                  <td>{{ record.workHours || '-' }}</td>
                  <td>
                    <span class="status" :class="record.status">{{ record.statusText }}</span>
                  </td>
                  <td>{{ record.note || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑考勤记录模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑考勤记录' : '添加考勤记录' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitRecord">
            <div class="form-group">
              <label>日期 <span class="required">*</span></label>
              <input 
                type="date" 
                v-model="attendanceForm.date" 
                required
                :max="today"
              >
              <div v-if="formErrors.date" class="form-error">{{ formErrors.date }}</div>
            </div>
            
            <div class="form-group">
              <label>签到时间 <span class="required">*</span></label>
              <input 
                type="time" 
                v-model="attendanceForm.checkIn" 
                required
              >
              <div v-if="formErrors.checkIn" class="form-error">{{ formErrors.checkIn }}</div>
            </div>

            <div class="form-group">
              <label>签退时间</label>
              <input 
                type="time" 
                v-model="attendanceForm.checkOut"
              >
            </div>

            <div class="form-group">
              <label>备注</label>
              <textarea 
                v-model="attendanceForm.note" 
                placeholder="请输入备注信息..."
                rows="3"
              ></textarea>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeModal">取消</button>
          <button type="button" class="btn-submit" @click="submitRecord" :disabled="submitting">
            {{ submitting ? (isEditing ? '更新中...' : '提交中...') : (isEditing ? '更新记录' : '添加记录') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, computed, onMounted, provide } from 'vue'
import { message } from 'ant-design-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'
import { saveAttendanceRecords, getAttendanceRecords, saveUserActivity } from '@/utils/userDataManager'

export default defineComponent({
  name: 'MyAttendance',
  components: {
    CommonHeader,
    CommonNavigation
  },
  setup() {
    // 规则时间
    const START_TIME = '08:30'
    const END_TIME = '17:30'

    // 头部用户信息
    const headerUserInfo = reactive({
      username: '',
      avatar: ''
    })

    // 提供用户信息给公共头部组件
    provide('userInfo', headerUserInfo)

    // 模态框状态
    const showModal = ref(false)
    const isEditing = ref(false)
    const submitting = ref(false)
    const editingRecord = ref(null)

    // 表单数据
    const attendanceForm = reactive({
      date: '',
      checkIn: '',
      checkOut: '',
      note: ''
    })

    // 表单错误
    const formErrors = reactive({
      date: '',
      checkIn: ''
    })

    // 考勤统计数据
    const attendanceStats = reactive({
      totalDays: 0,
      avgWorkHours: '0小时',
      onTimeCount: 0,
      lateCount: 0
    })

    // 考勤记录数据
    const attendanceRecords = ref([
      {
        id: 'ATT001',
        date: '2024-01-15',
        weekday: '周一',
        checkInTime: '08:30',
        checkOutTime: '17:30',
        workHours: '9小时',
        status: 'normal',
        statusText: '正常出勤',
        note: ''
      },
      {
        id: 'ATT002',
        date: '2024-01-14',
        weekday: '周日',
        checkInTime: '08:45',
        checkOutTime: '17:30',
        workHours: '8.75小时',
        status: 'normal',
        statusText: '正常出勤',
        note: '提前到岗'
      },
      {
        id: 'ATT003',
        date: '2024-01-13',
        weekday: '周六',
        checkInTime: '09:15',
        checkOutTime: '',
        workHours: '',
        status: 'checked-in',
        statusText: '已签到',
        note: '忘记签退'
      }
    ])

    const saveRecords = () => {
      saveAttendanceRecords(attendanceRecords.value)
    }

    const loadRecords = () => {
      const records = getAttendanceRecords()
      if (records && records.length > 0) {
        attendanceRecords.value = records
      }
    }

    // 计算统计
    const totalDays = computed(() => attendanceRecords.value.length)
    const averageHours = computed(() => {
      const records = attendanceRecords.value.filter(r => r.workHours)
      if (records.length === 0) return '0小时'
      const totalHours = records.reduce((sum, r) => {
        const hours = parseFloat(r.workHours.replace('小时', ''))
        return sum + hours
      }, 0)
      return (totalHours / records.length).toFixed(1) + '小时'
    })
    const onTimeCount = computed(() => 
      attendanceRecords.value.filter(r => r.checkInTime && r.checkInTime <= START_TIME).length
    )
    const lateCount = computed(() => 
      attendanceRecords.value.filter(r => r.checkInTime && r.checkInTime > START_TIME).length
    )

    // 更新统计数据
    const updateAttendanceStats = () => {
      attendanceStats.totalDays = totalDays.value
      attendanceStats.avgWorkHours = averageHours.value
      attendanceStats.onTimeCount = onTimeCount.value
      attendanceStats.lateCount = lateCount.value
    }

    // 打卡签到/签退
    const pushActivity = (detail) => {
      saveUserActivity({
        type: detail.type || 'attendance',
        icon: 'clock-circle',
        title: detail.title || '考勤操作',
        time: detail.time || new Date().toTimeString().slice(0,5),
        status: detail.status || 'completed',
        statusText: detail.statusText || '已完成'
      })
    }

    const handleCheckin = () => {
      const now = new Date()
      const currentTime = now.toTimeString().slice(0, 5)
      const currentDate = now.toISOString().split('T')[0]
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const weekday = weekdays[now.getDay()]
      
      // 检查是否已经打卡
      const existingRecord = attendanceRecords.value.find(r => r.date === currentDate)
      if (existingRecord) {
        if (!existingRecord.checkOutTime) {
          // 签退
          existingRecord.checkOutTime = currentTime
          const checkIn = new Date(`2000-01-01 ${existingRecord.checkInTime}`)
          const checkOut = new Date(`2000-01-01 ${currentTime}`)
          const workHours = ((checkOut - checkIn) / (1000 * 60 * 60)).toFixed(1)
          existingRecord.workHours = `${workHours}小时`
          // 早退判定
          if (currentTime < END_TIME) {
            if (existingRecord.status === 'late') {
              existingRecord.status = 'late-early'
              existingRecord.statusText = '迟到+早退'
            } else {
              existingRecord.status = 'early-leave'
              existingRecord.statusText = '早退'
            }
          } else {
            // 若不是迟到，则记为正常出勤
            if (existingRecord.status !== 'late') {
              existingRecord.status = 'present'
              existingRecord.statusText = '正常出勤'
            }
          }
          message.success('签退成功！')
          // 记录活动
          pushActivity({
            type: 'attendance',
            title: '下午签退',
            time: currentTime,
            status: existingRecord.status === 'early-leave' ? 'early-leave' : (existingRecord.status === 'late-early' ? 'late-early' : 'completed'),
            statusText: existingRecord.statusText
          })
          saveRecords()
        } else {
          message.warning('今天已经完成打卡！')
        }
      } else {
        // 签到
        const newRecord = {
          id: `ATT${Date.now()}`,
          date: currentDate,
          weekday: weekday,
          checkInTime: currentTime,
          checkOutTime: '',
          workHours: '',
          status: 'present',
          statusText: '已签到',
          note: ''
        }
        // 迟到判定
        if (currentTime > START_TIME) {
          newRecord.status = 'late'
          newRecord.statusText = '迟到'
        }
        attendanceRecords.value.unshift(newRecord)
        message.success('签到成功！')
        // 记录活动
        pushActivity({
          type: 'attendance',
          title: '上午打卡签到',
          time: currentTime,
          status: newRecord.status === 'late' ? 'late' : 'completed',
          statusText: newRecord.status === 'late' ? '迟到' : '已完成'
        })
        saveRecords()
      }
      
      updateAttendanceStats()
    }

    // 今天日期
    const today = computed(() => {
      const date = new Date()
      return date.toISOString().split('T')[0]
    })

    // 加载用户信息
    const loadUserInfo = () => {
      try {
        const savedUserInfo = localStorage.getItem('savedUserInfo')
        if (savedUserInfo) {
          const parsedSavedInfo = JSON.parse(savedUserInfo)
          headerUserInfo.username = parsedSavedInfo.username || ''
          headerUserInfo.avatar = parsedSavedInfo.avatar || ''
        } else {
          const currentUser = localStorage.getItem('user')
          if (currentUser) {
            const parsedUser = JSON.parse(currentUser)
            headerUserInfo.username = parsedUser.username || localStorage.getItem('username') || 'user'
            headerUserInfo.avatar = parsedUser.avatar || ''
          }
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    // 显示添加记录模态框
    const showAddModal = () => {
      isEditing.value = false
      editingRecord.value = null
      attendanceForm.date = today.value
      attendanceForm.checkIn = '09:00'
      attendanceForm.checkOut = ''
      attendanceForm.note = ''
      formErrors.date = ''
      formErrors.checkIn = ''
      showModal.value = true
    }

    // 显示编辑记录模态框
    const showEditModal = (record) => {
      isEditing.value = true
      editingRecord.value = record
      attendanceForm.date = record.date
      attendanceForm.checkIn = record.checkInTime
      attendanceForm.checkOut = record.checkOutTime || ''
      attendanceForm.note = record.note || ''
      formErrors.date = ''
      formErrors.checkIn = ''
      showModal.value = true
    }

    // 关闭模态框
    const closeModal = () => {
      showModal.value = false
      isEditing.value = false
      editingRecord.value = null
    }

    // 表单验证
    const validateForm = () => {
      let isValid = true
      formErrors.date = ''
      formErrors.checkIn = ''

      if (!attendanceForm.date) {
        formErrors.date = '请选择日期'
        isValid = false
      }
      if (!attendanceForm.checkIn) {
        formErrors.checkIn = '请输入签到时间'
        isValid = false
      }

      return isValid
    }

    // 提交记录（新增或编辑）
    const submitRecord = async () => {
      if (!validateForm()) return

      submitting.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))

        if (isEditing.value && editingRecord.value) {
          // 编辑模式
          const record = editingRecord.value
          record.date = attendanceForm.date
          record.checkInTime = attendanceForm.checkIn
          record.checkOutTime = attendanceForm.checkOut
          record.note = attendanceForm.note

          // 重新计算状态与工时
          if (record.checkOutTime) {
            const checkInTime = new Date(`2000-01-01 ${record.checkInTime}`)
            const checkOutTime = new Date(`2000-01-01 ${record.checkOutTime}`)
            const diffHours = (checkOutTime - checkInTime) / (1000 * 60 * 60)
            record.workHours = diffHours.toFixed(1) + '小时'
          } else {
            record.workHours = ''
          }

          // 状态
          if (record.checkInTime > START_TIME) {
            record.status = 'late'
            record.statusText = '迟到'
          } else {
            record.status = 'present'
            record.statusText = record.checkOutTime ? '正常出勤' : '已签到'
          }
          if (record.checkOutTime && record.checkOutTime < END_TIME) {
            if (record.status === 'late') {
              record.status = 'late-early'
              record.statusText = '迟到+早退'
            } else {
              record.status = 'early-leave'
              record.statusText = '早退'
            }
          }

          saveRecords()
          message.success('考勤记录已更新！')
        } else {
          // 新建模式
          const w = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
          const weekdayName = w[new Date(attendanceForm.date).getDay()]
          const newRecord = {
            id: `ATT${String(Date.now())}`,
            date: attendanceForm.date,
            weekday: weekdayName,
            checkInTime: attendanceForm.checkIn,
            checkOutTime: attendanceForm.checkOut,
            workHours: '',
            status: 'present',
            statusText: attendanceForm.checkOut ? '正常出勤' : '已签到',
            note: attendanceForm.note
          }

          // 计算工作时长与状态
          if (newRecord.checkOutTime) {
            const ci = new Date(`2000-01-01 ${newRecord.checkInTime}`)
            const co = new Date(`2000-01-01 ${newRecord.checkOutTime}`)
            const diffHours = (co - ci) / (1000 * 60 * 60)
            newRecord.workHours = diffHours.toFixed(1) + '小时'
          }
          if (newRecord.checkInTime > START_TIME) {
            newRecord.status = 'late'
            newRecord.statusText = '迟到'
          }
          if (newRecord.checkOutTime && newRecord.checkOutTime < END_TIME) {
            if (newRecord.status === 'late') {
              newRecord.status = 'late-early'
              newRecord.statusText = '迟到+早退'
            } else {
              newRecord.status = 'early-leave'
              newRecord.statusText = '早退'
            }
          }

          attendanceRecords.value.unshift(newRecord)
          saveRecords()
          message.success('考勤记录已添加！')
        }

        closeModal()
      } catch (error) {
        message.error('操作失败，请重试')
        console.error('提交错误:', error)
      } finally {
        submitting.value = false
      }
    }

    // 签退按钮（存在的话）
    const checkOut = (record) => {
      const now = new Date()
      const currentTime = now.toTimeString().slice(0, 5)
      record.checkOutTime = currentTime

      // 计算工作时长
      const checkInTime = new Date(`2000-01-01 ${record.checkInTime}`)
      const checkOutTime = new Date(`2000-01-01 ${currentTime}`)
      const diffHours = (checkOutTime - checkInTime) / (1000 * 60 * 60)
      record.workHours = diffHours.toFixed(1) + '小时'
      record.statusText = '正常出勤'

      saveRecords()
      message.success('签退成功！')
    }

    // 删除记录
    const deleteRecord = (record) => {
      if (confirm('确定要删除这条考勤记录吗？')) {
        const index = attendanceRecords.value.findIndex(r => r.id === record.id)
        if (index !== -1) {
          attendanceRecords.value.splice(index, 1)
          saveRecords()
          message.success('考勤记录已删除')
        }
      }
    }

    onMounted(() => {
      loadUserInfo()
      loadRecords()
      updateAttendanceStats()
    })

    return {
      headerUserInfo,
      showModal,
      isEditing,
      submitting,
      editingRecord,
      attendanceForm,
      formErrors,
      attendanceStats,
      attendanceRecords,
      totalDays,
      averageHours,
      onTimeCount,
      lateCount,
      today,
      showAddModal,
      showEditModal,
      closeModal,
      submitRecord,
      handleCheckin,
      checkOut,
      deleteRecord
    }
  }
})
</script>

<style scoped>
.my-attendance-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.attendance-content {
  display: flex;
  padding: 20px;
  gap: 20px;
  margin-top: 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
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

/* 考勤列表 */
.attendance-list {
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
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
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

tr:hover {
  background: #f8fafc;
}

.status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status.present {
  background: #dcfce7;
  color: #166534;
}

.status.late {
  background: #fef3c7;
  color: #d97706;
}

.status.absent {
  background: #fee2e2;
  color: #dc2626;
}

.status.early-leave {
  background: #fde68a;
  color: #b45309;
}

/* 迟到 + 早退 */
.status.late-early {
  background: #fcd34d;
  color: #92400e;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-checkout, .btn-delete {
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

.btn-checkout {
  background: #dcfce7;
  color: #166534;
}

.btn-checkout:hover {
  background: #bbf7d0;
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
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.required {
  color: #dc2626;
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

.btn-submit:hover:not(:disabled) {
  background: #2563eb;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  
  .my-attendance-container {
    padding: 16px;
  }
  
  .attendance-list {
    padding: 16px;
  }
  
  th, td {
    padding: 12px 8px;
    font-size: 12px;
  }
}
</style> 