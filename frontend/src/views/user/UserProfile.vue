<template>
  <div class="user-profile-container">
    <!-- 通用头部 -->
    <CommonHeader 
      title="个人信息" 
      subtitle="管理您的个人资料" 
      iconType="user"
      :isUserSide="true"
    />
    
    <div class="user-profile-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <div class="profile-section">
          <div class="profile-header">
            <h2>个人资料</h2>
            <p>管理您的个人信息和账户设置</p>
          </div>
          
          <div class="profile-content">
            <!-- 头像上传区域 -->
            <div class="avatar-section">
              <div class="avatar-container">
                <a-avatar 
                  :size="140" 
                  :src="userInfo.avatar" 
                  class="profile-avatar"
                >
                  <template #icon><UserOutlined /></template>
                </a-avatar>
                <div class="avatar-overlay">
                  <a-upload
                    v-model:file-list="avatarFileList"
                    :before-upload="beforeAvatarUpload"
                    :show-upload-list="false"
                    accept="image/*"
                  >
                    <a-button type="primary" size="small">
                      <template #icon><CameraOutlined /></template>
                      更换头像
                    </a-button>
                  </a-upload>
                </div>
              </div>
              <div class="avatar-info">
                <h3>{{ userInfo.username || '用户名' }}</h3>
                <p>{{ userInfo.department || '部门' }} · {{ userInfo.position || '职位' }}</p>
              </div>
            </div>
            
            <!-- 个人信息表单 -->
            <div class="profile-form">
              <a-form 
                :model="userInfo" 
                layout="vertical" 
                :label-col="{ span: 24 }"
                :wrapper-col="{ span: 24 }"
              >
                <div class="form-row">
                  <a-form-item label="用户名" name="username" class="form-item">
                    <a-input 
                      v-model:value="userInfo.username" 
                      placeholder="请输入用户名"
                      size="large"
                      class="form-input"
                    />
                  </a-form-item>
                  
                  <a-form-item label="部门" name="department" class="form-item">
                    <a-input 
                      v-model:value="userInfo.department" 
                      placeholder="请输入部门"
                      size="large"
                      class="form-input"
                    />
                  </a-form-item>
                </div>
                
                <div class="form-row">
                  <a-form-item label="职位" name="position" class="form-item">
                    <a-input 
                      v-model:value="userInfo.position" 
                      placeholder="请输入职位"
                      size="large"
                      class="form-input"
                    />
                  </a-form-item>
                  
                  <a-form-item label="工号" name="employeeId" class="form-item">
                    <a-input 
                      v-model:value="userInfo.employeeId" 
                      placeholder="请输入工号"
                      size="large"
                      disabled
                      class="form-input"
                    />
                  </a-form-item>
                </div>
                
                <a-form-item label="个人简介" name="bio" class="form-item-full">
                  <a-textarea 
                    v-model:value="userInfo.bio" 
                    placeholder="请输入个人简介"
                    :rows="4"
                    size="large"
                    class="form-textarea"
                  />
                </a-form-item>
              </a-form>
              
              <!-- 操作按钮 -->
              <div class="form-actions">
                <a-button 
                  type="primary" 
                  size="large" 
                  @click="saveProfile" 
                  :loading="saving"
                  class="save-btn"
                >
                  <template #icon><SaveOutlined /></template>
                  保存信息
                </a-button>
                <a-button 
                  size="large" 
                  @click="resetForm"
                  class="reset-btn"
                >
                  <template #icon><ReloadOutlined /></template>
                  重置
                </a-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 账户信息卡片 -->
        <div class="account-info-section">
          <h3>账户信息</h3>
          <div class="info-cards">
            <div class="info-card">
              <div class="info-icon">📧</div>
              <div class="info-content">
                <div class="info-label">邮箱</div>
                <div class="info-value">{{ accountInfo.email || '未设置' }}</div>
              </div>
            </div>
            
            <div class="info-card">
              <div class="info-icon">📱</div>
              <div class="info-content">
                <div class="info-label">手机号</div>
                <div class="info-value">{{ accountInfo.phone || '未设置' }}</div>
              </div>
            </div>
            
            <div class="info-card">
              <div class="info-icon">🏢</div>
              <div class="info-content">
                <div class="info-label">所属公司</div>
                <div class="info-value">{{ accountInfo.company || '智能OA系统' }}</div>
              </div>
            </div>
            
            <div class="info-card">
              <div class="info-icon">📅</div>
              <div class="info-content">
                <div class="info-label">注册时间</div>
                <div class="info-value">{{ formatDate(accountInfo.registerTime) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  CameraOutlined,
  SaveOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'

// 响应式数据
const saving = ref(false)
const avatarFileList = ref([])

// 用户信息
const userInfo = reactive({
  username: '',
  department: '',
  position: '',
  employeeId: '',
  bio: '',
  avatar: ''
})

// 账户信息
const accountInfo = reactive({
  email: '',
  phone: '',
  company: '智能OA系统',
  registerTime: new Date()
})

// 默认头像
const defaultAvatar = 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png'

// 方法
const loadUserInfo = () => {
  try {
    const token = localStorage.getItem('token')
    if (token) {
      const savedUserInfo = localStorage.getItem('savedUserInfo')
      if (savedUserInfo) {
        const parsedSavedInfo = JSON.parse(savedUserInfo)
        userInfo.username = parsedSavedInfo.username || ''
        userInfo.department = parsedSavedInfo.department || ''
        userInfo.position = parsedSavedInfo.position || ''
        userInfo.bio = parsedSavedInfo.bio || ''
        userInfo.avatar = parsedSavedInfo.avatar || defaultAvatar
        userInfo.employeeId = parsedSavedInfo.employeeId || 'EMP001'
        
        console.log('已加载保存的个人信息:', userInfo)
      } else {
        const currentUser = localStorage.getItem('user')
        if (currentUser) {
          const parsedUser = JSON.parse(currentUser)
          userInfo.username = parsedUser.username || localStorage.getItem('username') || 'user'
          userInfo.department = parsedUser.department || ''
          userInfo.position = parsedUser.position || ''
          userInfo.bio = parsedUser.bio || ''
          userInfo.avatar = parsedUser.avatar || defaultAvatar
          userInfo.employeeId = parsedUser.employeeId || 'EMP001'
          
          console.log('已加载当前用户信息:', userInfo)
        }
      }
      
      // 加载账户信息
      accountInfo.email = localStorage.getItem('userEmail') || ''
      accountInfo.phone = localStorage.getItem('userPhone') || ''
      accountInfo.registerTime = new Date(localStorage.getItem('registerTime') || Date.now())
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

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

const saveProfile = async () => {
  if (!userInfo.username.trim()) {
    message.warning('请输入用户名')
    return
  }
  
  saving.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const savedUserInfo = {
      username: userInfo.username,
      department: userInfo.department,
      position: userInfo.position,
      employeeId: userInfo.employeeId,
      bio: userInfo.bio,
      avatar: userInfo.avatar
    }
    
    localStorage.setItem('savedUserInfo', JSON.stringify(savedUserInfo))
    
    // 更新其他页面的用户信息
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

const resetForm = () => {
  loadUserInfo()
  message.info('表单已重置')
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadUserInfo()
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.user-profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.user-profile-content {
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

.profile-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 48px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 48px;
}

.profile-header h2 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 12px;
  font-weight: 700;
}

.profile-header p {
  color: #7f8c8d;
  font-size: 16px;
  margin: 0;
}

.profile-content {
  display: flex;
  gap: 60px;
  margin-bottom: 40px;
  flex-wrap: wrap;
  align-items: flex-start;
}

.avatar-section {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.profile-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  padding: 8px;
  border-radius: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar-info {
  text-align: center;
}

.avatar-info h3 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 4px;
}

.avatar-info p {
  font-size: 14px;
  color: #7f8c8d;
}

.profile-form {
  flex: 1;
  min-width: 400px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-item {
  flex: 1;
}

.form-item-full {
  margin-bottom: 20px;
}

.form-input {
  height: 44px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  padding: 0 12px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  outline: none;
}

.form-textarea {
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  padding: 12px;
  font-size: 14px;
  line-height: 1.5;
  min-height: 80px;
  transition: all 0.3s ease;
}

.form-textarea:focus {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  outline: none;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.save-btn {
  height: 44px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.save-btn:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.reset-btn {
  height: 44px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  border: 2px solid #d9d9d9;
  color: #666;
  background: #fff;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #40a9ff;
  color: #40a9ff;
  background: #f0f8ff;
}

.account-info-section {
  border-top: 1px solid #e9ecef;
  padding-top: 40px;
}

.account-info-section h3 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.info-card:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 12px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    gap: 30px;
  }
  
  .profile-form {
    min-width: auto;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .save-btn,
  .reset-btn {
    width: 100%;
  }
  
  .info-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .profile-section {
    padding: 30px 20px;
  }
  
  .info-cards {
    grid-template-columns: 1fr;
  }
  
  .avatar-container {
    text-align: center;
  }
}
</style>
