<template>
  <div class="my-files-container">
    <!-- 通用头部 -->
    <CommonHeader 
      title="我的文件"
      subtitle="管理您的个人文件和文档"
      icon-type="folder"
      :isUserSide="true"
    />
    
    <div class="main-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="content-area">
        <!-- 文件统计卡片 -->
        <div class="stats-section">
          <div class="stat-card">
            <div class="stat-icon">📁</div>
            <div class="stat-content">
              <div class="stat-number">{{ fileStats.totalFiles }}</div>
              <div class="stat-label">总文件数</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">💾</div>
            <div class="stat-content">
              <div class="stat-number">{{ formatFileSize(fileStats.totalSize) }}</div>
              <div class="stat-label">总大小</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">📤</div>
            <div class="stat-content">
              <div class="stat-number">{{ fileStats.recentUploads }}</div>
              <div class="stat-label">最近上传</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">🔒</div>
            <div class="stat-content">
              <div class="stat-number">{{ fileStats.privateFiles }}</div>
              <div class="stat-label">私有文件</div>
            </div>
          </div>
        </div>
        
        <!-- 文件管理区域 -->
        <div class="file-management-section">
          <div class="management-header">
            <div class="left-actions">
              <a-button type="primary" @click="showUploadModal = true" class="action-btn">
                <template #icon><UploadOutlined /></template>
                上传文件
              </a-button>
              <a-button @click="showCreateFolderModal = true" class="action-btn">
                <template #icon><FolderAddOutlined /></template>
                新建文件夹
              </a-button>
            </div>
            
            <div class="right-actions">
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索文件..."
                style="width: 200px; margin-right: 16px;"
                @search="handleSearch"
              />
              <a-select v-model:value="viewMode" style="width: 120px;">
                <a-select-option value="grid">网格视图</a-select-option>
                <a-select-option value="list">列表视图</a-select-option>
              </a-select>
            </div>
          </div>
          
          <!-- 文件列表 -->
          <div class="file-list" :class="viewMode">
            <div 
              v-for="file in filteredFiles" 
              :key="file.id" 
              class="file-item"
              @click="handleFileClick(file)"
            >
              <div class="file-icon">
                {{ getFileIcon(file.type) }}
              </div>
              <div class="file-info">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-meta">
                  <span class="file-size">{{ formatFileSize(file.size) }}</span>
                  <span class="file-date">{{ formatDate(file.uploadTime) }}</span>
                </div>
              </div>
              <div class="file-actions">
                <a-button 
                  type="text" 
                  size="small" 
                  @click.stop="downloadFile(file)"
                  class="action-btn"
                >
                  <template #icon><DownloadOutlined /></template>
                </a-button>
                <a-button 
                  type="text" 
                  size="small" 
                  @click.stop="showEditModalHandler(file)"
                  class="action-btn"
                >
                  <template #icon><EditOutlined /></template>
                </a-button>
                <a-button 
                  type="text" 
                  size="small" 
                  @click.stop="deleteFile(file)"
                  class="action-btn"
                  danger
                >
                  <template #icon><DeleteOutlined /></template>
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 上传文件模态框 -->
    <a-modal
      v-model:open="showUploadModal"
      title="上传文件"
      @ok="handleUpload"
      @cancel="showUploadModal = false"
      :confirm-loading="uploading"
    >
      <a-upload
        v-model:file-list="uploadFileList"
        :before-upload="beforeUpload"
        :multiple="true"
        :show-upload-list="true"
      >
        <a-button>
          <template #icon><UploadOutlined /></template>
          选择文件
        </a-button>
      </a-upload>
      <div class="upload-tips">
        <p>支持的文件类型：文档、图片、视频、音频等</p>
        <p>单个文件大小限制：50MB</p>
      </div>
    </a-modal>
    
    <!-- 新建文件夹模态框 -->
    <a-modal
      v-model:open="showCreateFolderModal"
      title="新建文件夹"
      @ok="handleCreateFolder"
      @cancel="showCreateFolderModal = false"
    >
      <a-form :model="folderForm" layout="vertical">
        <a-form-item label="文件夹名称" name="name">
          <a-input v-model:value="folderForm.name" placeholder="请输入文件夹名称" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="folderForm.description" placeholder="请输入文件夹描述" />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 编辑文件模态框 -->
    <a-modal
      v-model:open="showEditModal"
      title="编辑文件信息"
      @ok="handleEditSubmit"
      @cancel="showEditModal = false"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="文件名称" name="name">
          <a-input v-model:value="editForm.name" placeholder="请输入文件名称" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="editForm.description" placeholder="请输入文件描述" />
        </a-form-item>
        <a-form-item label="标签" name="tags">
          <a-select
            v-model:value="editForm.tags"
            mode="tags"
            placeholder="请输入标签"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 删除确认模态框 -->
    <a-modal
      v-model:open="showDeleteModal"
      title="确认删除"
      @ok="handleDeleteConfirm"
      @cancel="showDeleteModal = false"
    >
      <p>确定要删除文件 "{{ fileToDelete?.name }}" 吗？此操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UploadOutlined,
  FolderAddOutlined,
  DownloadOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'

// 响应式数据
const showUploadModal = ref(false)
const showCreateFolderModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const uploading = ref(false)
const searchKeyword = ref('')
const viewMode = ref('grid')
const currentPage = ref(1)
const fileToDelete = ref(null)

// 文件统计
const fileStats = reactive({
  totalFiles: 0,
  totalSize: 0,
  recentUploads: 0,
  privateFiles: 0
})

// 文件列表
const files = ref([])
const uploadFileList = ref([])

// 表单数据
const folderForm = reactive({
  name: '',
  description: ''
})

const editForm = reactive({
  id: '',
  name: '',
  description: '',
  tags: []
})

// 计算属性
const filteredFiles = computed(() => {
  let result = files.value
  
  if (searchKeyword.value) {
    result = result.filter(file => 
      file.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      file.description?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  return result
})

// 方法
const loadFiles = () => {
  // 模拟加载文件数据
  const mockFiles = [
    {
      id: 1,
      name: '项目计划书.docx',
      type: 'document',
      size: 1024 * 1024 * 2.5,
      uploadTime: new Date('2024-01-15'),
      description: '2024年第一季度项目计划',
      tags: ['项目', '计划', '文档'],
      isPrivate: false
    },
    {
      id: 2,
      name: '会议记录.pdf',
      type: 'document',
      size: 1024 * 512,
      uploadTime: new Date('2024-01-14'),
      description: '团队周会会议记录',
      tags: ['会议', '记录'],
      isPrivate: false
    },
    {
      id: 3,
      name: '产品设计图.psd',
      type: 'image',
      size: 1024 * 1024 * 15,
      uploadTime: new Date('2024-01-13'),
      description: '新产品UI设计稿',
      tags: ['设计', 'UI', '产品'],
      isPrivate: true
    },
    {
      id: 4,
      name: '演示视频.mp4',
      type: 'video',
      size: 1024 * 1024 * 45,
      uploadTime: new Date('2024-01-12'),
      description: '产品功能演示视频',
      tags: ['视频', '演示', '产品'],
      isPrivate: false
    },
    {
      id: 5,
      name: '数据报表.xlsx',
      type: 'spreadsheet',
      size: 1024 * 256,
      uploadTime: new Date('2024-01-11'),
      description: '月度销售数据报表',
      tags: ['数据', '报表', '销售'],
      isPrivate: false
    },
    {
      id: 6,
      name: '代码文件.zip',
      type: 'archive',
      size: 1024 * 1024 * 8,
      uploadTime: new Date('2024-01-10'),
      description: '前端代码压缩包',
      tags: ['代码', '前端', '开发'],
      isPrivate: true
    }
  ]
  
  files.value = mockFiles
  updateFileStats()
}

const updateFileStats = () => {
  const totalFiles = files.value.length
  const totalSize = files.value.reduce((sum, file) => sum + file.size, 0)
  const recentUploads = files.value.filter(file => {
    const daysDiff = (new Date() - file.uploadTime) / (1000 * 60 * 60 * 24)
    return daysDiff <= 7
  }).length
  const privateFiles = files.value.filter(file => file.isPrivate).length
  
  fileStats.totalFiles = totalFiles
  fileStats.totalSize = totalSize
  fileStats.recentUploads = recentUploads
  fileStats.privateFiles = privateFiles
}

const getFileIcon = (type) => {
  const icons = {
    document: '📄',
    image: '🖼️',
    video: '🎥',
    audio: '🎵',
    spreadsheet: '📊',
    presentation: '📽️',
    archive: '📦',
    code: '💻',
    pdf: '📕',
    folder: '📁'
  }
  return icons[type] || '📄'
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFileClick = (file) => {
  if (file.type === 'folder') {
    // 进入文件夹逻辑
    message.info(`进入文件夹: ${file.name}`)
  } else {
    // 预览文件逻辑
    message.info(`预览文件: ${file.name}`)
  }
}

const beforeUpload = (file) => {
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    message.error('文件大小不能超过50MB!')
    return false
  }
  return true
}

const pushActivity = (detail) => {
  try {
    window.dispatchEvent(new CustomEvent('userActivity', { detail }))
    const key = 'userActivities'
    const raw = localStorage.getItem(key)
    const list = raw ? JSON.parse(raw) : []
    list.unshift({
      id: Date.now(),
      type: detail.type || 'file',
      icon: 'folder',
      title: detail.title || '文件操作',
      time: detail.time || new Date().toTimeString().slice(0,5),
      status: detail.status || 'completed',
      statusText: detail.statusText || '已完成'
    })
    localStorage.setItem(key, JSON.stringify(list.slice(0,20)))
  } catch (e) {
    // ignore storage errors
  }
}

const handleUpload = async () => {
  if (uploadFileList.value.length === 0) {
    message.warning('请选择要上传的文件')
    return
  }
  
  uploading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟上传成功
    const newFiles = uploadFileList.value.map((file, index) => ({
      id: Date.now() + index,
      name: file.name,
      type: getFileType(file.name),
      size: file.size || 1024 * 1024,
      uploadTime: new Date(),
      description: '',
      tags: [],
      isPrivate: false
    }))
    
    files.value.unshift(...newFiles)
    updateFileStats()
    pushActivity({ type: 'file', title: '上传文件', status: 'completed', statusText: '已完成' })
    
    message.success('文件上传成功！')
    showUploadModal.value = false
    uploadFileList.value = []
  } catch (error) {
    message.error('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

const getFileType = (filename) => {
  const ext = filename.split('.').pop().toLowerCase()
  const typeMap = {
    'doc': 'document', 'docx': 'document', 'pdf': 'pdf',
    'jpg': 'image', 'jpeg': 'image', 'png': 'image', 'gif': 'image',
    'mp4': 'video', 'avi': 'video', 'mov': 'video',
    'mp3': 'audio', 'wav': 'audio', 'flac': 'audio',
    'xls': 'spreadsheet', 'xlsx': 'spreadsheet',
    'ppt': 'presentation', 'pptx': 'presentation',
    'zip': 'archive', 'rar': 'archive', '7z': 'archive',
    'js': 'code', 'ts': 'code', 'vue': 'code', 'html': 'code', 'css': 'code'
  }
  return typeMap[ext] || 'document'
}

const handleCreateFolder = async () => {
  if (!folderForm.name.trim()) {
    message.warning('请输入文件夹名称')
    return
  }
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newFolder = {
      id: Date.now(),
      name: folderForm.name,
      type: 'folder',
      size: 0,
      uploadTime: new Date(),
      description: folderForm.description,
      tags: [],
      isPrivate: false
    }
    
    files.value.unshift(newFolder)
    updateFileStats()
    pushActivity({ type: 'file', title: '新建文件夹', status: 'completed', statusText: '已完成' })
    
    message.success('文件夹创建成功！')
    showCreateFolderModal.value = false
    folderForm.name = ''
    folderForm.description = ''
  } catch (error) {
    message.error('创建失败，请重试')
  }
}

const showEditModalHandler = (file) => {
  editForm.id = file.id
  editForm.name = file.name
  editForm.description = file.description || ''
  editForm.tags = file.tags || []
  showEditModal.value = true
}

const handleEditSubmit = async () => {
  if (!editForm.name.trim()) {
    message.warning('请输入文件名称')
    return
  }
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const fileIndex = files.value.findIndex(f => f.id === editForm.id)
    if (fileIndex !== -1) {
      files.value[fileIndex].name = editForm.name
      files.value[fileIndex].description = editForm.description
      files.value[fileIndex].tags = editForm.tags
      
      pushActivity({ type: 'file', title: '更新文件信息', status: 'completed', statusText: '已完成' })
      message.success('文件信息更新成功！')
      showEditModal.value = false
    }
  } catch (error) {
    message.error('更新失败，请重试')
  }
}

const deleteFile = (file) => {
  fileToDelete.value = file
  showDeleteModal.value = true
}

const handleDeleteConfirm = async () => {
  if (!fileToDelete.value) return
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const fileIndex = files.value.findIndex(f => f.id === fileToDelete.value.id)
    if (fileIndex !== -1) {
      files.value.splice(fileIndex, 1)
      updateFileStats()
      
      pushActivity({ type: 'file', title: '删除文件', status: 'completed', statusText: '已完成' })
      message.success('文件删除成功！')
      showDeleteModal.value = false
      fileToDelete.value = null
    }
  } catch (error) {
    message.error('删除失败，请重试')
  }
}

const downloadFile = (file) => {
  message.info(`开始下载文件: ${file.name}`)
  // 这里可以实现实际的下载逻辑
}

// 生命周期
onMounted(() => {
  loadFiles()
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.my-files-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.main-content {
  display: flex;
  padding: 20px;
  gap: 20px;
  margin-top: 0;
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 统计卡片样式 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

/* 文件管理区域样式 */
.file-management-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.left-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  height: 40px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 文件列表样式 */
.file-list {
  margin-bottom: 24px;
}

.file-list.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.file-list.list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.file-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.file-icon {
  font-size: 32px;
  margin-right: 16px;
  flex-shrink: 0;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7f8c8d;
}

.file-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.action-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.action-icon:hover {
  background: #e3f2fd;
  color: #1976d2;
}

/* 分页样式 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 模态框样式 */
.upload-tips {
  margin-top: 16px;
  padding: 16px;
  background: #f0f8ff;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.upload-tips p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .management-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .left-actions,
  .right-actions {
    justify-content: center;
  }
  
  .file-list.grid {
    grid-template-columns: 1fr;
  }
  
  .file-item {
    padding: 12px;
  }
  
  .file-actions {
    flex-direction: column;
    gap: 2px;
  }
}

@media (max-width: 480px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    font-size: 36px;
    margin-right: 16px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}
</style>
