<template>
  <div class="system-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="dashboard" class="title-icon" />
            系统监控
          </h1>
          <p class="page-subtitle">实时监控系统运行状态、性能指标和资源使用情况</p>
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

    <!-- 系统状态概览 -->
    <div class="system-overview">
      <div class="status-card">
        <div class="status-icon">🟢</div>
        <div class="status-content">
          <div class="status-title">系统状态</div>
          <div class="status-value">正常运行</div>
        </div>
      </div>
      <div class="status-card">
        <div class="status-icon">⚡</div>
        <div class="status-content">
          <div class="status-title">CPU使用率</div>
          <div class="status-value">{{ systemStats.cpuUsage }}%</div>
        </div>
      </div>
      <div class="status-card">
        <div class="status-icon">💾</div>
        <div class="status-content">
          <div class="status-title">内存使用率</div>
          <div class="status-value">{{ systemStats.memoryUsage }}%</div>
        </div>
      </div>
      <div class="status-card">
        <div class="status-icon">💿</div>
        <div class="status-content">
          <div class="status-title">磁盘使用率</div>
          <div class="status-value">{{ systemStats.diskUsage }}%</div>
        </div>
      </div>
    </div>

    <!-- 实时监控数据 -->
    <div class="monitoring-data">
      <div class="data-section">
        <h2>实时监控</h2>
        <div class="data-grid">
          <div class="data-item">
            <div class="data-label">在线用户</div>
            <div class="data-value">{{ systemStats.onlineUsers }}</div>
          </div>
          <div class="data-item">
            <div class="data-label">活跃会话</div>
            <div class="data-value">{{ systemStats.activeSessions }}</div>
          </div>
          <div class="data-item">
            <div class="data-label">请求响应时间</div>
            <div class="data-value">{{ systemStats.responseTime }}ms</div>
          </div>
          <div class="data-item">
            <div class="data-label">错误率</div>
            <div class="data-value">{{ systemStats.errorRate.toFixed(2) }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统日志 -->
    <div class="system-logs">
      <div class="logs-header">
        <h2>系统日志</h2>
        <button class="btn-refresh" @click="refreshLogs">刷新</button>
      </div>
      
      <div class="logs-table">
        <table>
          <thead>
            <tr>
              <th>时间</th>
              <th>级别</th>
              <th>模块</th>
              <th>消息</th>
              <th>用户</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in systemLogs" :key="log.id">
              <td>{{ log.timestamp }}</td>
              <td>
                <span class="log-level" :class="log.level">{{ log.levelText }}</span>
              </td>
              <td>{{ log.module }}</td>
              <td>{{ log.message }}</td>
              <td>
                <div class="user-info">
                  <img :src="log.userAvatar" :alt="log.user" class="avatar">
                  <span>{{ log.user }}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'System',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemUser1'
    })

    // 系统监控数据
    const systemStats = ref({
      cpuUsage: 45,
      memoryUsage: 62,
      diskUsage: 78,
      onlineUsers: 142,
      activeSessions: 89,
      responseTime: 125,
      errorRate: 0.02
    })
    
    // 系统日志数据
    const systemLogs = ref([
      {
        id: 'LOG001',
        timestamp: '2024-01-15 10:30:00',
        level: 'info',
        levelText: '信息',
        module: '用户管理',
        message: '用户登录成功',
        user: '张三',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog1'
      },
      {
        id: 'LOG002',
        timestamp: '2024-01-15 10:25:00',
        level: 'warning',
        levelText: '警告',
        module: '系统监控',
        message: '内存使用率超过80%',
        user: '李四',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog2'
      },
      {
        id: 'LOG003',
        timestamp: '2024-01-15 10:20:00',
        level: 'error',
        levelText: '错误',
        module: '文件管理',
        message: '文件上传失败',
        user: '王五',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog3'
      },
      {
        id: 'LOG004',
        timestamp: '2024-01-15 10:15:00',
        level: 'info',
        levelText: '信息',
        module: '考勤管理',
        message: '员工打卡记录同步完成',
        user: '赵六',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog4'
      },
      {
        id: 'LOG005',
        timestamp: '2024-01-15 10:10:00',
        level: 'info',
        levelText: '信息',
        module: '审批流程',
        message: '请假申请审批通过',
        user: '钱七',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog5'
      },
      {
        id: 'LOG006',
        timestamp: '2024-01-15 10:05:00',
        level: 'warning',
        levelText: '警告',
        module: '数据库',
        message: '数据库连接池使用率较高',
        user: '孙八',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog6'
      },
      {
        id: 'LOG007',
        timestamp: '2024-01-15 10:00:00',
        level: 'info',
        levelText: '信息',
        module: '公告管理',
        message: '新公告发布成功',
        user: '周九',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog7'
      },
      {
        id: 'LOG008',
        timestamp: '2024-01-15 09:55:00',
        level: 'error',
        levelText: '错误',
        module: '邮件服务',
        message: '邮件发送失败',
        user: '吴十',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog8'
      },
      {
        id: 'LOG009',
        timestamp: '2024-01-15 09:50:00',
        level: 'info',
        levelText: '信息',
        module: '权限管理',
        message: '角色权限更新完成',
        user: '郑十一',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog9'
      },
      {
        id: 'LOG010',
        timestamp: '2024-01-15 09:45:00',
        level: 'info',
        levelText: '信息',
        module: '系统备份',
        message: '系统数据备份完成',
        user: '王十二',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog10'
      },
      {
        id: 'LOG011',
        timestamp: '2024-01-15 09:40:00',
        level: 'warning',
        levelText: '警告',
        module: '网络监控',
        message: '网络延迟异常',
        user: '李十三',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog11'
      },
      {
        id: 'LOG012',
        timestamp: '2024-01-15 09:35:00',
        level: 'info',
        levelText: '信息',
        module: '用户管理',
        message: '新用户注册成功',
        user: '张十四',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog12'
      },
      {
        id: 'LOG013',
        timestamp: '2024-01-15 09:30:00',
        level: 'info',
        levelText: '信息',
        module: '文件管理',
        message: '文件下载完成',
        user: '刘十五',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog13'
      },
      {
        id: 'LOG014',
        timestamp: '2024-01-15 09:25:00',
        level: 'error',
        levelText: '错误',
        module: '审批流程',
        message: '审批流程异常终止',
        user: '陈十六',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog14'
      },
      {
        id: 'LOG015',
        timestamp: '2024-01-15 09:20:00',
        level: 'info',
        levelText: '信息',
        module: '考勤管理',
        message: '考勤数据统计完成',
        user: '杨十七',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog15'
      },
      {
        id: 'LOG016',
        timestamp: '2024-01-15 09:15:00',
        level: 'warning',
        levelText: '警告',
        module: '系统监控',
        message: 'CPU使用率异常波动',
        user: '黄十八',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog16'
      },
      {
        id: 'LOG017',
        timestamp: '2024-01-15 09:10:00',
        level: 'info',
        levelText: '信息',
        module: '公告管理',
        message: '公告阅读统计更新',
        user: '赵十九',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog17'
      },
      {
        id: 'LOG018',
        timestamp: '2024-01-15 09:05:00',
        level: 'info',
        levelText: '信息',
        module: '权限管理',
        message: '用户权限验证通过',
        user: '吴二十',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog18'
      },
      {
        id: 'LOG019',
        timestamp: '2024-01-15 09:00:00',
        level: 'error',
        levelText: '错误',
        module: '数据库',
        message: '数据库查询超时',
        user: '徐二一',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog19'
      },
      {
        id: 'LOG020',
        timestamp: '2024-01-15 08:55:00',
        level: 'info',
        levelText: '信息',
        module: '系统维护',
        message: '系统维护任务完成',
        user: '孙二二',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog20'
      },
      {
        id: 'LOG021',
        timestamp: '2024-01-15 08:50:00',
        level: 'info',
        levelText: '信息',
        module: '用户管理',
        message: '用户信息更新成功',
        user: '马二三',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog21'
      },
      {
        id: 'LOG022',
        timestamp: '2024-01-15 08:45:00',
        level: 'warning',
        levelText: '警告',
        module: '网络监控',
        message: '网络流量异常',
        user: '朱二四',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog22'
      },
      {
        id: 'LOG023',
        timestamp: '2024-01-15 08:40:00',
        level: 'info',
        levelText: '信息',
        module: '文件管理',
        message: '文件压缩完成',
        user: '胡二五',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog23'
      },
      {
        id: 'LOG024',
        timestamp: '2024-01-15 08:35:00',
        level: 'info',
        levelText: '信息',
        module: '审批流程',
        message: '审批流程启动成功',
        user: '郭二六',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog24'
      },
      {
        id: 'LOG025',
        timestamp: '2024-01-15 08:30:00',
        level: 'error',
        levelText: '错误',
        module: '邮件服务',
        message: '邮件模板加载失败',
        user: '何二七',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog25'
      },
      {
        id: 'LOG026',
        timestamp: '2024-01-15 08:25:00',
        level: 'info',
        levelText: '信息',
        module: '考勤管理',
        message: '考勤规则更新完成',
        user: '高二八',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog26'
      },
      {
        id: 'LOG027',
        timestamp: '2024-01-15 08:20:00',
        level: 'info',
        levelText: '信息',
        module: '系统监控',
        message: '系统性能检测完成',
        user: '林二九',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog27'
      },
      {
        id: 'LOG028',
        timestamp: '2024-01-15 08:15:00',
        level: 'warning',
        levelText: '警告',
        module: '数据库',
        message: '数据库索引重建建议',
        user: '罗三十',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog28'
      },
      {
        id: 'LOG029',
        timestamp: '2024-01-15 08:10:00',
        level: 'info',
        levelText: '信息',
        module: '公告管理',
        message: '公告分类更新完成',
        user: '梁三一',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog29'
      },
      {
        id: 'LOG030',
        timestamp: '2024-01-15 08:05:00',
        level: 'info',
        levelText: '信息',
        module: '权限管理',
        message: '权限组配置完成',
        user: '谢三二',
        userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog30'
      }
    ])

    // 返回首页
    const goHome = () => {
      router.push('/')
    }

    // 更新系统监控数据（添加浮动效果）
    const updateSystemStats = () => {
      // CPU使用率：35-65%之间浮动
      systemStats.value.cpuUsage = Math.floor(Math.random() * 31) + 35
      
      // 内存使用率：50-80%之间浮动
      systemStats.value.memoryUsage = Math.floor(Math.random() * 31) + 50
      
      // 磁盘使用率：70-85%之间浮动
      systemStats.value.diskUsage = Math.floor(Math.random() * 16) + 70
      
      // 在线用户：120-180之间浮动
      systemStats.value.onlineUsers = Math.floor(Math.random() * 61) + 120
      
      // 活跃会话：70-110之间浮动
      systemStats.value.activeSessions = Math.floor(Math.random() * 41) + 70
      
      // 响应时间：80-200ms之间浮动
      systemStats.value.responseTime = Math.floor(Math.random() * 121) + 80
      
      // 错误率：0.00-0.10%之间浮动
      systemStats.value.errorRate = parseFloat((Math.random() * 0.1).toFixed(2))
    }

    // 启动定时器，每3秒更新一次数据
    let updateTimer = null
    const startDataUpdate = () => {
      updateTimer = setInterval(updateSystemStats, 3000)
    }

    // 停止数据更新
    const stopDataUpdate = () => {
      if (updateTimer) {
        clearInterval(updateTimer)
        updateTimer = null
      }
    }
    
    // 刷新系统日志
    const refreshLogs = () => {
      const modules = ['用户管理', '系统监控', '文件管理', '考勤管理', '审批流程', '公告管理', '权限管理', '数据库', '邮件服务', '网络监控', '系统维护']
      const levels = ['info', 'warning', 'error']
      const levelTexts = ['信息', '警告', '错误']
      const users = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑十一', '王十二', '李十三', '张十四', '刘十五', '陈十六', '杨十七', '黄十八', '赵十九', '吴二十', '徐二一', '孙二二', '马二三', '朱二四', '胡二五', '郭二六', '何二七', '高二八', '林二九', '罗三十', '梁三一', '谢三二']
      const messages = [
        '用户登录成功', '内存使用率超过80%', '文件上传失败', '员工打卡记录同步完成', '请假申请审批通过',
        '数据库连接池使用率较高', '新公告发布成功', '邮件发送失败', '角色权限更新完成', '系统数据备份完成',
        '网络延迟异常', '新用户注册成功', '文件下载完成', '审批流程异常终止', '考勤数据统计完成',
        'CPU使用率异常波动', '公告阅读统计更新', '用户权限验证通过', '数据库查询超时', '系统维护任务完成',
        '用户信息更新成功', '网络流量异常', '文件压缩完成', '审批流程启动成功', '邮件模板加载失败',
        '考勤规则更新完成', '系统性能检测完成', '数据库索引重建建议', '公告分类更新完成', '权限组配置完成'
      ]
      
      // 生成新的30条随机日志
      const newLogs = []
      const now = new Date()
      
      for (let i = 0; i < 30; i++) {
        const randomModule = modules[Math.floor(Math.random() * modules.length)]
        const randomLevelIndex = Math.floor(Math.random() * levels.length)
        const randomUser = users[Math.floor(Math.random() * users.length)]
        const randomMessage = messages[Math.floor(Math.random() * messages.length)]
        
        // 生成随机时间（最近24小时内）
        const randomTime = new Date(now.getTime() - Math.random() * 24 * 60 * 60 * 1000)
        const timestamp = randomTime.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
        
        newLogs.push({
          id: `LOG${String(i + 1).padStart(3, '0')}`,
          timestamp: timestamp,
          level: levels[randomLevelIndex],
          levelText: levelTexts[randomLevelIndex],
          module: randomModule,
          message: randomMessage,
          user: randomUser,
          userAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=SystemLog${i + 1}`
        })
      }
      
      // 按时间排序（最新的在前）
      newLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
      
      // 更新日志数据
      systemLogs.value = newLogs
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

    // 监听用户信息更新事件，实现实时同步
    const handleUserInfoUpdate = () => {
      getUserInfo() // 重新加载用户信息
    }

    onMounted(() => {
      getUserInfo()
      startDataUpdate() // 启动数据浮动
      // 监听用户信息更新事件
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    // 组件销毁时清理定时器
    onUnmounted(() => {
      stopDataUpdate()
      // 清理事件监听器
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    return {
      userInfo,
      systemStats,
      systemLogs,
      goHome,
      refreshLogs
    }
  }
})
</script>

<style scoped>
.system-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
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

/* 系统状态概览 */
.system-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.status-card {
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

.status-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.status-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 500;
}

.status-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

/* 实时监控数据 */
.monitoring-data {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.data-section h2 {
  margin: 0 0 24px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.data-item {
  text-align: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.data-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 500;
}

.data-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
}

/* 系统日志 */
.system-logs {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.logs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.logs-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.btn-refresh {
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-refresh:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.logs-table {
  overflow-x: auto;
}

.logs-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.logs-table th,
.logs-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.logs-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.log-level {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.log-level.info {
  background: #dbeafe;
  color: #1d4ed8;
}

.log-level.warning {
  background: #fef3c7;
  color: #d97706;
}

.log-level.error {
  background: #fee2e2;
  color: #dc2626;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info .avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-download, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-download {
  background: #dbeafe;
  color: #1d4ed8;
}

.btn-download:hover {
  background: #bfdbfe;
}

.btn-delete {
  background: #fee2e2;
  color: #dc2626;
}

.btn-delete:hover {
  background: #fecaca;
}

@media (max-width: 768px) {
  .system-overview {
    grid-template-columns: 1fr;
  }
  
  .data-grid {
    grid-template-columns: 1fr;
  }
  
  .logs-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .logs-table {
    font-size: 12px;
  }
  
  .logs-table th, .logs-table td {
    padding: 12px 8px;
  }
}
</style>