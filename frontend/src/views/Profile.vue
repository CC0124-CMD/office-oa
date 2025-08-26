<template>
  <div class="profile-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="user" class="title-icon" />
            个人信息管理
          </h1>
          <p class="page-subtitle">管理您的个人资料、头像和基本信息</p>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info">
          <a-avatar :size="32" class="user-avatar" :src="headerUserInfo.avatar">
            <a-icon type="user" />
          </a-avatar>
          <span>欢迎，{{ headerUserInfo.username }}</span>
        </div>
        <a-button type="link" @click="goHome" class="home-btn">
          <a-icon type="home" /> 返回首页
        </a-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="profile-content">
      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <div class="avatar-container">
              <a-avatar 
                :size="120" 
                :src="userInfo.avatar || defaultAvatar"
                class="user-avatar"
              />
              <div class="avatar-overlay">
                <a-upload
                  name="avatar"
                  :show-upload-list="false"
                  :before-upload="beforeAvatarUpload"
                  @change="handleAvatarChange"
                  :customRequest="() => {}"
                >
                  <a-button type="primary" size="small">
                    <a-icon type="camera" /> 更换头像
                  </a-button>
                </a-upload>
              </div>
            </div>
            
            <!-- 默认头像选择 -->
            <div class="default-avatars">
              <h4>选择默认头像</h4>
              <div class="avatar-grid">
                <div 
                  v-for="(avatar, index) in defaultAvatars" 
                  :key="index"
                  class="default-avatar-item"
                  :class="{ active: userInfo.avatar === avatar }"
                  @click="selectDefaultAvatar(avatar)"
                >
                  <a-avatar :size="50" :src="avatar" />
                </div>
              </div>
            </div>
          </div>

          <div class="user-details">
            <a-form 
              :model="userInfo" 
              :label-col="{ span: 6 }" 
              :wrapper-col="{ span: 18 }"
              layout="horizontal"
            >
              <a-form-item label="用户名">
                <a-input v-model:value="userInfo.username" placeholder="请输入用户名" />
              </a-form-item>
              
              <a-form-item label="部门">
                <a-input v-model:value="userInfo.department" placeholder="请输入部门名称" />
              </a-form-item>
              
              <a-form-item label="职位">
                <a-input v-model:value="userInfo.position" placeholder="请输入职位名称" />
              </a-form-item>
              
              <a-form-item label="个人简介">
                <a-textarea 
                  v-model:value="userInfo.bio" 
                  :rows="4" 
                  placeholder="请输入个人简介"
                />
              </a-form-item>
            </a-form>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="profile-actions">
          <a-button type="primary" @click="saveProfile" :loading="saving">
            <a-icon type="save" /> 保存信息
          </a-button>
          <a-button @click="resetProfile" class="btn-reset">
            <a-icon type="undo" /> 重置
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

export default defineComponent({
  name: 'Profile',
  setup() {
    const router = useRouter()
    
    // 头部显示的用户信息（只在保存时更新）
    const headerUserInfo = reactive({
      username: '',
      avatar: ''
    })

    // 表单编辑的用户信息（支持实时输入）
    const userInfo = reactive({
      username: '',
      department: '',
      position: '',
      bio: '',
      avatar: ''
    })

    // 默认头像列表
    const defaultAvatars = [
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user1',
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user2',
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user3',
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user4',
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user5',
      'https://api.dicebear.com/7.x/avataaars/svg?seed=user6'
    ]

    const defaultAvatar = defaultAvatars[0]
    const saving = ref(false)

    // 加载用户信息
    const loadUserInfo = () => {
      try {
        // 从localStorage获取用户信息
        const token = localStorage.getItem('token')
        if (token) {
          // 首先检查是否有保存的个人信息，优先级最高
          const savedUserInfo = localStorage.getItem('savedUserInfo')
          if (savedUserInfo) {
            try {
              const parsedSavedInfo = JSON.parse(savedUserInfo)
              // 优先使用保存的个人信息
              userInfo.username = parsedSavedInfo.username || ''
              userInfo.department = parsedSavedInfo.department || ''
              userInfo.position = parsedSavedInfo.position || ''
              userInfo.bio = parsedSavedInfo.bio || ''
              userInfo.avatar = parsedSavedInfo.avatar || defaultAvatar
              
              // 同时更新头部显示信息
              headerUserInfo.username = parsedSavedInfo.username || ''
              headerUserInfo.avatar = parsedSavedInfo.avatar || defaultAvatar
              
              console.log('已加载保存的个人信息:', userInfo)
            } catch (error) {
              console.error('解析保存的用户信息失败:', error)
            }
          } else {
            // 如果没有保存的个人信息，则使用当前登录用户信息
            const currentUser = localStorage.getItem('user')
            if (currentUser) {
              try {
                const parsedUser = JSON.parse(currentUser)
                // 逐个赋值以确保响应式更新
                userInfo.username = parsedUser.username || localStorage.getItem('username') || 'user'
                userInfo.department = parsedUser.department || ''
                userInfo.position = parsedUser.position || ''
                userInfo.bio = parsedUser.bio || ''
                userInfo.avatar = parsedUser.avatar || defaultAvatar
                
                // 同时更新头部显示信息
                headerUserInfo.username = parsedUser.username || localStorage.getItem('username') || 'user'
                headerUserInfo.avatar = parsedUser.avatar || defaultAvatar
                
                console.log('已加载当前用户信息:', userInfo)
              } catch (error) {
                console.error('解析当前用户信息失败:', error)
              }
            }
          }
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    // 保存个人信息
    const saveProfile = async () => {
      saving.value = true
      try {
        // 模拟保存过程
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 只保存需要的字段到localStorage
        const savedUserInfo = {
          username: userInfo.username,
          department: userInfo.department,
          position: userInfo.position,
          bio: userInfo.bio,
          avatar: userInfo.avatar
        }
        localStorage.setItem('savedUserInfo', JSON.stringify(savedUserInfo))
        
        // 只有保存成功后，才更新头部显示的用户信息
        headerUserInfo.username = userInfo.username
        headerUserInfo.avatar = userInfo.avatar
        
        // 触发自定义事件，通知其他组件用户信息已更新
        window.dispatchEvent(new CustomEvent('userInfoUpdated', {
          detail: savedUserInfo
        }))
        
        message.success('个人信息保存成功！')
      } catch (error) {
        message.error('保存失败，请重试')
        console.error('保存错误:', error)
      } finally {
        saving.value = false
      }
    }

    // 重置个人信息
    const resetProfile = () => {
      // 重新加载用户信息
      loadUserInfo()
      message.info('个人信息已重置')
    }

    // 选择默认头像
    const selectDefaultAvatar = (avatar) => {
      userInfo.avatar = avatar
      message.success('头像已更新')
    }

    // 头像上传前处理
    const beforeAvatarUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isJpgOrPng) {
        message.error('只能上传 JPG/PNG 格式的图片!')
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    }

    // 头像上传处理
    const handleAvatarChange = (info) => {
      if (info.file.status === 'done') {
        // 这里可以处理头像上传成功后的逻辑
        message.success('头像上传成功')
      } else if (info.file.status === 'error') {
        message.error('头像上传失败')
      }
    }

    // 返回首页
    const goHome = () => {
      router.push('/')
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      userInfo,
      headerUserInfo,
      defaultAvatars,
      defaultAvatar,
      saving,
      saveProfile,
      resetProfile,
      selectDefaultAvatar,
      beforeAvatarUpload,
      handleAvatarChange,
      goHome
    }
  }
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

/* 页面头部样式 */
.page-header {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 150, 255, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #00a6ff;
  display: flex;
  align-items: center;
  gap: 12px;
  text-shadow: 0 0 20px rgba(0, 150, 255, 0.3);
}

.title-icon {
  font-size: 32px;
  color: #00a6ff;
}

.page-subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 400;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-info span {
  color: #fff;
  font-weight: 500;
}

.home-btn {
  color: #00a6ff !important;
  border-color: #00a6ff !important;
  background: rgba(0, 150, 255, 0.1) !important;
  border-radius: 8px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.home-btn:hover {
  background: rgba(0, 150, 255, 0.2) !important;
  border-color: #00a6ff !important;
  color: #00a6ff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 150, 255, 0.3);
}

/* 内容区域 */
.profile-content {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.profile-header {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
  align-items: start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.avatar-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  border: 4px solid #00a6ff;
  box-shadow: 0 8px 32px rgba(0, 150, 255, 0.3);
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 40px rgba(0, 150, 255, 0.4);
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.default-avatars {
  text-align: center;
}

.default-avatars h4 {
  margin: 0 0 16px 0;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.default-avatar-item {
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.default-avatar-item:hover {
  background: rgba(0, 150, 255, 0.1);
  border-color: rgba(0, 150, 255, 0.3);
}

.default-avatar-item.active {
  border-color: #00a6ff;
  background: rgba(0, 150, 255, 0.1);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-details .ant-form-item {
  margin-bottom: 0;
}

.user-details .ant-input,
.user-details .ant-textarea {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  padding: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.user-details .ant-input:focus,
.user-details .ant-textarea:focus {
  border-color: #00a6ff;
  box-shadow: 0 0 0 3px rgba(0, 150, 255, 0.1);
}

.user-details .ant-form-item-label > label {
  color: #374151;
  font-weight: 600;
  font-size: 14px;
}

.profile-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.profile-actions .ant-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.profile-actions .ant-btn-primary {
  background: linear-gradient(135deg, #00a6ff 0%, #0056b3 100%);
  border-color: #00a6ff;
}

.profile-actions .ant-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 150, 255, 0.4);
}

.btn-reset {
  background: #f3f4f6;
  border-color: #d1d5db;
  color: #374151;
}

.btn-reset:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

@media (max-width: 768px) {
  .profile-header {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .avatar-section {
    order: 2;
  }
  
  .user-details {
    order: 1;
  }
  
  .profile-actions {
    flex-direction: column;
  }
  
  .page-header {
    padding: 20px;
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .profile-container {
    padding: 16px;
  }
  
  .profile-card {
    padding: 24px;
  }
}
</style> 