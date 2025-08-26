import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import Employee from '@/views/Employee.vue'
import Approval from '@/views/Approval.vue'
import Attendance from '@/views/Attendance.vue'
import Announcement from '@/views/Announcement.vue'
import FileManager from '@/views/FileManager.vue'
import UserManagement from '@/views/UserManagement.vue'
import System from '@/views/System.vue'
import Profile from '@/views/Profile.vue'

// 用户端页面导入
import UserProfile from '@/views/user/UserProfile.vue'
import UserHome from '@/views/user/UserHome.vue'
import MyWorkbench from '@/views/user/MyWorkbench.vue'
import MyApproval from '@/views/user/MyApproval.vue'
import MyAttendance from '@/views/user/MyAttendance.vue'
import MyFiles from '@/views/user/MyFiles.vue'
import MyAnnouncement from '@/views/user/MyAnnouncement.vue'
import UserDataManager from '@/views/user/UserDataManager.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true, title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  
  // 用户端路由 - 放在管理端路由之前
  {
    path: '/user',
    name: 'UserHome',
    component: UserHome,
    meta: { requiresAuth: true, title: '用户主页' }
  },
  {
    path: '/user/workbench',
    name: 'UserWorkbench',
    component: MyWorkbench,
    meta: { requiresAuth: true, title: '我的工作台' }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: UserProfile,
    meta: { requiresAuth: true, title: '个人信息' }
  },
  {
    path: '/user/approval',
    name: 'MyApproval',
    component: MyApproval,
    meta: { requiresAuth: true, title: '我的审批' }
  },
  {
    path: '/user/attendance',
    name: 'MyAttendance',
    component: MyAttendance,
    meta: { requiresAuth: true, title: '我的考勤' }
  },
  {
    path: '/user/files',
    name: 'MyFiles',
    component: MyFiles,
    meta: { requiresAuth: true, title: '我的文件' }
  },
  {
    path: '/user/announcement',
    name: 'MyAnnouncement',
    component: MyAnnouncement,
    meta: { requiresAuth: true, title: '我的公告' }
  },
  {
    path: '/user/data-manager',
    name: 'UserDataManager',
    component: UserDataManager,
    meta: { requiresAuth: true, title: '数据管理' }
  },
  
  // 管理端路由
  {
    path: '/admin/employee',
    name: 'Employee',
    component: Employee,
    meta: { requiresAuth: true, title: '员工管理' }
  },
  {
    path: '/admin/approval',
    name: 'Approval',
    component: Approval,
    meta: { requiresAuth: true, title: '审批管理' }
  },
  {
    path: '/admin/attendance',
    name: 'Attendance',
    component: Attendance,
    meta: { requiresAuth: true, title: '考勤管理' }
  },
  {
    path: '/admin/announcement',
    name: 'Announcement',
    component: Announcement,
    meta: { requiresAuth: true, title: '公告管理' }
  },
  {
    path: '/admin/file',
    name: 'FileManager',
    component: FileManager,
    meta: { requiresAuth: true, title: '文件管理' }
  },
  {
    path: '/admin/user',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true, title: '用户管理' }
  },
  {
    path: '/admin/system',
    name: 'System',
    component: System,
    meta: { requiresAuth: true, title: '系统监控' }
  },
  {
    path: '/admin/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true, title: '个人资料' }
  },
  
  // 兼容旧的管理端路由（重定向到新的路径）
  {
    path: '/employee',
    redirect: '/admin/employee'
  },
  {
    path: '/approval',
    redirect: '/admin/approval'
  },
  {
    path: '/attendance',
    redirect: '/admin/attendance'
  },
  {
    path: '/announcement',
    redirect: '/admin/announcement'
  },
  {
    path: '/file',
    redirect: '/admin/file'
  },
  {
    path: '/user-management',
    redirect: '/admin/user'
  },
  {
    path: '/system',
    redirect: '/admin/system'
  },
  {
    path: '/profile',
    redirect: '/admin/profile'
  }
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

// 简化的路由守卫
router.beforeEach((to, from, next) => {
  console.log('路由跳转:', { from: from.path, to: to.path, requiresAuth: to.meta.requiresAuth })
  
  // 如果访问登录页面，直接允许
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果访问需要认证的页面，检查token
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      console.log('未登录，跳转到登录页面')
      next('/login')
      return
    }
  }
  
  // 其他情况正常通过
  next()
})

export default router 