<template>
  <div class="dashboard-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">🏢</div>
          <h2>Smart OA</h2>
          <p>管理后台</p>
        </div>
      </div>
      
      <nav class="sidebar-nav">
        <div class="nav-item active">
          <span class="nav-icon">📊</span>
          <span class="nav-text">数据概览</span>
        </div>
        <div class="nav-item" @click="navigateTo('/employee')">
          <span class="nav-icon">👥</span>
          <span class="nav-text">员工管理</span>
        </div>
        <div class="nav-item" @click="navigateTo('/approval')">
          <span class="nav-icon">✅</span>
          <span class="nav-text">审批管理</span>
        </div>
        <div class="nav-item" @click="navigateTo('/attendance')">
          <span class="nav-icon">⏰</span>
          <span class="nav-text">考勤管理</span>
        </div>
        <div class="nav-item" @click="navigateTo('/announcement')">
          <span class="nav-icon">📢</span>
          <span class="nav-text">公告管理</span>
        </div>
        <div class="nav-item" @click="navigateTo('/file-manager')">
          <span class="nav-icon">📁</span>
          <span class="nav-text">文件管理</span>
        </div>
        <div class="nav-item" @click="navigateTo('/user-management')">
          <span class="nav-icon">👤</span>
          <span class="nav-text">用户管理</span>
        </div>
      </nav>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <div class="header-left">
          <h1>数据概览</h1>
        </div>
        <div class="header-right">
          <div class="search-box">
            <input type="text" placeholder="搜索..." />
            <span class="search-icon">🔍</span>
          </div>
          <div class="user-menu">
            <a-dropdown>
              <a class="user-dropdown-link">
                <a-avatar :size="32" class="user-avatar">
                  <a-icon type="user" />
                </a-avatar>
                <span class="username">{{ userInfo.username || 'admin' }}</span>
                <a-icon type="down" />
              </a>
              <template #overlay>
                <a-menu class="user-dropdown-menu">
                  <a-menu-item key="profile" @click="goToProfile">
                    <a-icon type="setting" />
                    <span>个人信息</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <a-icon type="logout" />
                    <span>退出登录</span>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
      </header>

      <!-- 主要内容 -->
      <div class="content">
        <!-- 统计卡片 -->
        <div class="stats-section">
          <div class="stat-card stat-card-1">
            <div class="stat-icon">🏢</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.employees }}</div>
              <div class="stat-label">员工总数</div>
            </div>
            <div class="stat-trend">+12%</div>
          </div>
          
          <div class="stat-card stat-card-2">
            <div class="stat-icon">📋</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.approvals }}</div>
              <div class="stat-label">待审批</div>
            </div>
            <div class="stat-trend">+5%</div>
          </div>
          
          <div class="stat-card stat-card-3">
            <div class="stat-icon">⏰</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.attendance }}</div>
              <div class="stat-label">出勤率</div>
              <div class="stat-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: stats.attendance + '%' }"></div>
                </div>
                <span class="progress-text">{{ stats.attendance }}%</span>
              </div>
            </div>
          </div>
          
          <div class="stat-card stat-card-4">
            <div class="stat-icon">📊</div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.files }}</div>
              <div class="stat-label">文件总数</div>
              <div class="stat-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: (stats.files / 1000) * 100 + '%' }"></div>
                </div>
                <span class="progress-text">{{ Math.round((stats.files / 1000) * 100) }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
          <div class="chart-card">
            <div class="chart-header">
              <h3>部门分布</h3>
              <span class="chart-subtitle">员工部门统计</span>
            </div>
            <div class="chart-content">
              <div class="pie-chart">
                <div class="pie-segment" style="--start: 0deg; --end: 120deg; --color: #ff6b6b;"></div>
                <div class="pie-segment" style="--start: 120deg; --end: 240deg; --color: #4ecdc4;"></div>
                <div class="pie-segment" style="--start: 240deg; --end: 360deg; --color: #45b7d1;"></div>
                <div class="pie-center">
                  <span class="pie-total">{{ stats.employees }}</span>
                  <span class="pie-label">总人数</span>
                </div>
              </div>
              <div class="chart-legend">
                <div class="legend-item">
                  <span class="legend-color" style="background: #ff6b6b;"></span>
                  <span>技术部 40%</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background: #4ecdc4;"></span>
                  <span>人事部 33%</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background: #45b7d1;"></span>
                  <span>财务部 27%</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chart-card">
            <div class="chart-header">
              <h3>月度趋势</h3>
              <span class="chart-subtitle">近6个月数据</span>
            </div>
            <div class="chart-content">
              <div class="bar-chart">
                <div class="bar-item" v-for="(item, index) in monthlyData" :key="index">
                  <div class="bar" :style="{ height: (item.value / 100) * 100 + '%' }"></div>
                  <span class="bar-label">{{ item.month }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部图表 -->
        <div class="bottom-charts">
          <div class="chart-card">
            <div class="chart-header">
              <h3>系统性能分析</h3>
              <span class="chart-subtitle">各项指标评分</span>
            </div>
            <div class="chart-content">
              <div class="radar-chart">
                <div class="radar-grid">
                  <div class="radar-axis" v-for="i in 6" :key="i" :style="{ transform: `rotate(${i * 60}deg)` }"></div>
                </div>
                <div class="radar-polygon">
                  <div class="radar-point" style="--angle: 0deg; --radius: 80%;" data-label="响应速度"></div>
                  <div class="radar-point" style="--angle: 60deg; --radius: 90%;" data-label="稳定性"></div>
                  <div class="radar-point" style="--angle: 120deg; --radius: 75%;" data-label="安全性"></div>
                  <div class="radar-point" style="--angle: 180deg; --radius: 85%;" data-label="易用性"></div>
                  <div class="radar-point" style="--angle: 240deg; --radius: 95%;" data-label="功能性"></div>
                  <div class="radar-point" style="--angle: 300deg; --radius: 70%;" data-label="扩展性"></div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chart-card">
            <div class="chart-header">
              <h3>访问趋势</h3>
              <span class="chart-subtitle">近30天访问量</span>
            </div>
            <div class="chart-content">
              <div class="line-chart">
                <svg class="chart-svg" viewBox="0 0 300 150">
                  <path class="line-path" d="M0,120 L50,100 L100,80 L150,60 L200,90 L250,70 L300,50" />
                  <path class="line-path line-path-2" d="M0,130 L50,110 L100,90 L150,70 L200,100 L250,80 L300,60" />
                </svg>
                <div class="line-legend">
                  <div class="legend-item">
                    <span class="legend-color" style="background: #667eea;"></span>
                    <span>页面访问</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background: #764ba2;"></span>
                    <span>API调用</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

export default defineComponent({
  name: 'Home',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      role: ''
    })
    
    // 统计数据
    const stats = ref({
      employees: 156,
      approvals: 23,
      attendance: 94,
      files: 1247
    })

    // 月度数据
    const monthlyData = ref([
      { month: '1月', value: 85 },
      { month: '2月', value: 92 },
      { month: '3月', value: 78 },
      { month: '4月', value: 95 },
      { month: '5月', value: 88 },
      { month: '6月', value: 96 }
    ])

    // 获取用户信息
    const getUserInfo = () => {
      const user = localStorage.getItem('user')
      if (user) {
        try {
          userInfo.value = JSON.parse(user)
        } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      }
    }
    
    // 导航到指定页面
    const navigateTo = (path) => {
      router.push(path)
    }

    // 导航到个人信息页面
    const goToProfile = () => {
      router.push('/profile')
    }
    
    // 退出登录
    const handleLogout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      message.success('已退出登录')
      router.push('/login')
    }
    
    // 检查登录状态
    const checkAuth = () => {
      const token = localStorage.getItem('token')
      if (!token) {
        message.warning('请先登录')
        router.push('/login')
        return false
      }
      return true
    }
    
    onMounted(() => {
      if (checkAuth()) {
        getUserInfo()
      }
    })
    
    return {
      userInfo,
      stats,
      monthlyData,
      navigateTo,
      handleLogout,
      goToProfile
    }
  }
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 左侧导航栏 */
.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 0 20px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.logo {
  text-align: center;
}

.logo-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.logo h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
}

.logo p {
  margin: 0;
  font-size: 12px;
  opacity: 0.8;
}

.sidebar-nav {
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 5px 15px;
  border-radius: 10px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  background: #ff6b6b;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.nav-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.nav-text {
  font-size: 14px;
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
}

/* 顶部导航栏 */
.top-header {
  background: white;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e8ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #2d3748;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box input {
  width: 250px;
  padding: 10px 40px 10px 15px;
  border: 1px solid #e2e8f0;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
}

.search-box input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  right: 15px;
  color: #a0aec0;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 15px;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #4a5568;
}

.user-dropdown-link:hover {
  background: #f7fafc;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.username {
  font-weight: 500;
  font-size: 14px;
}

/* 内容区域 */
.content {
  padding: 30px;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card-1 {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
  color: white;
}

.stat-card-2 {
  background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
  color: white;
}

.stat-card-3 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-card-4 {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #2d3748;
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 15px;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 15px;
}

.stat-trend {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.stat-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
  min-width: 35px;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
}

.chart-subtitle {
  font-size: 12px;
  color: #718096;
}

/* 饼图 */
.pie-chart {
  position: relative;
  width: 150px;
  height: 150px;
  margin: 0 auto 20px;
}

.pie-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    var(--color) var(--start),
    var(--color) var(--end),
    transparent var(--end)
  );
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: white;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.pie-total {
  font-size: 16px;
  font-weight: 700;
  color: #2d3748;
  line-height: 1;
}

.pie-label {
  font-size: 10px;
  color: #718096;
  margin-top: 2px;
}

.chart-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #4a5568;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

/* 柱状图 */
.bar-chart {
  display: flex;
  align-items: end;
  justify-content: space-around;
  height: 120px;
  padding: 20px 0;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 30px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: all 0.3s ease;
}

.bar:hover {
  transform: scale(1.1);
}

.bar-label {
  font-size: 12px;
  color: #718096;
}

/* 底部图表 */
.bottom-charts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* 雷达图 */
.radar-chart {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto;
}

.radar-grid {
  position: absolute;
  width: 100%;
  height: 100%;
}

.radar-axis {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1px;
  height: 100px;
  background: #e2e8f0;
  transform-origin: 50% 0;
}

.radar-polygon {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
}

.radar-point {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transform-origin: center;
}

.radar-point::before {
  content: attr(data-label);
  position: absolute;
  top: -25px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #718096;
  white-space: nowrap;
}

/* 折线图 */
.line-chart {
  position: relative;
  height: 150px;
}

.chart-svg {
  width: 100%;
  height: 100%;
}

.line-path {
  fill: none;
  stroke: #667eea;
  stroke-width: 3;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.line-path-2 {
  stroke: #764ba2;
}

.line-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-section,
  .bottom-charts {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .content {
    padding: 20px;
  }
  
  .top-header {
    padding: 15px 20px;
  }
  
  .search-box input {
    width: 200px;
  }
}
</style> 