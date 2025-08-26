<template>
  <div class="file-manager-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="folder" class="title-icon" />
            文件管理
          </h1>
          <p class="page-subtitle">管理系统文件、文档和资源共享</p>
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

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card total-files">
        <div class="stat-icon">
          <span class="icon-emoji">📁</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">1,234</div>
          <div class="stat-label">总文件数</div>
        </div>
      </div>
      <div class="stat-card total-size">
        <div class="stat-icon">
          <span class="icon-emoji">💾</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">45.6GB</div>
          <div class="stat-label">总存储量</div>
        </div>
      </div>
      <div class="stat-card uploaded-today">
        <div class="stat-icon">
          <span class="icon-emoji">📤</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">89</div>
          <div class="stat-label">今日上传</div>
        </div>
      </div>
      <div class="stat-card shared-files">
        <div class="stat-icon">
          <span class="icon-emoji">👥</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">156</div>
          <div class="stat-label">共享文件</div>
        </div>
      </div>
    </div>

    <!-- 文件列表 -->
    <div class="file-list">
      <div class="list-header">
        <h2>文件列表</h2>
        <button class="btn-upload" @click="openUploadModal">+ 上传文件</button>
      </div>
      
      <div class="file-table">
        <table>
          <thead>
            <tr>
              <th>文件名</th>
              <th>类型</th>
              <th>大小</th>
              <th>上传人</th>
              <th>上传时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="file in fileList" :key="file.id">
              <td>
                <div class="file-info">
                  <span class="file-icon">{{ file.icon }}</span>
                  <span class="file-name">{{ file.name }}</span>
                </div>
              </td>
              <td>{{ file.type }}</td>
              <td>{{ file.size }}</td>
              <td>
                <div class="uploader-info">
                  <img :src="file.uploaderAvatar" :alt="file.uploader" class="avatar">
                  <span>{{ file.uploader }}</span>
                </div>
              </td>
              <td>{{ file.uploadTime }}</td>
              <td>
                <span class="status" :class="file.status">{{ file.statusText }}</span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-download" @click="downloadFile(file)">下载</button>
                  <button class="btn-delete" @click="showDeleteConfirm(file)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 上传文件模态框 -->
    <div v-if="showUploadModal" class="modal-overlay" @click="closeUploadModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>上传文件</h3>
          <button class="modal-close" @click="closeUploadModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitUpload">
            <div class="form-group">
              <label>选择文件 <span class="required">*</span></label>
              <div class="file-upload-area" @click="triggerFileInput" @drop="handleFileDrop" @dragover.prevent @dragenter.prevent>
                <div v-if="!selectedFile" class="upload-placeholder">
                  <span class="upload-icon">📁</span>
                  <p>点击选择文件或拖拽文件到此处</p>
                  <p class="upload-hint">支持所有文件类型，最大100MB</p>
                </div>
                <div v-else class="selected-file">
                  <span class="file-icon">{{ getFileIcon(selectedFile.name) }}</span>
                  <div class="file-details">
                    <div class="file-name">{{ selectedFile.name }}</div>
                    <div class="file-info">{{ formatFileSize(selectedFile.size) }} • {{ getFileType(selectedFile.name) }}</div>
                  </div>
                  <button type="button" class="remove-file" @click="removeSelectedFile">✕</button>
                </div>
              </div>
              <input 
                ref="fileInput"
                type="file" 
                @change="handleFileSelect"
                accept="*/*"
                style="display: none;"
              >
              <div v-if="formErrors.file" class="form-error">{{ formErrors.file }}</div>
            </div>
            
            <div class="form-group">
              <label>文件描述 <span class="optional">(可选)</span></label>
              <textarea 
                v-model="uploadForm.description" 
                placeholder="请输入文件描述，帮助其他用户了解文件内容..."
                rows="3"
                maxlength="200"
              ></textarea>
              <div class="char-count">{{ uploadForm.description.length }}/200</div>
            </div>

            <div class="form-group">
              <label>文件分类</label>
              <select v-model="uploadForm.category">
                <option value="">请选择分类</option>
                <option value="document">文档类</option>
                <option value="image">图片类</option>
                <option value="video">视频类</option>
                <option value="audio">音频类</option>
                <option value="archive">压缩包</option>
                <option value="other">其他</option>
              </select>
            </div>

            <div class="form-group">
              <label>访问权限</label>
              <select v-model="uploadForm.access">
                <option value="public">公开</option>
                <option value="private">私有</option>
                <option value="shared">共享</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeUploadModal">取消</button>
          <button type="button" class="btn-submit" @click="submitUpload" :disabled="!selectedFile || uploading">
            <span v-if="uploading" class="loading-spinner"></span>
            {{ uploading ? '上传中...' : '上传文件' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="modal-close" @click="closeDeleteModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>确定要删除文件 <strong>{{ fileToDelete?.name }}</strong> 吗？</p>
          <p>此操作无法撤销。</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeDeleteModal">取消</button>
          <button type="button" class="btn-delete-confirm" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'FileManager',
  setup() {
    const router = useRouter()
    
    // 文件输入引用
    const fileInput = ref(null)
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=FileManager1'
    })
    
    // 文件数据
    const fileList = ref([
      {
        id: 'FILE001',
        name: '项目计划书.docx',
        icon: '📄',
        type: 'Word文档',
          size: '2.5MB',
        uploader: '张三',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File1',
        uploadTime: '2024-01-15 10:30:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE002',
        name: '财务报表.xlsx',
        icon: '📊',
        type: 'Excel表格',
        size: '1.8MB',
        uploader: '李四',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File2',
        uploadTime: '2024-01-15 09:15:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE003',
        name: '产品设计图.psd',
        icon: '🎨',
        type: 'Photoshop文件',
        size: '15.2MB',
        uploader: '王五',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File3',
        uploadTime: '2024-01-14 16:45:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE004',
        name: '会议纪要.pdf',
        icon: '📋',
        type: 'PDF文档',
        size: '3.2MB',
        uploader: '赵六',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File4',
        uploadTime: '2024-01-14 14:20:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE005',
        name: '员工手册.docx',
        icon: '📄',
        type: 'Word文档',
        size: '5.7MB',
        uploader: '钱七',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File5',
        uploadTime: '2024-01-14 11:45:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE006',
        name: '销售数据.xlsx',
        icon: '📊',
        type: 'Excel表格',
        size: '2.1MB',
        uploader: '孙八',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File6',
        uploadTime: '2024-01-14 09:30:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE007',
        name: '网站设计稿.fig',
        icon: '🎨',
        type: 'Figma文件',
        size: '8.9MB',
        uploader: '周九',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File7',
        uploadTime: '2024-01-13 17:15:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE008',
        name: '技术规范.pdf',
        icon: '📋',
        type: 'PDF文档',
        size: '4.3MB',
        uploader: '吴十',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File8',
        uploadTime: '2024-01-13 15:40:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE009',
        name: '客户名单.xlsx',
        icon: '📊',
        type: 'Excel表格',
          size: '1.2MB',
        uploader: '郑十一',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File9',
        uploadTime: '2024-01-13 13:25:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE010',
        name: '产品宣传片.mp4',
        icon: '🎬',
        type: '视频文件',
        size: '45.8MB',
        uploader: '王十二',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File10',
        uploadTime: '2024-01-13 10:50:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE011',
        name: '年度报告.pdf',
        icon: '📋',
        type: 'PDF文档',
        size: '12.7MB',
        uploader: '李十三',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File11',
        uploadTime: '2024-01-12 16:30:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE012',
        name: '培训材料.pptx',
        icon: '📽️',
        type: 'PowerPoint',
        size: '6.8MB',
        uploader: '张十四',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File12',
        uploadTime: '2024-01-12 14:15:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE013',
        name: '数据库备份.sql',
        icon: '🗄️',
        type: 'SQL文件',
        size: '2.3MB',
        uploader: '刘十五',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File13',
        uploadTime: '2024-01-12 11:20:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE014',
        name: '合同模板.docx',
        icon: '📄',
        type: 'Word文档',
        size: '3.9MB',
        uploader: '陈十六',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File14',
        uploadTime: '2024-01-12 09:45:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE015',
        name: '库存清单.xlsx',
        icon: '📊',
        type: 'Excel表格',
        size: '1.6MB',
        uploader: '杨十七',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File15',
        uploadTime: '2024-01-11 17:10:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE016',
        name: 'Logo设计.ai',
        icon: '🎨',
        type: 'Illustrator',
        size: '7.4MB',
        uploader: '黄十八',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File16',
        uploadTime: '2024-01-11 15:35:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE017',
        name: '系统日志.txt',
        icon: '📝',
        type: '文本文件',
        size: '0.8MB',
        uploader: '赵十九',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File17',
        uploadTime: '2024-01-11 13:50:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE018',
        name: '项目预算.xlsx',
        icon: '📊',
        type: 'Excel表格',
        size: '2.8MB',
        uploader: '吴二十',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File18',
        uploadTime: '2024-01-11 11:25:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE019',
        name: '用户手册.pdf',
        icon: '📋',
        type: 'PDF文档',
        size: '9.1MB',
        uploader: '徐二一',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File19',
        uploadTime: '2024-01-10 16:40:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE020',
        name: '代码规范.md',
        icon: '📝',
        type: 'Markdown',
        size: '0.5MB',
        uploader: '孙二二',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File20',
        uploadTime: '2024-01-10 14:55:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE021',
        name: '产品图片.jpg',
        icon: '🖼️',
        type: '图片文件',
        size: '3.7MB',
        uploader: '马二三',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File21',
        uploadTime: '2024-01-10 12:30:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE022',
        name: '会议录音.mp3',
        icon: '🎵',
        type: '音频文件',
        size: '18.5MB',
        uploader: '朱二四',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File22',
        uploadTime: '2024-01-10 10:15:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE023',
        name: '工作流程.pdf',
        icon: '📋',
        type: 'PDF文档',
        size: '5.2MB',
        uploader: '胡二五',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File23',
        uploadTime: '2024-01-09 17:25:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE024',
        name: '数据分析.xlsx',
        icon: '📊',
        type: 'Excel表格',
        size: '4.1MB',
        uploader: '郭二六',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File24',
        uploadTime: '2024-01-09 15:40:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE025',
        name: '设计方案.sketch',
        icon: '🎨',
        type: 'Sketch文件',
        size: '22.3MB',
        uploader: '何二七',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File25',
        uploadTime: '2024-01-09 13:55:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE026',
        name: '测试报告.docx',
        icon: '📄',
        type: 'Word文档',
        size: '2.9MB',
        uploader: '高二八',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File26',
        uploadTime: '2024-01-09 11:20:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE027',
        name: '配置文件.yaml',
        icon: '⚙️',
        type: '配置文件',
        size: '0.3MB',
        uploader: '林二九',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File27',
        uploadTime: '2024-01-09 09:35:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE028',
        name: '演示文稿.pptx',
        icon: '📽️',
        type: 'PowerPoint',
        size: '8.7MB',
        uploader: '罗三十',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File28',
        uploadTime: '2024-01-08 16:50:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE029',
        name: '源代码.zip',
        icon: '📦',
        type: '压缩文件',
        size: '35.6MB',
        uploader: '梁三一',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File29',
        uploadTime: '2024-01-08 14:25:00',
        status: 'active',
        statusText: '正常'
      },
      {
        id: 'FILE030',
        name: '项目总结.docx',
        icon: '📄',
        type: 'Word文档',
        size: '4.8MB',
        uploader: '谢三二',
        uploaderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=File30',
        uploadTime: '2024-01-08 12:10:00',
        status: 'active',
        statusText: '正常'
      }
    ])

    // 模态框状态
    const showUploadModal = ref(false)
    const showDeleteModal = ref(false)
    const selectedFile = ref(null)
    const fileToDelete = ref(null)
    const uploadForm = ref({
      description: '',
      category: '',
      access: 'public'
    })

    // 上传状态
    const uploading = ref(false)
    const formErrors = ref({
      file: ''
    })

    // 返回首页
    const goHome = () => {
      router.push('/')
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

    // 打开上传文件模态框
    const openUploadModal = () => {
      showUploadModal.value = true
      selectedFile.value = null
      uploadForm.value = {
        description: '',
        category: '',
        access: 'public'
      }
      formErrors.value.file = ''
    }

    // 关闭上传文件模态框
    const closeUploadModal = () => {
      showUploadModal.value = false
      selectedFile.value = null
      uploadForm.value = {
        description: '',
        category: '',
        access: 'public'
      }
      formErrors.value.file = ''
    }

    // 触发文件选择
    const triggerFileInput = () => {
      fileInput.value.click()
    }

    // 处理文件选择
    const handleFileSelect = (event) => {
      const file = event.target.files[0]
      if (file) {
        validateAndSetFile(file)
      }
    }

    // 处理拖拽上传
    const handleFileDrop = (event) => {
      event.preventDefault()
      const files = event.dataTransfer.files
      if (files.length > 0) {
        validateAndSetFile(files[0])
      }
    }

    // 验证并设置文件
    const validateAndSetFile = (file) => {
      // 检查文件大小 (100MB限制)
      const maxSize = 100 * 1024 * 1024
      if (file.size > maxSize) {
        formErrors.value.file = '文件大小不能超过100MB'
        return
      }

      // 检查文件类型
      const allowedTypes = [
        'image/', 'video/', 'audio/', 'text/', 'application/',
        'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument',
        'application/vnd.ms-excel', 'application/zip', 'application/x-rar-compressed'
      ]
      
      const isValidType = allowedTypes.some(type => file.type.startsWith(type))
      if (!isValidType && !file.name.includes('.')) {
        formErrors.value.file = '不支持的文件类型'
        return
      }

      selectedFile.value = file
      formErrors.value.file = ''
      
      // 自动设置分类
      if (file.type.startsWith('image/')) {
        uploadForm.value.category = 'image'
      } else if (file.type.startsWith('video/')) {
        uploadForm.value.category = 'video'
      } else if (file.type.startsWith('audio/')) {
        uploadForm.value.category = 'audio'
      } else if (file.type.includes('zip') || file.type.includes('rar')) {
        uploadForm.value.category = 'archive'
      } else if (file.type.includes('pdf') || file.type.includes('word') || file.type.includes('excel')) {
        uploadForm.value.category = 'document'
      } else {
        uploadForm.value.category = 'other'
      }
    }

    // 移除选中的文件
    const removeSelectedFile = () => {
      selectedFile.value = null
      formErrors.value.file = ''
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    // 提交上传
    const submitUpload = () => {
      if (!selectedFile.value) {
        formErrors.value.file = '请选择要上传的文件'
        return
      }

      // 创建新文件记录
      const newFile = {
        id: `FILE${String(fileList.value.length + 1).padStart(3, '0')}`,
        name: selectedFile.value.name,
        icon: getFileIcon(selectedFile.value.name),
        type: getFileType(selectedFile.value.name),
        size: formatFileSize(selectedFile.value.size),
        uploader: userInfo.value.username || '当前用户',
        uploaderAvatar: userInfo.value.avatar,
        uploadTime: new Date().toLocaleString('zh-CN'),
        status: 'active',
        statusText: '正常'
      }

      // 添加到文件列表顶部
      fileList.value.unshift(newFile)
      
      closeUploadModal()
      alert('文件上传成功！')
    }

    // 获取文件图标
    const getFileIcon = (fileName) => {
      const ext = fileName.toLowerCase().split('.').pop()
      const iconMap = {
        'pdf': '📋',
        'doc': '📄', 'docx': '📄',
        'xls': '📊', 'xlsx': '📊',
        'ppt': '📽️', 'pptx': '📽️',
        'jpg': '🖼️', 'jpeg': '🖼️', 'png': '🖼️', 'gif': '🖼️',
        'mp4': '🎬', 'avi': '🎬', 'mov': '🎬',
        'mp3': '🎵', 'wav': '🎵',
        'zip': '📦', 'rar': '📦', '7z': '📦',
        'psd': '🎨', 'ai': '🎨', 'sketch': '🎨', 'fig': '🎨',
        'sql': '🗄️',
        'md': '📝', 'txt': '📝',
        'yaml': '⚙️', 'yml': '⚙️', 'json': '⚙️'
      }
      return iconMap[ext] || '📄'
    }

    // 获取文件类型
    const getFileType = (fileName) => {
      const ext = fileName.toLowerCase().split('.').pop()
      const typeMap = {
        'pdf': 'PDF文档',
        'doc': 'Word文档', 'docx': 'Word文档',
        'xls': 'Excel表格', 'xlsx': 'Excel表格',
        'ppt': 'PowerPoint', 'pptx': 'PowerPoint',
        'jpg': '图片文件', 'jpeg': '图片文件', 'png': '图片文件', 'gif': '图片文件',
        'mp4': '视频文件', 'avi': '视频文件', 'mov': '视频文件',
        'mp3': '音频文件', 'wav': '音频文件',
        'zip': '压缩文件', 'rar': '压缩文件', '7z': '压缩文件',
        'psd': 'Photoshop文件', 'ai': 'Illustrator', 'sketch': 'Sketch文件', 'fig': 'Figma文件',
        'sql': 'SQL文件',
        'md': 'Markdown', 'txt': '文本文件',
        'yaml': '配置文件', 'yml': '配置文件', 'json': '配置文件'
      }
      return typeMap[ext] || '未知文件'
    }

    // 格式化文件大小
    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + sizes[i]
    }

    // 下载文件
    const downloadFile = (file) => {
      // 创建下载链接
      const link = document.createElement('a')
      link.href = `data:text/plain;charset=utf-8,${encodeURIComponent(file.name)}`
      link.download = file.name
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      alert(`开始下载文件：${file.name}`)
    }

    // 显示删除确认
    const showDeleteConfirm = (file) => {
      fileToDelete.value = file
      showDeleteModal.value = true
    }

    // 关闭删除确认模态框
    const closeDeleteModal = () => {
      showDeleteModal.value = false
      fileToDelete.value = null
    }

    // 确认删除
    const confirmDelete = () => {
      if (fileToDelete.value) {
        const index = fileList.value.findIndex(file => file.id === fileToDelete.value.id)
        if (index !== -1) {
          fileList.value.splice(index, 1)
          closeDeleteModal()
          alert('文件已删除')
        }
      }
    }

    // 监听用户信息更新事件，实现实时同步
    const handleUserInfoUpdate = () => {
      getUserInfo() // 重新加载用户信息
    }

    onMounted(() => {
      getUserInfo()
      // 监听用户信息更新事件
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    onUnmounted(() => {
      // 清理事件监听器
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
    })

    return {
      userInfo,
      fileList,
      goHome,
      showUploadModal,
      openUploadModal,
      closeUploadModal,
      triggerFileInput,
      handleFileSelect,
      handleFileDrop,
      removeSelectedFile,
      submitUpload,
      downloadFile,
      showDeleteConfirm,
      closeDeleteModal,
      confirmDelete,
      selectedFile,
      uploadForm,
      formErrors,
      uploading,
      showDeleteModal,
      fileToDelete,
      fileInput,
      getFileIcon,
      getFileType,
      formatFileSize
    }
  }
})
</script>

<style scoped>
.file-manager-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
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

.upload-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  height: 48px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.6);
  backdrop-filter: blur(20px);
  transition: all 0.3s ease;
  text-align: center;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

/* 不同卡片的图标背景色 */
.total-files .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.total-size .stat-icon {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.uploaded-today .stat-icon {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
}

.shared-files .stat-icon {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  font-weight: 600;
}

.table-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.table-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  border-radius: 8px;
}

.refresh-btn {
  border-radius: 8px;
  height: 36px;
}

.file-table {
  border-radius: 8px;
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.file-icon-pdf {
  color: #ff4d4f;
  font-size: 18px;
}

.file-icon-word {
  color: #1890ff;
  font-size: 18px;
}

.file-icon-excel {
  color: #52c41a;
  font-size: 18px;
}

.file-icon-ppt {
  color: #fa8c16;
  font-size: 18px;
}

.file-icon-image {
  color: #722ed1;
  font-size: 18px;
}

.file-icon-video {
  color: #13c2c2;
  font-size: 18px;
}

.file-icon-audio {
  color: #eb2f96;
  font-size: 18px;
}

.file-icon-archive {
  color: #2f54eb;
  font-size: 18px;
}

.file-icon-default {
  color: #666;
  font-size: 18px;
}

.file-type-tag {
  border-radius: 20px;
  padding: 4px 12px;
  font-weight: 500;
}

.file-size {
  font-family: 'Courier New', monospace;
  font-weight: 500;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.download-btn, .delete-btn {
  border-radius: 6px;
  height: 28px;
  font-size: 12px;
}

.download-btn {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
}

.delete-btn {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
}

/* 文件列表样式 */
.file-list {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
}

.list-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-header h2::before {
  content: '📋';
  font-size: 28px;
}

.btn-upload {
  padding: 12px 24px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-upload::before {
  content: '📤';
  font-size: 18px;
}

.btn-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.file-table {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow-x: auto;
  border-radius: 16px;
  overflow: hidden;
}

.file-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.file-table th,
.file-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.file-table th {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #e2e8f0;
  padding: 20px 16px;
}

.file-table tr {
  transition: all 0.2s ease;
}

.file-table tr:hover {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-icon {
  font-size: 20px;
}

.file-name {
  font-weight: 500;
  color: #1e293b;
}

.uploader-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.uploader-info .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.uploader-info span {
  font-weight: 500;
  color: #1e293b;
}

.status {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: inline-block;
  text-align: center;
  min-width: 80px;
}

.status.active {
  background: #dcfce7;
  color: #166534;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-download, .btn-delete {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-download {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
}

.btn-download:hover {
  background: linear-gradient(135deg, #389e0d 0%, #52c41a 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.btn-delete {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.btn-delete:hover {
  background: linear-gradient(135deg, #cf1322 0%, #ff4d4f 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.btn-download::before {
  content: '⬇️';
  font-size: 14px;
}

.btn-delete::before {
  content: '🗑️';
  font-size: 14px;
}



/* 文件上传区域样式 */
.file-upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9fafb;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-upload-area:hover {
  border-color: #3b82f6;
  background: #f0f9ff;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  font-size: 48px;
  color: #6b7280;
}

.upload-placeholder p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.upload-hint {
  font-size: 12px;
  color: #9ca3af;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  width: 100%;
}

.file-icon {
  font-size: 32px;
}

.file-details {
  flex: 1;
  text-align: left;
}

.file-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.file-info {
  font-size: 12px;
  color: #6b7280;
}

.remove-file {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.remove-file:hover {
  background: #fecaca;
  transform: scale(1.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.required {
  color: #dc2626;
}

.optional {
  color: #6b7280;
  font-size: 12px;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 0;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0 24px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 0 24px 24px 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #374151;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-error {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  margin-top: 24px;
}

.btn-submit,
.btn-cancel {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-submit {
  background: #3b82f6;
  color: white;
}

.btn-submit:hover {
  background: #2563eb;
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.delete-modal .modal-content {
  max-width: 400px;
}

.delete-modal .modal-body {
  text-align: center;
  padding: 32px 24px;
}

.delete-modal .modal-footer {
  justify-content: center;
}

.btn-delete-confirm {
  background: #dc2626;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-delete-confirm:hover {
  background: #b91c1c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .file-manager-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .stats-section .ant-col {
    margin-bottom: 16px;
  }
  
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

/* 上传人头像样式 */
.uploader-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uploader-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.uploader-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
</style> 