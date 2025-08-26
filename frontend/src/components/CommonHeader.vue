<template>
  <div class="page-header">
    <div class="header-left">
      <div class="title-section">
        <h1 class="page-title">
          <a-icon :type="iconType" class="title-icon" />
          {{ title }}
        </h1>
        <p class="page-subtitle">{{ subtitle }}</p>
      </div>
    </div>
    <div class="header-right">
      <div class="user-info">
        <span>欢迎, {{ headerUserInfo.username || userInfo.username || '用户' }}</span>
        <a-avatar 
          :src="headerUserInfo.avatar || userInfo.avatar" 
          class="user-avatar"
          size="small"
        >
          <template #icon><UserOutlined /></template>
        </a-avatar>
      </div>
      
      <button class="home-btn" @click="goHome">
        返回首页
      </button>
      
      <!-- 退出登录按钮 -->
      <button class="logout-btn" @click="handleLogout">
        退出登录
      </button>
    </div>

    <!-- 退出登录确认对话框 -->
    <a-modal
      v-model:visible="showLogoutConfirm"
      title="确认退出登录"
      :confirm-loading="loggingOut"
      @ok="confirmLogout"
      @cancel="showLogoutConfirm = false"
      ok-text="确认退出"
      cancel-text="取消"
    >
      <p>您确定要退出登录吗？退出后需要重新登录才能访问系统。</p>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, inject, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined } from '@ant-design/icons-vue'

export default defineComponent({
  name: 'CommonHeader',
  components: {
    UserOutlined
  },
  props: {
    title: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    iconType: {
      type: String,
      default: 'setting'
    },
    // 添加一个prop来明确指定当前是用户端还是管理端
    isUserSide: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    
    // 注入用户信息（从父组件传递）
    const userInfo = inject('userInfo', {
      username: '',
      avatar: ''
    })

    // 本组件内部用于显示的用户信息，监听全局事件实时更新
    const headerUserInfo = ref({ username: '', avatar: '' })

    const loadHeaderUserInfo = () => {
      try {
        // 1) 优先使用刚注册的信息（未登录前的占位显示）
        const recent = localStorage.getItem('recentRegisteredUser')
        if (recent) {
          const parsedRecent = JSON.parse(recent)
          headerUserInfo.value = {
            username: parsedRecent.username || '',
            avatar: parsedRecent.avatar || ''
          }
          return
        }
        // 2) 使用已保存的个人资料
        const saved = localStorage.getItem('savedUserInfo')
        if (saved) {
          const parsed = JSON.parse(saved)
          headerUserInfo.value = {
            username: parsed.username || '',
            avatar: parsed.avatar || ''
          }
          return
        }
        // 3) 回退到当前登录用户
        const user = localStorage.getItem('user')
        if (user) {
          const parsedUser = JSON.parse(user)
          headerUserInfo.value = {
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
        headerUserInfo.value = {
          username: detail.username || headerUserInfo.value.username,
          avatar: detail.avatar || headerUserInfo.value.avatar
        }
      } else {
        loadHeaderUserInfo()
      }
    }

    // 退出登录相关状态
    const showLogoutConfirm = ref(false)
    const loggingOut = ref(false)

    const goHome = () => {
      console.log('CommonHeader - isUserSide prop:', props.isUserSide)
      console.log('CommonHeader - 当前路径:', route.path)
      
      // 优先使用props中的isUserSide来判断
      if (props.isUserSide) {
        console.log('CommonHeader - 跳转到用户端首页: /user')
        router.push('/user')
      } else {
        console.log('CommonHeader - 跳转到管理端首页: /')
        router.push('/')
      }
    }

    // 退出登录
    const handleLogout = () => {
      showLogoutConfirm.value = true
    }

    const confirmLogout = async () => {
      loggingOut.value = true
      try {
        // 清除本地存储的用户信息
        localStorage.removeItem('user')
        localStorage.removeItem('username')
        localStorage.removeItem('token')
        localStorage.removeItem('savedUserInfo')
        
        // 跳转到登录页面
        if (props.isUserSide) {
          router.push('/login?system=user')
        } else {
          router.push('/login?system=admin')
        }
        
        message.success('退出登录成功')
      } catch (error) {
        console.error('退出登录失败:', error)
        message.error('退出登录失败，请重试')
      } finally {
        loggingOut.value = false
        showLogoutConfirm.value = false
      }
    }

    onMounted(() => {
      loadHeaderUserInfo()
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    onUnmounted(() => {
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    return {
      userInfo,
      headerUserInfo,
      goHome,
      showLogoutConfirm,
      loggingOut,
      handleLogout,
      confirmLogout
    }
  }
})
</script>

<style scoped>
.page-header {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 150, 255, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  margin: 0 0 24px 0;
  height: 64px;
  position: relative;
  z-index: 100;
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
  color: #fff;
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

.logout-btn {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border: none;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
}

.logout-btn:hover {
  color: #fff;
  background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.5);
}
</style> 