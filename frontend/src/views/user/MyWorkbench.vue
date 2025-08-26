<template>
  <div class="workbench-container">
    <CommonHeader 
      title="我的工作台" 
      subtitle="个人工作概览与快速操作" 
      iconType="dashboard"
      :isUserSide="true"
    />
    
    <div class="workbench-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <!-- 工作概览统计 -->
        <div class="stats-section">
          <div class="stat-card">
            <div class="stat-icon">
              <a-icon type="clock-circle" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ workStats.todayHours }}h</div>
              <div class="stat-label">今日工作时长</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <a-icon type="check-circle" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ workStats.completedTasks }}</div>
              <div class="stat-label">已完成任务</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <a-icon type="file-text" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ workStats.pendingApprovals }}</div>
              <div class="stat-label">待审批事项</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <a-icon type="notification" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ workStats.unreadAnnouncements }}</div>
              <div class="stat-label">未读公告</div>
            </div>
          </div>
        </div>

        <!-- 快速操作区域 -->
        <div class="quick-actions">
          <h3>快速操作</h3>
          <div class="action-grid">
            <div class="action-item" @click="quickAction('attendance')">
              <a-icon type="clock-circle" />
              <span>打卡签到</span>
            </div>
            <div class="action-item" @click="quickAction('approval')">
              <a-icon type="file-text" />
              <span>申请审批</span>
            </div>
            <div class="action-item" @click="quickAction('file')">
              <a-icon type="upload" />
              <span>上传文件</span>
            </div>
            <div class="action-item" @click="quickAction('announcement')">
              <a-icon type="notification" />
              <span>查看公告</span>
            </div>
          </div>
        </div>

        <!-- 最近工作记录 -->
        <div class="recent-work">
          <h3>最近工作记录</h3>
          <div class="work-list">
            <div v-for="record in recentWorkRecords" :key="record.id" class="work-item">
              <div class="work-icon">
                <a-icon :type="record.icon" />
              </div>
              <div class="work-info">
                <div class="work-title">{{ record.title }}</div>
                <div class="work-time">{{ record.time }}</div>
              </div>
              <div class="work-status" :class="record.status">
                {{ record.statusText }}
              </div>
            </div>
          </div>
        </div>

        <!-- 待办事项 -->
        <div class="todo-section">
          <h3>待办事项</h3>
          <div class="todo-list">
            <div v-for="todo in todoList" :key="todo.id" class="todo-item">
              <a-checkbox 
                :checked="todo.completed" 
                @change="toggleTodo(todo.id)"
              />
              <span class="todo-text" :class="{ completed: todo.completed }">
                {{ todo.text }}
              </span>
              <span class="todo-priority" :class="todo.priority">
                {{ todo.priorityText }}
              </span>
            </div>
          </div>
          <div class="add-todo">
            <a-input 
              v-model:value="newTodoText" 
              placeholder="添加新的待办事项..."
              @pressEnter="addTodo"
            >
              <template #suffix>
                <a-button type="primary" size="small" @click="addTodo">
                  添加
                </a-button>
              </template>
            </a-input>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'

export default defineComponent({
  name: 'MyWorkbench',
  components: {
    CommonHeader,
    CommonNavigation
  },
  setup() {
    const router = useRouter()
    const userInfo = inject('userInfo', { username: '', avatar: '' })

    // 工作统计数据
    const workStats = reactive({
      todayHours: 8.5,
      completedTasks: 12,
      pendingApprovals: 3,
      unreadAnnouncements: 5
    })

    // 最近工作记录
    const recentWorkRecords = ref([
      {
        id: 1,
        icon: 'clock-circle',
        title: '上午打卡签到',
        time: '09:00',
        status: 'completed',
        statusText: '已完成'
      },
      {
        id: 2,
        icon: 'file-text',
        title: '提交请假申请',
        time: '10:30',
        status: 'pending',
        statusText: '审批中'
      },
      {
        id: 3,
        icon: 'upload',
        title: '上传项目文档',
        time: '14:15',
        status: 'completed',
        statusText: '已完成'
      },
      {
        id: 4,
        icon: 'notification',
        title: '查看部门公告',
        time: '16:00',
        status: 'completed',
        statusText: '已读'
      }
    ])

    // 待办事项
    const todoList = ref([
      {
        id: 1,
        text: '完成周报总结',
        completed: false,
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 2,
        text: '准备明日会议材料',
        completed: false,
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 3,
        text: '回复客户邮件',
        completed: true,
        priority: 'low',
        priorityText: '低'
      }
    ])

    const newTodoText = ref('')

    // 快速操作
    const quickAction = (action) => {
      switch (action) {
        case 'attendance':
          router.push('/user/attendance')
          break
        case 'approval':
          router.push('/user/approval')
          break
        case 'file':
          router.push('/user/files')
          break
        case 'announcement':
          router.push('/user/announcement')
          break
      }
    }

    // 切换待办事项状态
    const toggleTodo = (id) => {
      const todo = todoList.value.find(item => item.id === id)
      if (todo) {
        todo.completed = !todo.completed
      }
    }

    // 添加待办事项
    const addTodo = () => {
      if (newTodoText.value.trim()) {
        const newTodo = {
          id: Date.now(),
          text: newTodoText.value.trim(),
          completed: false,
          priority: 'medium',
          priorityText: '中'
        }
        todoList.value.unshift(newTodo)
        newTodoText.value = ''
      }
    }

    // 模拟数据更新
    const updateWorkStats = () => {
      workStats.todayHours = Math.max(0, workStats.todayHours + (Math.random() - 0.5) * 0.2)
      workStats.completedTasks = Math.max(0, workStats.completedTasks + Math.floor((Math.random() - 0.5) * 2))
      workStats.pendingApprovals = Math.max(0, workStats.pendingApprovals + Math.floor((Math.random() - 0.5) * 2))
      workStats.unreadAnnouncements = Math.max(0, workStats.unreadAnnouncements + Math.floor((Math.random() - 0.5) * 2))
    }

    onMounted(() => {
      // 每30秒更新一次统计数据
      const timer = setInterval(updateWorkStats, 30000)
      return () => clearInterval(timer)
    })

    return {
      userInfo,
      workStats,
      recentWorkRecords,
      todoList,
      newTodoText,
      quickAction,
      toggleTodo,
      addTodo
    }
  }
})
</script>

<style scoped>
.workbench-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.workbench-content {
  display: flex;
  padding: 20px;
  gap: 20px;
  margin-top: 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 统计卡片样式 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
}

/* 快速操作样式 */
.quick-actions {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.quick-actions h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 20px;
  font-weight: 600;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f7fafc, #edf2f7);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  transform: translateY(-2px);
  background: linear-gradient(135deg, #e6fffa, #b2f5ea);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.action-item .anticon {
  font-size: 32px;
  color: #667eea;
}

.action-item span {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

/* 最近工作记录样式 */
.recent-work {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.recent-work h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 20px;
  font-weight: 600;
}

.work-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.work-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f7fafc;
  transition: all 0.3s ease;
}

.work-item:hover {
  background: #edf2f7;
  transform: translateX(4px);
}

.work-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.work-info {
  flex: 1;
}

.work-title {
  font-size: 16px;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 4px;
}

.work-time {
  font-size: 14px;
  color: #718096;
}

.work-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.work-status.completed {
  background: #c6f6d5;
  color: #22543d;
}

.work-status.pending {
  background: #fed7d7;
  color: #742a2a;
}

/* 待办事项样式 */
.todo-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.todo-section h3 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 20px;
  font-weight: 600;
}

.todo-list {
  margin-bottom: 20px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #e2e8f0;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-text {
  flex: 1;
  font-size: 16px;
  color: #2d3748;
}

.todo-text.completed {
  text-decoration: line-through;
  color: #a0aec0;
}

.todo-priority {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.todo-priority.high {
  background: #fed7d7;
  color: #742a2a;
}

.todo-priority.medium {
  background: #fef5e7;
  color: #744210;
}

.todo-priority.low {
  background: #c6f6d5;
  color: #22543d;
}

.add-todo {
  display: flex;
  gap: 12px;
}

.add-todo .ant-input {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .workbench-content {
    flex-direction: column;
    padding: 16px;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>