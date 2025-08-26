<template>
  <div class="user-home-container">
    <CommonHeader 
      title="欢迎回来" 
      subtitle="开始您今天的工作" 
      iconType="home"
      :isUserSide="true"
    />
    
    <div class="user-home-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <!-- 欢迎区域 -->
        <div class="welcome-section">
          <div class="welcome-card">
            <div class="welcome-info">
              <h1>{{ getGreeting() }}，{{ userInfo.username || '用户' }}</h1>
              <p>{{ getCurrentDate() }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <a-icon type="clock-circle" />
                  <span>今日工作时长：{{ workStats.todayHours }}小时</span>
                </div>
                <div class="stat-item">
                  <a-icon type="check-circle" />
                  <span>已完成任务：{{ workStats.completedTasks }}个</span>
                </div>
              </div>
            </div>
            <div class="welcome-avatar">
              <a-avatar :size="80" :src="userInfo.avatar">
                <template #icon><UserOutlined /></template>
              </a-avatar>
            </div>
          </div>
        </div>

        <!-- 快速导航 -->
        <div class="quick-nav-section">
          <h2>快速导航</h2>
          <div class="nav-grid">
            <div class="nav-card" @click="navigateTo('/user/workbench')">
              <div class="nav-icon">📊</div>
              <h3>我的工作台</h3>
              <p>查看工作概览和快速操作</p>
            </div>
            <div class="nav-card" @click="navigateTo('/user/approval')">
              <div class="nav-icon">📋</div>
              <h3>我的审批</h3>
              <p>管理审批申请和查看状态</p>
            </div>
            <div class="nav-card" @click="navigateTo('/user/attendance')">
              <div class="nav-icon">⏰</div>
              <h3>我的考勤</h3>
              <p>查看考勤记录和打卡状态</p>
            </div>
            <div class="nav-card" @click="navigateTo('/user/files')">
              <div class="nav-icon">📁</div>
              <h3>我的文件</h3>
              <p>管理个人文件和文档</p>
            </div>
            <div class="nav-card" @click="navigateTo('/user/announcement')">
              <div class="nav-icon">📢</div>
              <h3>我的公告</h3>
              <p>查看重要公告信息</p>
            </div>
            <div class="nav-card" @click="navigateTo('/user/profile')">
              <div class="nav-icon">👤</div>
              <h3>个人信息</h3>
              <p>管理个人资料和设置</p>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions-section">
          <h2>快捷操作</h2>
          <div class="action-buttons">
            <button class="action-btn" @click="quickAction('checkin')">
              <a-icon type="clock-circle" />
              打卡签到
            </button>
            <button class="action-btn" @click="quickAction('approval')">
              <a-icon type="file-text" />
              申请审批
            </button>
            <button class="action-btn" @click="quickAction('upload')">
              <a-icon type="upload" />
              上传文件
            </button>
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="recent-activity-section">
          <h2>最近活动</h2>
          <div class="activity-list">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">{{ activity.icon }}</div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
              <div class="activity-status" :class="activity.status">
                {{ activity.statusText }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted, provide, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'
import { UserOutlined } from '@ant-design/icons-vue'

export default defineComponent({
  name: 'UserHome',
  components: {
    CommonHeader,
    CommonNavigation,
    UserOutlined
  },
  setup() {
    const router = useRouter()
    
    // 用户信息状态
    const headerUserInfo = ref({ username: '', avatar: '' })

    // 最近活动（持久化）
    const STORAGE_KEY = 'userActivities'
    const recentActivities = ref([])

    const loadActivities = () => {
      try {
        const raw = localStorage.getItem(STORAGE_KEY)
        const list = raw ? JSON.parse(raw) : []
        if (Array.isArray(list)) {
          recentActivities.value = list
        }
      } catch (e) {
        // ignore storage parse errors
      }
    }

    const persistActivities = () => {
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(recentActivities.value.slice(0, 20)))
      } catch (e) {
        // ignore storage write errors
      }
    }

    const addActivity = (activity) => {
      recentActivities.value.unshift(activity)
      if (recentActivities.value.length > 20) recentActivities.value.pop()
      persistActivities()
    }

    // 用户信息加载与事件（保持原有逻辑）
    const headerUserInfoValue = headerUserInfo
    const loadUserInfo = () => {
      try {
        const recent = localStorage.getItem('recentRegisteredUser')
        if (recent) {
          const parsedRecent = JSON.parse(recent)
          headerUserInfoValue.value = {
            username: parsedRecent.username || '',
            avatar: parsedRecent.avatar || ''
          }
          return
        }
        const saved = localStorage.getItem('savedUserInfo')
        if (saved) {
          const parsed = JSON.parse(saved)
          headerUserInfoValue.value = {
            username: parsed.username || '',
            avatar: parsed.avatar || ''
          }
          return
        }
        const user = localStorage.getItem('user')
        if (user) {
          const parsedUser = JSON.parse(user)
          headerUserInfoValue.value = {
            username: parsedUser.username || '',
            avatar: parsedUser.avatar || ''
          }
        }
      } catch (e) {
        // ignore parse errors
      }
    }

    const handleUserInfoUpdate = (e) => {
      const detail = e && e.detail ? e.detail : null
      if (detail) {
        headerUserInfoValue.value = {
          username: detail.username || headerUserInfoValue.value.username,
          avatar: detail.avatar || headerUserInfoValue.value.avatar
        }
      } else {
        loadUserInfo()
      }
    }

    const handleUserActivity = (e) => {
      const detail = e && e.detail ? e.detail : null
      if (!detail) return
      const id = Date.now()
      const activity = {
        id,
        type: detail.type || 'activity',
        icon: detail.type === 'attendance' ? 'clock-circle' : (detail.type === 'file' ? 'folder' : 'notification'),
        title: detail.title || '用户操作',
        time: detail.time || new Date().toTimeString().slice(0,5),
        status: detail.status || 'completed',
        statusText: detail.statusText || '已完成'
      }
      addActivity(activity)
    }

    onMounted(() => {
      loadUserInfo()
      loadActivities()
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
      window.addEventListener('userActivity', handleUserActivity)
      
      const timer = setInterval(() => {
        // keep component alive and could be used for periodic stats refresh later
        // no-op
      }, 60000)
      return () => clearInterval(timer)
    })

    onUnmounted(() => {
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
      window.removeEventListener('userActivity', handleUserActivity)
    })

    provide('userInfo', headerUserInfo)

    const workStats = reactive({ todayHours: 8.5, completedTasks: 12 })

    const getGreeting = () => {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      if (hour < 22) return '晚上好'
      return '夜深了'
    }

    const getCurrentDate = () => {
      const now = new Date()
      const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
      return now.toLocaleDateString('zh-CN', options)
    }

    const navigateTo = (path) => router.push(path)

    const quickAction = (action) => {
      switch (action) {
        case 'checkin':
          router.push('/user/attendance')
          break
        case 'approval':
          router.push('/user/approval')
          break
        case 'upload':
          router.push('/user/files')
          break
      }
    }

    return {
      userInfo: headerUserInfo,
      workStats,
      recentActivities,
      getGreeting,
      getCurrentDate,
      navigateTo,
      quickAction
    }
  }
})
</script>

<style scoped>
.user-home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.user-home-content {
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

/* 欢迎区域样式 */
.welcome-section {
  margin-bottom: 24px;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-info h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-info p {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #718096;
}

.user-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #4a5568;
}

.stat-item .anticon {
  color: #667eea;
}

.welcome-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 快速导航样式 */
.quick-nav-section h2,
.recent-activity-section h2,
.quick-actions-section h2 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 24px;
  font-weight: 600;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.nav-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.nav-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.nav-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
  font-size: 28px;
}

.nav-card h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
}

.nav-card p {
  margin: 0;
  font-size: 14px;
  color: #718096;
  line-height: 1.5;
}

/* 最近活动样式 */
.recent-activity-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f7fafc;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: #edf2f7;
  transform: translateX(4px);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.activity-icon.attendance {
  background: linear-gradient(135deg, #48bb78, #38a169);
}

.activity-icon.approval {
  background: linear-gradient(135deg, #ed8936, #dd6b20);
}

.activity-icon.file {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.activity-icon.announcement {
  background: linear-gradient(135deg, #f56565, #e53e3e);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 16px;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 14px;
  color: #718096;
}

.activity-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.activity-status.completed {
  background: #c6f6d5;
  color: #22543d;
}

.activity-status.pending {
  background: #fed7d7;
  color: #742a2a;
}

/* 快捷操作样式 */
.quick-actions-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.action-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.action-buttons .ant-btn {
  height: 48px;
  padding: 0 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-home-content {
    flex-direction: column;
    padding: 16px;
  }
  
  .welcome-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .welcome-info h1 {
    font-size: 24px;
  }
  
  .nav-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .ant-btn {
    width: 100%;
  }
}
</style> 