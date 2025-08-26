<template>
  <div class="my-announcement-container">
    <!-- 通用头部 -->
    <CommonHeader 
      title="我的公告"
      subtitle="查看重要公告信息"
      icon-type="notification"
      :isUserSide="true"
    />
    
    <div class="announcement-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <!-- 公告统计 -->
        <div class="stats-overview">
          <div class="stat-card">
            <div class="stat-icon">📢</div>
            <div class="stat-content">
              <div class="stat-number">{{ announcementStats.total }}</div>
              <div class="stat-label">总公告数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📖</div>
            <div class="stat-content">
              <div class="stat-number">{{ announcementStats.read }}</div>
              <div class="stat-label">已读公告</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">👀</div>
            <div class="stat-content">
              <div class="stat-number">{{ announcementStats.unread }}</div>
              <div class="stat-label">未读公告</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⭐</div>
            <div class="stat-content">
              <div class="stat-number">{{ announcementStats.important }}</div>
              <div class="stat-label">重要公告</div>
            </div>
          </div>
        </div>

        <!-- 公告列表 -->
        <div class="announcement-list">
          <div class="list-header">
            <h2>公告列表</h2>
            <div class="filter-actions">
              <a-select v-model:value="filterType" style="width: 120px; margin-right: 16px;">
                <a-select-option value="all">全部公告</a-select-option>
                <a-select-option value="unread">未读公告</a-select-option>
                <a-select-option value="important">重要公告</a-select-option>
              </a-select>
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索公告..."
                style="width: 200px;"
                @search="handleSearch"
              />
            </div>
          </div>
          
          <div class="announcement-grid">
            <div 
              v-for="announcement in filteredAnnouncements" 
              :key="announcement.id" 
              class="announcement-card"
              :class="{ unread: !announcement.isRead, important: announcement.isImportant }"
              @click="viewAnnouncement(announcement)"
            >
              <div class="card-header">
                <div class="announcement-type">
                  <span v-if="announcement.isImportant" class="important-badge">重要</span>
                  <span v-if="!announcement.isRead" class="unread-badge">未读</span>
                </div>
                <div class="announcement-date">{{ formatDate(announcement.publishTime) }}</div>
              </div>
              
              <div class="card-title">{{ announcement.title }}</div>
              <div class="card-content">{{ announcement.content }}</div>
              
              <div class="card-footer">
                <div class="publisher">发布人：{{ announcement.publisher }}</div>
                <div class="category">分类：{{ announcement.category }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 公告详情模态框 -->
    <a-modal
      v-model:open="showDetailModal"
      title="公告详情"
      width="800px"
      @cancel="showDetailModal = false"
      :footer="null"
    >
      <div v-if="selectedAnnouncement" class="announcement-detail">
        <div class="detail-header">
          <h2 class="detail-title">
            <span v-if="selectedAnnouncement.isImportant" class="important-badge">⭐</span>
            {{ selectedAnnouncement.title }}
          </h2>
          <div class="detail-meta">
            <p><strong>发布者：</strong>{{ selectedAnnouncement.publisher }}</p>
            <p><strong>发布时间：</strong>{{ formatDate(selectedAnnouncement.publishTime) }}</p>
            <p><strong>分类：</strong>
              <a-tag 
                v-for="tag in selectedAnnouncement.tags" 
                :key="tag"
                :color="getTagColor(tag)"
              >
                {{ tag }}
              </a-tag>
            </p>
          </div>
        </div>
        
        <div class="detail-content">
          <div class="content-text">{{ selectedAnnouncement.content }}</div>
          
          <div v-if="selectedAnnouncement.attachments && selectedAnnouncement.attachments.length > 0" class="attachments">
            <h4>附件：</h4>
            <div class="attachment-list">
              <div 
                v-for="attachment in selectedAnnouncement.attachments" 
                :key="attachment.id"
                class="attachment-item"
              >
                <PaperClipOutlined />
                <span class="attachment-name">{{ attachment.name }}</span>
                <a-button type="link" size="small" @click="downloadAttachment(attachment)">
                  下载
                </a-button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-footer">
          <a-button 
            v-if="!selectedAnnouncement.isRead"
            type="primary" 
            @click="markAsRead(selectedAnnouncement)"
          >
            标记为已读
          </a-button>
          <a-button @click="showDetailModal = false">关闭</a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PaperClipOutlined } from '@ant-design/icons-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'

// 响应式数据
const showDetailModal = ref(false)
const selectedAnnouncement = ref(null)
const searchKeyword = ref('')
const filterType = ref('all')
const currentPage = ref(1)

// 公告统计
const announcementStats = reactive({
  total: 0,
  read: 0,
  unread: 0,
  important: 0
})

// 公告列表
const announcements = ref([])

// 计算属性
const filteredAnnouncements = computed(() => {
  let result = announcements.value
  
  // 按类型过滤
  if (filterType.value === 'unread') {
    result = result.filter(item => !item.isRead)
  } else if (filterType.value === 'read') {
    result = result.filter(item => item.isRead)
  } else if (filterType.value === 'important') {
    result = result.filter(item => item.isImportant)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(item => 
      item.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      item.content.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      item.publisher.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  return result
})

// 方法
const loadAnnouncements = () => {
  // 模拟加载公告数据
  const mockAnnouncements = [
    {
      id: 1,
      title: '关于系统维护的通知',
      content: '为了提升系统性能，我们将于本周六凌晨2:00-6:00进行系统维护。维护期间系统将暂停服务，请提前做好相关工作安排。',
      publisher: '系统管理员',
      publishTime: new Date('2024-01-20 10:00:00'),
      tags: ['系统', '维护', '通知'],
      isRead: false,
      isImportant: true,
      attachments: [
        { id: 1, name: '维护计划.pdf', url: '#' }
      ]
    },
    {
      id: 2,
      title: '2024年春节放假安排',
      content: '根据国家规定和公司安排，2024年春节放假时间为2月10日-2月17日，共8天。2月18日正常上班。',
      publisher: '人事部',
      publishTime: new Date('2024-01-19 14:30:00'),
      tags: ['放假', '春节', '人事'],
      isRead: true,
      isImportant: true,
      attachments: []
    },
    {
      id: 3,
      title: '新员工入职培训通知',
      content: '欢迎新同事加入我们的团队！新员工入职培训将于下周一上午9:00在会议室A举行，请准时参加。',
      publisher: '培训部',
      publishTime: new Date('2024-01-18 16:00:00'),
      tags: ['培训', '新员工', '入职'],
      isRead: false,
      isImportant: false,
      attachments: [
        { id: 2, name: '培训手册.docx', url: '#' },
        { id: 3, name: '培训日程.xlsx', url: '#' }
      ]
    },
    {
      id: 4,
      title: '办公用品采购通知',
      content: '各部门如需采购办公用品，请于本周五前提交采购申请单。采购清单和预算请详细填写。',
      publisher: '行政部',
      publishTime: new Date('2024-01-17 11:00:00'),
      tags: ['采购', '办公用品', '行政'],
      isRead: true,
      isImportant: false,
      attachments: [
        { id: 4, name: '采购申请单.xlsx', url: '#' }
      ]
    },
    {
      id: 5,
      title: '项目进度汇报会议',
      content: '定于本周四下午2:00召开项目进度汇报会议，请各项目负责人准备相关材料并准时参加。',
      publisher: '项目管理部',
      publishTime: new Date('2024-01-16 15:30:00'),
      tags: ['会议', '项目', '汇报'],
      isRead: false,
      isImportant: false,
      attachments: []
    },
    {
      id: 6,
      title: '员工健康体检安排',
      content: '为保障员工身体健康，公司将于下月组织年度健康体检。具体时间和地点另行通知。',
      publisher: '人事部',
      publishTime: new Date('2024-01-15 09:00:00'),
      tags: ['体检', '健康', '人事'],
      isRead: true,
      isImportant: false,
      attachments: []
    }
  ]
  
  announcements.value = mockAnnouncements
  updateAnnouncementStats()
}

const updateAnnouncementStats = () => {
  const total = announcements.value.length
  const read = announcements.value.filter(item => item.isRead).length
  const unread = total - read
  const important = announcements.value.filter(item => item.isImportant).length
  
  announcementStats.total = total
  announcementStats.read = read
  announcementStats.unread = unread
  announcementStats.important = important
}

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}

const getTagColor = (tag) => {
  const colorMap = {
    '系统': 'blue',
    '维护': 'orange',
    '通知': 'green',
    '放假': 'red',
    '春节': 'red',
    '人事': 'purple',
    '培训': 'cyan',
    '新员工': 'blue',
    '入职': 'green',
    '采购': 'orange',
    '办公用品': 'blue',
    '行政': 'purple',
    '会议': 'cyan',
    '项目': 'blue',
    '汇报': 'green',
    '体检': 'green',
    '健康': 'green'
  }
  return colorMap[tag] || 'default'
}

const handleSearch = () => {
  currentPage.value = 1
}

const viewAnnouncement = (announcement) => {
  selectedAnnouncement.value = announcement
  showDetailModal.value = true
  
  // 自动标记为已读
  if (!announcement.isRead) {
    markAsRead(announcement)
  }
}

const markAsRead = (announcement) => {
  announcement.isRead = true
  updateAnnouncementStats()
  message.success('已标记为已读')
}

const downloadAttachment = (attachment) => {
  message.info(`开始下载附件: ${attachment.name}`)
  // 这里可以实现实际的下载逻辑
}

// 生命周期
onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.my-announcement-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.announcement-content {
  display: flex;
  gap: 20px;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.main-content {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 统计卡片样式 */
.stats-overview {
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

/* 公告列表样式 */
.announcement-list {
  margin-bottom: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.list-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.announcement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.announcement-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
  border-left: 4px solid transparent;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.announcement-card:hover {
  background: #e9ecef;
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.announcement-card.unread {
  border-left-color: #1890ff;
  background: #f0f8ff;
}

.announcement-card.important {
  border-left-color: #faad14;
  background: #fff7e6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 12px;
}

.announcement-type {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.announcement-date {
  font-size: 14px;
  color: #7f8c8d;
  flex-shrink: 0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.card-content {
  font-size: 14px;
  color: #5a6c7d;
  line-height: 1.6;
  margin-bottom: 16px;
  white-space: pre-wrap;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #7f8c8d;
}

.publisher {
  font-weight: 500;
}

.category {
  font-weight: 500;
}

/* 公告详情模态框样式 */
.announcement-detail {
  padding: 20px 0;
}

.detail-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.detail-title {
  margin: 0 0 16px 0;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-meta {
  color: #7f8c8d;
}

.detail-meta p {
  margin: 8px 0;
}

.detail-content {
  margin-bottom: 24px;
}

.content-text {
  line-height: 1.8;
  color: #5a6c7d;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.attachments h4 {
  margin: 16px 0 12px 0;
  color: #2c3e50;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.attachment-name {
  flex: 1;
  color: #2c3e50;
}

.detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .announcement-content {
    flex-direction: column;
  }

  .main-content {
    width: 100%;
    padding: 20px;
  }

  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .filter-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .announcement-grid {
    grid-template-columns: 1fr;
  }

  .announcement-card {
    padding: 16px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .announcement-type {
    flex-direction: column;
    align-items: flex-start;
  }

  .announcement-date {
    align-self: flex-start;
  }

  .card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .stats-overview {
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
  
  .announcement-card {
    padding: 16px;
  }
  
  .card-title {
    font-size: 16px;
  }
}
</style> 