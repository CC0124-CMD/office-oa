<template>
  <div class="navigation-container">
    <div class="nav-header">
      <h3>{{ isAdmin ? '管理端导航' : '用户端导航' }}</h3>
    </div>
    <div class="nav-menu">
      <div 
        v-for="item in navigationItems" 
        :key="item.path"
        class="nav-item"
        :class="{ active: currentPath === item.path }"
        @click="navigateTo(item.path)"
      >
        <a-icon :type="item.icon" class="nav-icon" />
        <span class="nav-text">{{ item.title }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default defineComponent({
  name: 'CommonNavigation',
  props: {
    isAdmin: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    
    const currentPath = computed(() => route.path)
    
    // 管理端导航项
    const adminNavigationItems = [
      { path: '/admin/employee', title: '员工管理', icon: 'team' },
      { path: '/admin/user-management', title: '用户管理', icon: 'user' },
      { path: '/admin/file-manager', title: '文件管理', icon: 'folder' },
      { path: '/admin/approval', title: '审批管理', icon: 'check-circle' },
      { path: '/admin/attendance', title: '考勤管理', icon: 'clock-circle' },
      { path: '/admin/announcement', title: '公告管理', icon: 'sound' },
      { path: '/admin/system', title: '系统监控', icon: 'dashboard' }
    ]
    
    // 用户端导航项
    const userNavigationItems = [
      { path: '/user', title: '用户主页', icon: 'home' },
      { path: '/user/workbench', title: '我的工作台', icon: 'dashboard' },
      { path: '/user/profile', title: '个人信息', icon: 'user' },
      { path: '/user/approval', title: '我的审批', icon: 'check-circle' },
      { path: '/user/attendance', title: '我的考勤', icon: 'clock-circle' },
      { path: '/user/files', title: '我的文件', icon: 'folder' },
      { path: '/user/announcement', title: '我的公告', icon: 'sound' },
      { path: '/user/data-manager', title: '数据管理', icon: 'database' }
    ]
    
    const navigationItems = computed(() => 
      props.isAdmin ? adminNavigationItems : userNavigationItems
    )
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    return {
      currentPath,
      navigationItems,
      navigateTo
    }
  }
})
</script>

<style scoped>
.navigation-container {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.nav-header h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
  padding-bottom: 16px;
  border-bottom: 2px solid #e5e7eb;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.nav-item:hover {
  background: #f8fafc;
  border-color: #e2e8f0;
  transform: translateX(4px);
}

.nav-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.nav-item.active .nav-icon {
  color: white;
}

.nav-icon {
  font-size: 18px;
  color: #64748b;
  width: 20px;
  text-align: center;
}

.nav-text {
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .navigation-container {
    padding: 16px;
  }
  
  .nav-item {
    padding: 10px 12px;
  }
}
</style> 