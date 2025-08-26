<template>
  <div class="login-bg">
    <!-- 登录/注册表单容器 -->
    <div class="login-container">
      <div class="login-card">
        <div class="logo-section">
          <div class="logo-icon">
            <a-icon type="robot" />
          </div>
          <h1 class="system-title">Smart OA 系统登录</h1>
          <div class="accent-underline"></div>
          <p class="subtitle">统一办公 · 高效协作</p>
          
          <!-- 系统选择切换 -->
          <div class="system-switch">
            <div class="switch-tabs">
              <div 
                class="switch-tab" 
                :class="{ active: selectedSystem === 'admin' }"
                @click="handleSystemChange('admin')"
              >
                <a-icon type="setting" />
                管理端
              </div>
              <div 
                class="switch-tab" 
                :class="{ active: selectedSystem === 'user' }"
                @click="handleSystemChange('user')"
              >
                <a-icon type="user" />
                用户端
              </div>
            </div>
          </div>
        </div>

        <!-- 登录表单 -->
        <a-form
          v-if="isLogin"
          :model="loginForm"
          @finish="onLoginFinish"
          autocomplete="off"
          layout="vertical"
          class="login-form"
        >
          <a-form-item
            name="username"
            :rules="[{ required: true, message: '请输入用户名!' }]"
            label="用户名"
          >
            <div class="custom-input-wrapper">
              <a-icon type="user" class="input-icon" />
              <input 
                v-model="loginForm.username" 
                type="text"
                placeholder="请输入用户名" 
                class="custom-input"
                autocomplete="off"
              />
            </div>
          </a-form-item>
          <a-form-item
            name="password"
            :rules="[{ required: true, message: '请输入密码!' }]"
            label="密码"
          >
            <div class="custom-input-wrapper">
              <a-icon type="lock" class="input-icon" />
              <input 
                v-model="loginForm.password" 
                type="password"
                placeholder="请输入密码" 
                class="custom-input"
                autocomplete="off"
              />
            </div>
          </a-form-item>
          <a-form-item>
            <a-button 
              type="primary" 
              html-type="submit" 
              block 
              size="large" 
              :loading="loading"
              class="login-button"
            >
              <a-icon type="login" />
              登录系统
            </a-button>
          </a-form-item>
          <div class="status-inline" v-if="backendStatus !== null">
            <a-icon :type="backendStatus ? 'check-circle' : 'close-circle'" :class="['status-dot', backendStatus ? 'ok' : 'bad']" />
            <span :class="['status-label', backendStatus ? 'ok' : 'bad']">{{ backendStatus ? '后端服务正常' : '后端服务未启动' }}</span>
            <a @click="checkBackendStatus" class="status-action">重新检查</a>
          </div>
          <!-- 只在用户端显示注册链接 -->
          <div class="form-footer" v-if="selectedSystem === 'user'">
            <a @click="toggleForm" class="switch-link">没有账号? 点击注册</a>
          </div>
        </a-form>
        
        <!-- 注册表单 -->
        <a-form
          v-else
          :model="registerForm"
          @finish="onRegisterFinish"
          autocomplete="off"
          layout="vertical"
          class="register-form"
        >
          <a-form-item
            name="username"
            :rules="[
              { required: true, message: '请输入用户名!' },
              { min: 3, message: '用户名至少3个字符!' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线!' }
            ]"
            label="用户名"
          >
            <div class="custom-input-wrapper">
              <a-icon type="user" class="input-icon" />
              <input 
                v-model="registerForm.username" 
                type="text"
                placeholder="请输入用户名" 
                class="custom-input"
                autocomplete="off"
              />
            </div>
          </a-form-item>
          <a-form-item
            name="password"
            :rules="[
              { required: true, message: '请输入密码!' },
              { min: 6, message: '密码至少6个字符!' }
            ]"
            label="密码"
          >
            <div class="custom-input-wrapper">
              <a-icon type="lock" class="input-icon" />
              <input 
                v-model="registerForm.password" 
                type="password"
                placeholder="请输入密码" 
                class="custom-input"
                autocomplete="off"
              />
            </div>
          </a-form-item>
          <a-form-item
            name="confirmPassword"
            :rules="[
              { required: true, message: '请确认密码!' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致!' }
            ]"
            label="确认密码"
          >
            <div class="custom-input-wrapper">
              <a-icon type="safety" class="input-icon" />
              <input 
                v-model="registerForm.confirmPassword" 
                type="password"
                placeholder="请再次输入密码" 
                class="custom-input"
                autocomplete="off"
              />
            </div>
          </a-form-item>

          <a-form-item>
            <a-button 
              type="primary" 
              html-type="submit" 
              block
              size="large" 
              :loading="loading"
              class="register-button"
            >
              <a-icon type="user-add" />
              注册账号
            </a-button>
          </a-form-item>
          <div class="form-footer">
            <a @click="toggleForm" class="switch-link">已有账号?返回登录</a>
          </div>
        </a-form>
      </div>

      <!-- 服务状态（移动端或需要时显示） -->
      <div class="info-section minimal">
        <div class="service-status">
          <h3 class="section-title">服务状态</h3>
          <p class="status-message" :class="{ error: !backendStatus }">
            {{ backendStatus ? '后端服务运行正常，可登录' : '后端服务未启动，请检查' }}
          </p>
          <a-button type="default" size="small" @click="checkBackendStatus" class="status-check-btn">检查服务状态</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { setCurrentUser } from '@/utils/userDataManager'
import { captureError, setUser, addBreadcrumb } from '@/utils/sentry'

export default defineComponent({
  name: 'Login',
  setup() {
    const router = useRouter()
    
    // 表单状态
    const isLogin = ref(true)
    const loading = ref(false)
    const backendStatus = ref(null)
    const selectedSystem = ref('user') // 默认选择用户端

    // 登录表单 - 确保为空
    const loginForm = reactive({
      username: '',
      password: ''
    })

    // 注册表单 - 确保为空
    const registerForm = reactive({
      username: '',
      password: '',
      confirmPassword: ''
    })

    // 切换登录/注册表单 - 只在用户端允许
    const toggleForm = () => {
      if (selectedSystem.value === 'admin') {
        message.warning('管理端不支持注册功能')
        return
      }
      isLogin.value = !isLogin.value
      // 清空表单
      loginForm.username = ''
      loginForm.password = ''
      registerForm.username = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
    }

    // 监听系统选择变化，自动切换到登录模式
    const handleSystemChange = (system) => {
      selectedSystem.value = system
      if (system === 'admin') {
        isLogin.value = true // 管理端强制使用登录模式
      }
      // 清空所有表单
      Object.assign(loginForm, { username: '', password: '' })
      Object.assign(registerForm, { username: '', password: '', confirmPassword: '' })
    }

    // 验证确认密码
    const validateConfirmPassword = (rule, value) => {
      if (value && value !== registerForm.password) {
        return Promise.reject('两次输入的密码不一致!')
      }
      return Promise.resolve()
    }

    // 本地用户存储工具
    const getRegisteredUsers = () => {
      try {
        const raw = localStorage.getItem('users')
        return raw ? JSON.parse(raw) : []
      } catch (e) {
        return []
      }
    }
    const saveRegisteredUsers = (users) => {
      localStorage.setItem('users', JSON.stringify(users))
    }
    const addRegisteredUser = (username, password) => {
      const users = getRegisteredUsers()
      users.push({ username, password })
      saveRegisteredUsers(users)
    }

    // 检查后端服务状态
    const checkBackendStatus = async () => {
      // 如果设置了后端地址才检查，否则跳过
      const backendUrl = import.meta.env?.VITE_API_BASE || process.env.VUE_APP_API_BASE
      if (!backendUrl) {
        backendStatus.value = null // 不显示状态
        return
      }
      
      try {
        const response = await fetch(`${backendUrl}/api/auth/me`)
        backendStatus.value = response.ok
        if (response.ok) {
          message.success('后端服务连接正常')
        } else {
          message.error('后端服务响应异常')
        }
      } catch (error) {
        backendStatus.value = false
        message.error('无法连接到后端服务')
        
        // 捕获后端连接错误
        captureError(error, {
          component: 'Login',
          action: 'checkBackendStatus',
          context: '后端健康检查失败'
        })
      }
    }

    // 登录处理
    const onLoginFinish = (values) => {
      loading.value = true
      console.log('登录表单提交:', values)
      
      // 验证用户名和密码
      if (!values.username || !values.password) {
        message.error('请输入用户名和密码')
        loading.value = false
        return
      }
      
      // 检查用户端是否尝试登录管理员账户
      if (selectedSystem.value === 'user' && values.username === 'admin') {
        message.error('用户端无法登录管理员账户，请切换到管理端')
        loading.value = false
        return
      }
      
      // 检查管理员账户是否在管理端登录
      if (values.username === 'admin' && selectedSystem.value !== 'admin') {
        message.error('管理员账户只能在管理端登录')
        loading.value = false
        return
      }
      
      // 模拟登录验证 - 添加基本的用户名密码验证
      setTimeout(() => {
        loading.value = false
        
        if (selectedSystem.value === 'admin') {
          // 仅允许 admin/admin123
          if (values.username !== 'admin' || values.password !== 'admin123') {
            message.error('仅允许管理员账号登录（admin/admin123）')
            return
          }
        } else {
          // 用户端：必须是已注册用户
          const users = getRegisteredUsers()
          const found = users.find(u => u.username === values.username)
          if (!found) {
            message.error('该用户未注册，请先注册后再登录')
            return
          }
          if (found.password !== values.password) {
            message.error('密码错误')
            return
          }
        }
        
        // 通过验证后，保存用户信息到localStorage
        const userData = {
          username: values.username,
          password: values.password,
          avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${values.username}`,
          department: values.username === 'admin' ? '系统管理部' : '技术部',
          position: values.username === 'admin' ? '系统管理员' : '员工',
          bio: values.username === 'admin' ? '系统管理员账户' : '这是一个示例用户',
          employeeId: values.username === 'admin' ? 'ADMIN001' : 'EMP001'
        }
        
        localStorage.setItem('user', JSON.stringify(userData))
        localStorage.setItem('username', values.username)
        // 保存token，用于路由守卫验证
        localStorage.setItem('token', `token_${values.username}_${Date.now()}`)
        
        // 设置当前用户，用于数据隔离
        setCurrentUser(values.username)
        
        // 设置 Sentry 用户信息
        setUser(userData)
        
        // 添加登录成功面包屑
        addBreadcrumb({
          category: 'auth',
          message: '用户登录成功',
          level: 'info',
          data: {
            username: values.username,
            system: selectedSystem.value
          }
        })
        
        // 登录成功后清除recentRegisteredUser，避免干扰后续显示
        localStorage.removeItem('recentRegisteredUser')
        
        // 根据选择的系统类型跳转到不同页面
        if (selectedSystem.value === 'user') {
          router.push('/user')
          message.success('用户端登录成功！')
        } else {
          router.push('/')
          message.success('管理端登录成功！')
        }
      }, 400)
    }

    // 注册处理
    const onRegisterFinish = (values) => {
      // 检查是否在用户端
      if (selectedSystem.value === 'admin') {
        message.error('管理端不支持注册功能')
        return
      }
      
      loading.value = true
      console.log('注册表单提交:', values)
      
      // 验证表单数据
      if (!values.username || !values.password || !values.confirmPassword) {
        message.error('请填写完整的注册信息')
        loading.value = false
        return
      }
      
      if (values.password !== values.confirmPassword) {
        message.error('两次输入的密码不一致')
        loading.value = false
        return
      }
      
      // 模拟注册过程
      setTimeout(() => {
        loading.value = false
        
        // 检查是否已存在
        const users = getRegisteredUsers()
        if (users.some(u => u.username === values.username)) {
          message.error('用户名已被注册，请更换用户名')
          return
        }
        
        // 保存到用户列表
        addRegisteredUser(values.username, values.password)
        
        // 记录最近注册的用户（供未登录或刚注册后的各处默认显示）
        localStorage.setItem('recentRegisteredUser', JSON.stringify({
          username: values.username,
          avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${values.username}`
        }))
        
        message.success('注册成功！请使用该账号登录')
        // 注册成功后不自动登录，不设置token，返回登录表单
        isLogin.value = true
        Object.assign(loginForm, { username: values.username, password: '' })
        Object.assign(registerForm, { username: '', password: '', confirmPassword: '' })
      }, 400)
    }

    onMounted(() => {
      checkBackendStatus()
    })

    return {
      isLogin,
      loading,
      backendStatus,
      loginForm,
      registerForm,
      toggleForm,
      validateConfirmPassword,
      checkBackendStatus,
      onLoginFinish,
      onRegisterFinish,
      selectedSystem,
      handleSystemChange
    }
  }
})
</script>

<style scoped>
/* 背景：柔和渐变 + 暗角 */
.login-bg {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  /* 允许纵向滚动，防止内容被截断 */
  overflow-x: hidden;
  overflow-y: auto;
  background: radial-gradient(1200px 600px at 50% -20%, rgba(255,255,255,0.18), transparent),
              linear-gradient(135deg, #5563d6 0%, #7d5be6 50%, #a777e3 100%);
}

/* 容器与卡片 */
.login-container { width: 100vw; max-width: 1024px; display: grid; grid-template-columns: 1fr; gap: 16px; align-items: start; justify-items: center; padding: 28px 16px 56px; }
.login-card {
  /* 自适应：在 340px-460px 范围内随屏宽伸缩，始终居中协调 */
  width: clamp(340px, 92vw, 460px);
  background: rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 26px 24px 24px 24px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  /* 更柔和的阴影，减少突兀 */
  box-shadow: 0 12px 28px rgba(13, 16, 45, 0.22), inset 0 1px 0 rgba(255,255,255,0.06);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-sizing: border-box;
}

@media (max-width: 768px) { .login-card { width: 92vw; padding: 22px 18px; margin-bottom: 40px; } }

/* 头部标题与装饰 */
.logo-section { text-align: center; margin-bottom: 18px; }
.logo-icon { font-size: 42px; color: #e9e7ff; opacity: .95; text-shadow: 0 6px 18px rgba(0,0,0,.25); }
.system-title { font-size: 22px; font-weight: 700; color: #eef2ff; margin: 0; letter-spacing: .5px; }
.accent-underline { width: 120px; height: 3px; background: linear-gradient(90deg, rgba(255,255,255,.65), rgba(255,255,255,.2)); border-radius: 2px; margin: 10px auto 6px; opacity: .9; box-shadow: 0 0 12px rgba(255,255,255,.25); }
.subtitle { margin: 0; font-size: 12px; color: rgba(255,255,255,.8); }

/* 系统选择切换 */
.system-switch { margin-top: 10px; }
.switch-tabs { display: flex; background: rgba(255, 255, 255, 0.1); border-radius: 12px; overflow: hidden; border: 1px solid rgba(255, 255, 255, 0.2); }
.switch-tab {
  flex: 1;
  padding: 8px 12px;
  text-align: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.switch-tab.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-bottom: 2px solid #fff;
}
.switch-tab:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

/* 表单可读性优化：标签深色、输入深色文字、浅背景 */
:deep(.ant-form-item-label > label) { color: #334155 !important; font-weight: 600; }
:deep(.ant-input-affix-wrapper-lg), :deep(.ant-input-lg) { height: 44px; }
.form-input, :deep(.ant-input-affix-wrapper) {
  border-radius: 12px;
  background: rgba(255,255,255,0.92);
  border: 1px solid rgba(17,24,39,0.08);
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.5);
}
.form-input:hover, :deep(.ant-input-affix-wrapper:hover) {
  border-color: rgba(17,24,39,0.18);
  box-shadow: 0 0 0 3px rgba(167,139,250,0.18), inset 0 1px 0 rgba(255,255,255,0.6);
}

/* 自定义输入框样式 - 解决光标位置问题 */
.custom-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.92);
  border: 1px solid rgba(17,24,39,0.08);
  border-radius: 12px;
  padding: 0 12px;
  height: 44px;
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.5);
  transition: all 0.3s ease;
}

.custom-input-wrapper:hover {
  border-color: rgba(17,24,39,0.18);
  box-shadow: 0 0 0 3px rgba(167,139,250,0.18), inset 0 1px 0 rgba(255,255,255,0.6);
}

.custom-input-wrapper .input-icon {
  color: #6b7280;
  margin-right: 8px;
  font-size: 16px;
}

.custom-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #111827;
  line-height: 1.5;
  padding: 0;
  margin: 0;
  width: 100%;
}

.custom-input::placeholder {
  color: #6b7280;
  opacity: 1;
}

.custom-input:focus {
  outline: none;
}

/* 修复输入框光标位置问题 */
:deep(.ant-input) { 
  font-size: 14px; 
  color: #111827; 
  background: transparent; 
  text-align: left; 
  line-height: 1.5;
  padding: 0;
  border: none;
  outline: none;
  box-shadow: none;
}
:deep(.ant-input::placeholder) { 
  color: #6b7280; 
  text-align: left; 
  line-height: 1.5;
}
:deep(.ant-input-affix-wrapper .ant-input) {
  padding: 0;
  margin: 0;
  border: none;
  outline: none;
  box-shadow: none;
}
:deep(.ant-input-affix-wrapper) {
  padding: 0 11px;
}
.input-icon { color: #6b7280; }

/* 表单整体间距更统一 */
.login-form, .register-form { margin-bottom: 16px; }
.login-form :deep(.ant-form-item), .register-form :deep(.ant-form-item) { margin-bottom: 14px; }

/* 提示与校验信息更克制，避免拥挤 */
:deep(.ant-form-item-explain) { margin-top: 4px; }
:deep(.ant-form-item-explain div) { font-size: 12px; line-height: 18px; }

/* 主按钮与输入间距更自然 */
.login-button, .register-button { height: 46px; margin-top: 2px; }

/* 容器顶/底部留白更均衡 */
.login-container { padding: 28px 16px 56px; }

/* 外部服务状态：桌面隐藏，移动端显示 */
.info-section.minimal { display: none; }
@media (max-width: 768px) { .info-section.minimal { display: block; } }

/* 主按钮：与背景融合的渐变 */
.login-button, .register-button {
  height: 46px;
  border-radius: 12px;
  font-weight: 700;
  letter-spacing: .5px;
  background: linear-gradient(135deg, rgba(255,255,255,.35), rgba(255,255,255,.15));
  border: 1px solid rgba(255,255,255,.35);
  color: #101436;
  box-shadow: 0 10px 30px rgba(0,0,0,.25), inset 0 1px 0 rgba(255,255,255,.5);
}
.login-button:hover, .register-button:hover { filter: brightness(1.06); transform: translateY(-1px); }

/* 按钮下方的微型状态条（浅色）*/
.status-inline { display: flex; gap: 8px; align-items: center; justify-content: center; margin-top: -4px; margin-bottom: 8px; }
.status-dot.ok { color: #86efac; }
.status-dot.bad { color: #fca5a5; }
.status-label { font-size: 12px; color: rgba(255,255,255,.85); }
.status-label.ok { color: #10b981; font-weight: 600; }
.status-label.bad { color: #fecaca; }
.status-action { color: #fbbf24; font-size: 12px; font-weight: 600; text-decoration: underline; }
.status-action:hover { color: #f59e0b; text-decoration: none; }

/* 切换链接样式 */
.switch-link { 
  color: #3b82f6; 
  font-weight: 600; 
  text-decoration: underline; 
  transition: color 0.3s ease;
}
.switch-link:hover { 
  color: #1d4ed8; 
  text-decoration: none;
}

/* 轻量服务状态区域（与背景融合）*/
.info-section.minimal { text-align: center; }
.section-title { font-size: 14px; color: #eef2ff; margin: 6px 0 6px; }
.status-message { font-size: 12px; color: rgba(255,255,255,.86); margin: 0 0 8px; }
.status-message.error { color: #fecaca; }
.status-check-btn { font-size: 12px; background: rgba(255,255,255,.1); color: #fff; border-color: rgba(255,255,255,.3); }
.status-check-btn:hover { border-color: rgba(255,255,255,.5); }

/* 兼容宽度与移动端 */
.form-input, :deep(.ant-input), :deep(.ant-input-affix-wrapper) { width: 100%; max-width: 100%; box-sizing: border-box; }
@media (max-width: 768px) { .login-card { width: 92vw; padding: 22px 18px; } }
</style> 