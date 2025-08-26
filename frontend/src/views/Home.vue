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
         <div class="nav-item" :class="{ active: activeModule === 'overview' }" @click="switchModule('overview')">
           <span class="nav-icon">📊</span>
           <span class="nav-text">数据概览</span>
         </div>
         <div class="nav-item" :class="{ active: activeModule === 'employee' }" @click="switchModule('employee')">
           <span class="nav-icon">👥</span>
           <span class="nav-text">员工管理</span>
         </div>
         <div class="nav-item" :class="{ active: activeModule === 'approval' }" @click="switchModule('approval')">
           <span class="nav-icon">✅</span>
           <span class="nav-text">审批管理</span>
         </div>
         <div class="nav-item" :class="{ active: activeModule === 'attendance' }" @click="switchModule('attendance')">
           <span class="nav-icon">⏰</span>
           <span class="nav-text">考勤管理</span>
         </div>
         <div class="nav-item" :class="{ active: activeModule === 'announcement' }" @click="switchModule('announcement')">
           <span class="nav-icon">📢</span>
           <span class="nav-text">公告管理</span>
         </div>
         <div class="nav-item" :class="{ active: activeModule === 'file' }" @click="switchModule('file')">
           <span class="nav-icon">📁</span>
           <span class="nav-text">文件管理</span>
         </div>
                   <div class="nav-item" :class="{ active: activeModule === 'user' }" @click="switchModule('user')">
            <span class="nav-icon">👤</span>
            <span class="nav-text">用户管理</span>
          </div>
          <div class="nav-item" :class="{ active: activeModule === 'system' }" @click="switchModule('system')">
            <span class="nav-icon">🔧</span>
            <span class="nav-text">系统监控</span>
          </div>
       </nav>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-header">
                 <div class="header-left">
           <h1>{{ getModuleTitle() }}</h1>
         </div>
        <div class="header-right">
          <div class="search-box">
            <input type="text" placeholder="搜索..." />
            <span class="search-icon">🔍</span>
          </div>
          <div class="user-menu">
            <a-dropdown>
              <a class="user-dropdown-link">
                <a-avatar :size="32" class="user-avatar" :src="userInfo.avatar">
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
         <!-- 数据概览模块 -->
         <div v-if="activeModule === 'overview'" class="overview-content">
                                   <!-- 卡片式轮播图 -->
            <div class="card-carousel-section">
              <div class="card-carousel">
                <div class="card-carousel-container" :style="{ transform: `translateX(-${currentCardSlide * 25}%)` }">
                                     <!-- 员工总数卡片 -->
                   <div class="stat-card-item employee-card" @click="switchModule('employee')">
                     <div class="card-trend-badge">+12%</div>
                     <div class="card-icon-large">👥</div>
                     <div class="card-number">{{ stats.employees }}</div>
                     <div class="card-label">员工总数</div>
                   </div>

                   <!-- 待审批卡片 -->
                   <div class="stat-card-item approval-card" @click="switchModule('approval')">
                     <div class="card-trend-badge">+5%</div>
                     <div class="card-icon-large">📋</div>
                     <div class="card-number">{{ stats.approvals }}</div>
                     <div class="card-label">待审批</div>
                   </div>

                   <!-- 出勤率卡片 -->
                   <div class="stat-card-item attendance-card" @click="switchModule('attendance')">
                     <div class="card-icon-large">⏰</div>
                     <div class="card-number">{{ stats.attendance }}%</div>
                     <div class="card-label">出勤率</div>
                     <div class="card-progress">
                       <div class="progress-bar-modern">
                         <div class="progress-fill-modern" :style="{ width: stats.attendance + '%' }"></div>
                       </div>
                     </div>
                   </div>

                   <!-- 文件总数卡片 -->
                   <div class="stat-card-item file-card" @click="switchModule('file')">
                     <div class="card-icon-large">📊</div>
                     <div class="card-number">{{ stats.files }}</div>
                     <div class="card-label">文件总数</div>
                     <div class="card-progress">
                       <div class="progress-bar-modern">
                         <div class="progress-fill-modern" :style="{ width: '78%' }"></div>
                       </div>
                     </div>
                   </div>

                   <!-- 公告管理卡片 -->
                   <div class="stat-card-item announcement-card" @click="switchModule('announcement')">
                     <div class="card-trend-badge">+8%</div>
                     <div class="card-icon-large">📢</div>
                     <div class="card-number">{{ stats.announcements }}</div>
                     <div class="card-label">公告总数</div>
                   </div>

                   <!-- 系统监控卡片 -->
                   <div class="stat-card-item system-card" @click="switchModule('system')">
                     <div class="card-icon-large">🔧</div>
                     <div class="card-number">{{ stats.systemUptime }}%</div>
                     <div class="card-label">系统可用性</div>
                     <div class="progress-bar-modern">
                       <div class="progress-fill-modern" :style="{ width: stats.systemUptime + '%' }"></div>
                     </div>
                   </div>
                </div>

                <!-- 轮播图控制按钮 -->
                <div class="carousel-nav">
                  <button class="nav-btn prev-btn" @click="prevCardSlide">
                    <span>‹</span>
                  </button>
                  <button class="nav-btn next-btn" @click="nextCardSlide">
                    <span>›</span>
                  </button>
                </div>

                <!-- 轮播图指示器 -->
                <div class="carousel-dots">
                  <div 
                    v-for="(dot, index) in 3" 
                    :key="index"
                    class="carousel-dot" 
                    :class="{ active: currentCardSlide === index }"
                    @click="goToCardSlide(index)"
                  ></div>
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
                 <canvas ref="pieCanvas" class="chart-canvas"></canvas>
                 <div class="chart-legend">
                   <div class="legend-item" v-for="(d, i) in departmentData" :key="i">
                     <span class="legend-color" :style="{ background: pieColors[i % pieColors.length] }"></span>
                     <span>{{ d.name }} {{ d.value }}%</span>
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
                 <canvas ref="barCanvas" class="chart-canvas"></canvas>
               </div>
             </div>
           </div>
         </div>

         <!-- 员工管理模块 -->
         <div v-if="activeModule === 'employee'" class="module-content">
           <div class="module-header">
             <h2>员工管理</h2>
             <p>管理公司员工信息、部门分配和权限设置</p>
           </div>
           <div class="module-grid">
             <div class="module-card">
               <div class="card-icon">👥</div>
               <h3>员工列表</h3>
               <p>查看和管理所有员工信息</p>
               <div class="card-actions">
                 <button class="btn-primary">查看员工</button>
                 <button class="btn-secondary">添加员工</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">🏢</div>
               <h3>部门管理</h3>
               <p>组织架构和部门设置</p>
               <div class="card-actions">
                 <button class="btn-primary">部门列表</button>
                 <button class="btn-secondary">新建部门</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">🔐</div>
               <h3>权限设置</h3>
               <p>员工角色和权限管理</p>
               <div class="card-actions">
                 <button class="btn-primary">角色管理</button>
                 <button class="btn-secondary">权限配置</button>
               </div>
             </div>
           </div>
         </div>

         <!-- 审批管理模块 -->
         <div v-if="activeModule === 'approval'" class="module-content">
           <div class="module-header">
             <h2>审批管理</h2>
             <p>处理各类审批申请和工作流程</p>
           </div>
           <div class="module-grid">
             <div class="module-card">
               <div class="card-icon">📋</div>
               <h3>待审批</h3>
               <p>需要您审批的申请列表</p>
               <div class="card-actions">
                 <button class="btn-primary">查看待审批</button>
                 <button class="btn-secondary">批量处理</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">✅</div>
               <h3>已审批</h3>
               <p>历史审批记录和状态</p>
               <div class="card-actions">
                 <button class="btn-primary">审批历史</button>
                 <button class="btn-secondary">导出记录</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">⚙️</div>
               <h3>流程配置</h3>
               <p>审批流程和规则设置</p>
               <div class="card-actions">
                 <button class="btn-primary">流程设计</button>
                 <button class="btn-secondary">规则配置</button>
               </div>
             </div>
           </div>
         </div>

         <!-- 考勤管理模块 -->
         <div v-if="activeModule === 'attendance'" class="module-content">
           <div class="module-header">
             <h2>考勤管理</h2>
             <p>员工考勤记录和统计管理</p>
           </div>
           <div class="module-grid">
             <div class="module-card">
               <div class="card-icon">⏰</div>
               <h3>考勤记录</h3>
               <p>员工上下班打卡记录</p>
               <div class="card-actions">
                 <button class="btn-primary">查看记录</button>
                 <button class="btn-secondary">导出数据</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">📊</div>
               <h3>考勤统计</h3>
               <p>出勤率和工作时长统计</p>
               <div class="card-actions">
                 <button class="btn-primary">统计报表</button>
                 <button class="btn-secondary">数据分析</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">📅</div>
               <h3>请假管理</h3>
               <p>员工请假申请和审批</p>
               <div class="card-actions">
                 <button class="btn-primary">请假申请</button>
                 <button class="btn-secondary">审批记录</button>
               </div>
             </div>
           </div>
         </div>

         <!-- 公告管理模块 -->
         <div v-if="activeModule === 'announcement'" class="module-content">
           <div class="module-header">
             <h2>公告管理</h2>
             <p>发布和管理公司公告信息</p>
           </div>
           <div class="module-grid">
             <div class="module-card">
               <div class="card-icon">📢</div>
               <h3>发布公告</h3>
               <p>创建和发布新的公告</p>
               <div class="card-actions">
                 <button class="btn-primary">新建公告</button>
                 <button class="btn-secondary">草稿箱</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">📋</div>
               <h3>公告列表</h3>
               <p>管理已发布的公告</p>
               <div class="card-actions">
                 <button class="btn-primary">查看公告</button>
                 <button class="btn-secondary">编辑公告</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">📊</div>
               <h3>阅读统计</h3>
               <p>公告阅读情况统计</p>
               <div class="card-actions">
                 <button class="btn-primary">查看统计</button>
                 <button class="btn-secondary">导出报告</button>
               </div>
             </div>
           </div>
         </div>

         <!-- 文件管理模块 -->
         <div v-if="activeModule === 'file'" class="module-content">
           <div class="module-header">
             <h2>文件管理</h2>
             <p>公司文件存储、共享和权限管理</p>
           </div>
           <div class="module-grid">
             <div class="module-card">
               <div class="card-icon">📁</div>
               <h3>文件存储</h3>
               <p>上传和管理公司文件</p>
               <div class="card-actions">
                 <button class="btn-primary">上传文件</button>
                 <button class="btn-secondary">文件列表</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">🔗</div>
               <h3>文件共享</h3>
               <p>设置文件共享和权限</p>
               <div class="card-actions">
                 <button class="btn-primary">共享设置</button>
                 <button class="btn-secondary">权限管理</button>
               </div>
             </div>
             <div class="module-card">
               <div class="card-icon">📊</div>
               <h3>存储统计</h3>
               <p>文件存储空间使用情况</p>
               <div class="card-actions">
                 <button class="btn-primary">查看统计</button>
                 <button class="btn-secondary">清理空间</button>
               </div>
             </div>
           </div>
         </div>

                   <!-- 用户管理模块 -->
          <div v-if="activeModule === 'user'" class="module-content">
            <div class="module-header">
              <h2>用户管理</h2>
              <p>系统用户账户和权限管理</p>
            </div>
            <div class="module-grid">
              <div class="module-card">
                <div class="card-icon">👤</div>
                <h3>用户账户</h3>
                <p>管理系统用户账户</p>
                <div class="card-actions">
                  <button class="btn-primary">用户列表</button>
                  <button class="btn-secondary">添加用户</button>
                </div>
              </div>
              <div class="module-card">
                <div class="card-icon">🔐</div>
                <h3>权限管理</h3>
                <p>用户角色和权限设置</p>
                <div class="card-actions">
                  <button class="btn-primary">角色管理</button>
                  <button class="btn-secondary">权限配置</button>
                </div>
              </div>
              <div class="module-card">
                <div class="card-icon">📊</div>
                <h3>登录日志</h3>
                <p>用户登录活动记录</p>
                <div class="card-actions">
                  <button class="btn-primary">查看日志</button>
                  <button class="btn-secondary">安全分析</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 系统监控模块 -->
          <div v-if="activeModule === 'system'" class="module-content">
            <div class="module-header">
              <h2>系统监控</h2>
              <p>系统性能监控和运行状态管理</p>
            </div>
            <div class="module-grid">
              <div class="module-card">
                <div class="card-icon">📊</div>
                <h3>性能监控</h3>
                <p>CPU、内存、磁盘使用率监控</p>
                <div class="card-actions">
                  <button class="btn-primary">实时监控</button>
                  <button class="btn-secondary">性能报告</button>
                </div>
              </div>
              <div class="module-card">
                <div class="card-icon">🔍</div>
                <h3>日志分析</h3>
                <p>系统日志查看和分析</p>
                <div class="card-actions">
                  <button class="btn-primary">日志查看</button>
                  <button class="btn-secondary">错误分析</button>
                </div>
              </div>
              <div class="module-card">
                <div class="card-icon">🛡️</div>
                <h3>安全监控</h3>
                <p>系统安全状态和威胁检测</p>
                <div class="card-actions">
                  <button class="btn-primary">安全状态</button>
                  <button class="btn-secondary">威胁检测</button>
                </div>
              </div>
            </div>
          </div>
       </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
/* import { message } from 'ant-design-vue' */

export default defineComponent({
  name: 'Home',
  setup() {
    const router = useRouter()
    
    // 当前激活的模块
    const activeModule = ref('overview')
    
    // 卡片式轮播相关
    const currentCardSlide = ref(0)
    const cardCarouselAutoPlayInterval = ref(null)
    
    // 用户信息
    const userInfo = ref({
      username: '',
      role: '',
      avatar: '' // Added avatar field
    })
    
    // 统计数据 - 超过30条相关数据
    const stats = ref({
      // 员工管理数据
      employees: 156,
      activeEmployees: 148,
      newEmployees: 8,
      departments: 12,
      avgWorkYears: 3.2,
      
      // 审批管理数据
      approvals: 23,
      todayApprovals: 5,
      processedApprovals: 156,
      avgProcessTime: 2.4,
      approvalRate: 92,
      
      // 考勤管理数据
      attendance: 94,
      todayAttendance: 142,
      lateCount: 3,
      leaveCount: 5,
      overtimeHours: 28,
      
      // 文件管理数据
      files: 1247,
      todayUploads: 23,
      storageUsed: 156,
      sharedFiles: 89,
      downloadCount: 2341,
      
      // 公告管理数据
      announcements: 67,
      monthlyAnnouncements: 12,
      readRate: 87,
      pinnedAnnouncements: 3,
      interactionCount: 156,
      
      // 系统监控数据
      systemUptime: 99.8,
      cpuUsage: 45,
      memoryUsage: 62,
      onlineUsers: 89,
      todayVisits: 234
    })

    // 部门分布数据（百分比）
    const departmentData = ref([
      { name: '技术部', value: 40 },
      { name: '人事部', value: 33 },
      { name: '财务部', value: 27 }
    ])
    const pieColors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#f9ca24', '#e056fd']

    // 月度数据
    const monthlyData = ref([
      { month: '1月', value: 85 },
      { month: '2月', value: 92 },
      { month: '3月', value: 78 },
      { month: '4月', value: 95 },
      { month: '5月', value: 88 },
      { month: '6月', value: 96 }
    ])

    const pieCanvas = ref(null)
    const barCanvas = ref(null)

    const switchModule = (module) => {
      // 如果是概览页面，保持在当前页面
      if (module === 'overview') {
        activeModule.value = module
        return
      }
      
      // 其他模块跳转到对应的路由页面
      const routeMap = {
        'employee': '/admin/employee',
        'approval': '/admin/approval',
        'attendance': '/admin/attendance',
        'announcement': '/admin/announcement',
        'file': '/admin/file',
        'user': '/admin/user',
        'system': '/admin/system'
      }
      
      const route = routeMap[module]
      if (route) {
        router.push(route)
      }
    }

    const getModuleTitle = () => {
      const titles = {
        'overview': '数据概览',
        'employee': '员工管理',
        'approval': '审批管理',
        'attendance': '考勤管理',
        'announcement': '公告管理',
        'file': '文件管理',
        'user': '用户管理',
        'system': '系统监控'
      }
      return titles[activeModule.value] || '数据概览'
    }

    // 卡片式轮播控制函数
    const nextCardSlide = () => {
      currentCardSlide.value = (currentCardSlide.value + 1) % 3 // 3组轮播
    }

    const prevCardSlide = () => {
      currentCardSlide.value = currentCardSlide.value === 0 ? 2 : currentCardSlide.value - 1
    }

    const goToCardSlide = (index, module) => {
      if (typeof index === 'number' && index < 3) {
        currentCardSlide.value = index
      }
      // 如果传入了模块名，跳转到对应模块
      if (module) {
        activeModule.value = module
      }
    }

    const startCardCarouselAutoPlay = () => {
      cardCarouselAutoPlayInterval.value = setInterval(() => {
        nextCardSlide()
      }, 3000) // 3秒自动切换
    }

    const stopCardCarouselAutoPlay = () => {
      if (cardCarouselAutoPlayInterval.value) {
        clearInterval(cardCarouselAutoPlayInterval.value)
        cardCarouselAutoPlayInterval.value = null
      }
    }



    const goToProfile = () => {
      router.push('/admin/profile')
    }

    const handleLogout = () => {
      // 只清空登录相关的信息，保留保存的个人信息
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('username')
      // 不清空 'savedUserInfo'，这样下次登录时个人信息会保持
      router.push('/login')
    }

    const drawPieChart = () => {
      const canvas = pieCanvas.value
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const width = canvas.clientWidth
      const height = canvas.clientHeight
      canvas.width = width * dpr
      canvas.height = height * dpr
      ctx.scale(dpr, dpr)

      // 清空
      ctx.clearRect(0, 0, width, height)

      const radius = Math.min(width, height) / 2 - 10
      const centerX = width / 2
      const centerY = height / 2
      let startAngle = -Math.PI / 2

      departmentData.value.forEach((seg, i) => {
        const angle = (seg.value / 100) * Math.PI * 2
        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.arc(centerX, centerY, radius, startAngle, startAngle + angle)
        ctx.closePath()
        ctx.fillStyle = pieColors[i % pieColors.length]
        ctx.fill()
        startAngle += angle
      })

      // 白色内圈制造环形效果
      ctx.beginPath()
      ctx.arc(centerX, centerY, radius * 0.55, 0, Math.PI * 2)
      ctx.fillStyle = '#ffffff'
      ctx.fill()
    }

    const drawBarChart = () => {
      const canvas = barCanvas.value
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const width = canvas.clientWidth
      const height = canvas.clientHeight
      canvas.width = width * dpr
      canvas.height = height * dpr
      ctx.scale(dpr, dpr)

      // 清空
      ctx.clearRect(0, 0, width, height)

      const padding = { left: 40, right: 30, top: 30, bottom: 40 }
      const chartW = width - padding.left - padding.right
      const chartH = height - padding.top - padding.bottom

      // 绘制Y轴刻度线
      const tickCount = 4
      for (let i = 0; i <= tickCount; i++) {
        const y = padding.top + (chartH / tickCount) * i
        ctx.strokeStyle = '#3b82f6'
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(padding.left - 5, y)
        ctx.lineTo(padding.left, y)
        ctx.stroke()
      }

      // 绘制坐标轴
      ctx.strokeStyle = '#3b82f6'
      ctx.lineWidth = 2
      
      // X轴
      ctx.beginPath()
      ctx.moveTo(padding.left, height - padding.bottom)
      ctx.lineTo(width - padding.right, height - padding.bottom)
      // X轴箭头
      ctx.lineTo(width - padding.right - 8, height - padding.bottom - 4)
      ctx.moveTo(width - padding.right, height - padding.bottom)
      ctx.lineTo(width - padding.right - 8, height - padding.bottom + 4)
      ctx.stroke()
      
      // Y轴
      ctx.beginPath()
      ctx.moveTo(padding.left, height - padding.bottom)
      ctx.lineTo(padding.left, padding.top)
      // Y轴箭头
      ctx.lineTo(padding.left - 4, padding.top + 8)
      ctx.moveTo(padding.left, padding.top)
      ctx.lineTo(padding.left + 4, padding.top + 8)
      ctx.stroke()

      const barCount = monthlyData.value.length
      const barWidth = chartW / barCount * 0.6  // 条形宽度占60%
      const gap = chartW / barCount * 0.4       // 间距占40%
      const maxValue = Math.max(...monthlyData.value.map(m => m.value)) || 100

      // 定义5种不同的颜色
      const barColors = ['#fbbf24', '#fb923c', '#f43f5e', '#3b82f6', '#8b5cf6']

      monthlyData.value.forEach((m, i) => {
        const x = padding.left + i * (barWidth + gap) + gap * 0.5
        const h = (m.value / maxValue) * chartH
        const y = height - padding.bottom - h
        
        // 使用纯色填充
        ctx.fillStyle = barColors[i % barColors.length]
        
        const w = barWidth
        
        // 绘制矩形（无圆角）
        ctx.fillRect(x, y, w, h)

        // 月份标签
        ctx.fillStyle = '#64748b'
        ctx.font = '16px sans-serif'
        const text = m.month
        const textWidth = ctx.measureText(text).width
        ctx.fillText(text, x + w / 2 - textWidth / 2, height - padding.bottom + 25)
      })
    }

    const getUserInfo = () => {
      console.log('Home.vue - getUserInfo 开始执行')
      console.log('Home.vue - localStorage 检查:')
      console.log('  - token:', localStorage.getItem('token'))
      console.log('  - username:', localStorage.getItem('username'))
      console.log('  - user:', localStorage.getItem('user'))
      console.log('  - savedUserInfo:', localStorage.getItem('savedUserInfo'))
      
      const user = localStorage.getItem('user')
      if (user) {
        try {
          const parsedUser = JSON.parse(user)
          console.log('Home.vue - 解析后的用户数据:', parsedUser)
          
          // 检查是否有保存的个人信息，如果有则合并
          const savedUserInfo = localStorage.getItem('savedUserInfo')
          if (savedUserInfo) {
            try {
              const parsedSavedInfo = JSON.parse(savedUserInfo)
              console.log('Home.vue - 解析后的保存信息:', parsedSavedInfo)
              userInfo.value = {
                ...parsedUser,
                ...parsedSavedInfo, // 保存的个人信息优先级更高
                username: parsedSavedInfo.username || parsedUser.username, // 优先使用保存的用户名
                avatar: parsedSavedInfo.avatar || parsedUser.avatar || '' // 确保头像正确加载
              }
            } catch (error) {
              console.error('解析保存的用户信息失败:', error)
              userInfo.value = {
                ...parsedUser,
                avatar: parsedUser.avatar || ''
              }
            }
          } else {
            // 如果没有保存的个人信息，直接使用后端返回的用户信息
            userInfo.value = {
              ...parsedUser,
              avatar: parsedUser.avatar || ''
            }
          }
          
          console.log('Home.vue - 最终设置的用户信息:', userInfo.value)
        } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      } else {
        console.log('Home.vue - 未找到用户信息')
      }
    }

    // 监听用户信息更新事件，实现实时同步
    const handleUserInfoUpdate = () => {
      getUserInfo() // 重新加载用户信息
    }

    onMounted(() => {
      getUserInfo()
      // 绘制图表
      drawPieChart()
      drawBarChart()
      // 启动卡片轮播自动播放
      startCardCarouselAutoPlay()
      // 监听用户信息更新事件
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
      // 自适应
      window.addEventListener('resize', () => {
        drawPieChart()
        drawBarChart()
      })
    })

    onUnmounted(() => {
      // 清理事件监听器
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
      stopCardCarouselAutoPlay()
    })

    return {
      userInfo,
      stats,
      departmentData,
      pieColors,
      monthlyData,
      pieCanvas,
      barCanvas,
      activeModule,
      switchModule,
      getModuleTitle,
      currentCardSlide,
      nextCardSlide,
      prevCardSlide,
      goToCardSlide,
      goToProfile,
      handleLogout
    }
  }
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  position: relative;
}

.dashboard-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 20%, rgba(59, 130, 246, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.05) 0%, transparent 50%);
  pointer-events: none;
  z-index: 1;
}

/* 左侧导航栏 - 现代庄严风格 */
.sidebar {
  width: 320px;
  background: linear-gradient(180deg, #111827 0%, #1f2937 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  color: white;
  padding: 0;
  box-shadow: 8px 0 32px rgba(0, 0, 0, 0.4);
  position: relative;
  z-index: 10;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.03) 0%, rgba(139, 92, 246, 0.02) 100%);
  pointer-events: none;
}

.sidebar-header {
  padding: 40px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
}

.sidebar-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border-radius: 1px;
}

.logo {
  text-align: center;
  position: relative;
  z-index: 2;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.logo h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #f8fafc;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.logo p {
  margin: 0;
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.sidebar-nav {
  padding: 30px 0;
  position: relative;
  z-index: 2;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 18px 30px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  margin: 8px 20px;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.6s;
}

.nav-item:hover::before {
  left: 100%;
}

.nav-item:hover {
  background: rgba(59, 130, 246, 0.15);
  transform: translateX(8px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
}

.nav-item.active {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  box-shadow: 0 12px 35px rgba(59, 130, 246, 0.4);
  transform: translateX(12px);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px 0 0 2px;
}

.nav-icon {
  font-size: 20px;
  margin-right: 16px;
  width: 24px;
  text-align: center;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
}

.nav-text {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.3px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 主内容区域 - 现代庄严风格 */
.main-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  position: relative;
  z-index: 2;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

/* 顶部导航栏 - 现代庄严风格 */
.top-header {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  padding: 30px 40px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
  backdrop-filter: blur(20px);
  position: relative;
}

.top-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.02) 0%, rgba(139, 92, 246, 0.01) 100%);
  pointer-events: none;
}

.header-left h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.8px;
  position: relative;
  z-index: 2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 2;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box input {
  width: 300px;
  padding: 16px 50px 16px 20px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 24px;
  font-size: 15px;
  outline: none;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.8) 100%);
  backdrop-filter: blur(12px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.06),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
  transition: all 0.3s ease;
  font-weight: 500;
}

.search-box input:focus {
  border-color: #3b82f6;
  box-shadow: 
    0 8px 30px rgba(59, 130, 246, 0.15),
    0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.search-icon {
  position: absolute;
  right: 18px;
  color: #64748b;
  font-size: 18px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #475569;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(248, 250, 252, 0.6) 100%);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(226, 232, 240, 0.5);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.user-dropdown-link:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.15);
}

.user-avatar {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.username {
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.3px;
}

/* 内容区域 - 现代庄严风格 */
.content {
  padding: 40px;
  background: transparent;
  position: relative;
  z-index: 2;
}

.content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 10% 10%, rgba(59, 130, 246, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 90% 90%, rgba(139, 92, 246, 0.03) 0%, transparent 50%);
  pointer-events: none;
  z-index: -1;
}

/* 卡片式轮播图 - 现代庄严风格 */
.card-carousel-section {
  margin-bottom: 50px;
  position: relative;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.8) 0%, rgba(241, 245, 249, 0.6) 100%);
  border-radius: 24px;
  padding: 30px;
  border: 1px solid rgba(226, 232, 240, 0.4);
  backdrop-filter: blur(20px);
}

.card-carousel {
  position: relative;
  overflow: hidden;
  padding: 10px 0;
}

.card-carousel-container {
  display: flex;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  gap: 24px;
}

.stat-card-item {
  flex: 0 0 22%;
  min-width: 260px;
  height: 220px;
  border-radius: 24px;
  padding: 28px;
  position: relative;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  box-shadow: 
    0 12px 40px rgba(0, 0, 0, 0.15),
    0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-card-item:hover {
  transform: translateY(-12px) scale(1.03);
  box-shadow: 
    0 25px 60px rgba(0, 0, 0, 0.25),
    0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-card-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.stat-card-item:hover::before {
  opacity: 1;
}

/* 卡片主题色 */
.employee-card {
  background: linear-gradient(135deg, #4F46E5 0%, #3B82F6 100%);
}

.approval-card {
  background: linear-gradient(135deg, #8B5CF6 0%, #A855F7 100%);
}

.attendance-card {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
}

.file-card {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
}

.announcement-card {
  background: linear-gradient(135deg, #EC4899 0%, #DB2777 100%);
}

.system-card {
  background: linear-gradient(135deg, #6B7280 0%, #4B5563 100%);
}

/* 趋势徽章 */
.card-trend-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 24px;
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 700;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-card-item:hover .card-trend-badge {
  background: rgba(255, 255, 255, 0.35);
  transform: scale(1.05);
}

/* 图标 */
.card-icon-large {
  font-size: 36px;
  margin-bottom: 12px;
  opacity: 0.95;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
  transition: all 0.3s ease;
}

.stat-card-item:hover .card-icon-large {
  transform: scale(1.1);
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.3));
}

/* 数字 */
.card-number {
  font-size: 52px;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 6px;
  text-shadow: 0 3px 10px rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
}

.stat-card-item:hover .card-number {
  transform: scale(1.05);
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 标签 */
.card-label {
  font-size: 17px;
  font-weight: 600;
  opacity: 0.95;
  margin-bottom: 18px;
  letter-spacing: 0.3px;
  transition: all 0.3s ease;
}

.stat-card-item:hover .card-label {
  opacity: 1;
  transform: translateY(-2px);
}

/* 进度条 */
.card-progress {
  margin-top: auto;
}

.progress-bar-modern {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill-modern {
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  transition: width 0.6s ease;
}

/* 导航按钮 */
.carousel-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 -20px;
  pointer-events: none;
}

.nav-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.6);
  color: #475569;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  backdrop-filter: blur(12px);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  pointer-events: auto;
}

.nav-btn:hover {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.3);
  transform: scale(1.1);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.prev-btn {
  left: -22px;
}

.next-btn {
  right: -22px;
}

/* 指示器 */
.carousel-dots {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

.carousel-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.carousel-dot:hover {
  background: rgba(59, 130, 246, 0.5);
  transform: scale(1.2);
}

.carousel-dot.active {
  background: #3b82f6;
  border-color: rgba(59, 130, 246, 0.8);
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.6);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-card-item {
    flex: 0 0 30%;
    min-width: 240px;
  }
}

@media (max-width: 768px) {
  .stat-card-item {
    flex: 0 0 80%;
    min-width: 200px;
  }
}

/* 统计卡片 - 现代庄严风格 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  margin-bottom: 40px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 1px solid rgba(226, 232, 240, 0.6);
  backdrop-filter: blur(20px);
  width: 100%;
  max-width: 500px;
  min-height: 160px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.02) 0%, rgba(139, 92, 246, 0.01) 100%);
  pointer-events: none;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 25px rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.3);
}

.stat-card-1 {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.95) 0%, rgba(37, 99, 235, 0.9) 100%);
  color: white;
}

.stat-card-2 {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.95) 0%, rgba(124, 58, 237, 0.9) 100%);
  color: white;
}

.stat-card-3 {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.95) 0%, rgba(5, 150, 105, 0.9) 100%);
  color: white;
}

.stat-card-4 {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.95) 0%, rgba(217, 119, 6, 0.9) 100%);
  color: white;
}

.stat-icon {
  font-size: 48px;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  position: relative;
  z-index: 2;
}

.stat-number {
  font-size: 42px;
  font-weight: 800;
  margin-bottom: 8px;
  letter-spacing: -1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

.stat-label {
  font-size: 16px;
  opacity: 0.95;
  margin-bottom: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

.stat-trend {
  position: absolute;
  top: 24px;
  right: 24px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(8px);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 700;
  border: 1px solid rgba(255, 255, 255, 0.3);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.stat-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 2;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 4px;
  overflow: hidden;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  border-radius: 4px;
  transition: width 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

/* 轮播图指示器 */
.carousel-indicators {
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 5;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.indicator:hover {
  background: rgba(59, 130, 246, 0.5);
}

.indicator.active {
  background: #3b82f6;
  border-color: rgba(59, 130, 246, 0.8);
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.6);
}

/* 轮播图控制按钮 */
.carousel-controls {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 5;
}

.control-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.8);
  color: #475569;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  backdrop-filter: blur(12px);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.control-btn:hover {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.3);
  transform: scale(1.1);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.control-btn.prev {
  left: 20px;
}

.control-btn.next {
  right: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
    gap: 24px;
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

.chart-content { display: grid; grid-template-columns: 240px 1fr; gap: 24px; align-items: center; }
.chart-canvas { width: 240px; height: 240px; display: block; }
.chart-legend { display: grid; gap: 8px; }
.legend-item { display: flex; align-items: center; gap: 8px; color: #4a5568; font-size: 12px; }
.legend-color { width: 12px; height: 12px; border-radius: 2px; }

/* 柱状图容器 */
.chart-card canvas.chart-canvas { 
  width: 100%; 
  height: 220px; 
}

/* 模块内容样式 - 现代庄严风格 */
.module-content {
  padding: 40px 0;
  position: relative;
}

.module-header {
  margin-bottom: 50px;
  text-align: center;
  position: relative;
}

.module-header::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
}

.module-header h2 {
  font-size: 36px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 16px 0;
  letter-spacing: -1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.module-header p {
  font-size: 18px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 32px;
  margin-bottom: 40px;
}

.module-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 1px solid rgba(226, 232, 240, 0.6);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.module-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.02) 0%, rgba(139, 92, 246, 0.01) 100%);
  pointer-events: none;
}

.module-card:hover {
  transform: translateY(-12px) scale(1.03);
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 25px rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
}

.card-icon {
  font-size: 64px;
  margin-bottom: 24px;
  text-align: center;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  position: relative;
  z-index: 2;
}

.module-card h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
  text-align: center;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

.module-card p {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 32px 0;
  text-align: center;
  line-height: 1.6;
  font-weight: 500;
  letter-spacing: 0.3px;
  position: relative;
  z-index: 2;
}

.card-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.btn-primary, .btn-secondary {
  padding: 14px 28px;
  border: none;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  letter-spacing: 0.3px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.btn-primary:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 25px rgba(59, 130, 246, 0.4);
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
}

.btn-secondary {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.8) 100%);
  color: #475569;
  border: 1px solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(8px);
}

.btn-secondary:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.15);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .module-grid {
    grid-template-columns: 1fr;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .btn-primary, .btn-secondary {
    width: 100%;
  }
}
</style>