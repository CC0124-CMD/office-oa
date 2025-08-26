<template>
  <div class="announcement-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="sound" class="title-icon" />
            公告管理
          </h1>
          <p class="page-subtitle">发布和管理公司公告、通知和重要信息</p>
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
      <div class="stat-card">
        <div class="stat-icon">📢</div>
        <div class="stat-content">
          <div class="stat-number">15</div>
          <div class="stat-label">今日公告</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👁️</div>
        <div class="stat-content">
          <div class="stat-number">89%</div>
          <div class="stat-label">阅读率</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-content">
          <div class="stat-number">12</div>
          <div class="stat-label">重要公告</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-number">3</div>
          <div class="stat-label">待发布</div>
        </div>
      </div>
    </div>

    <!-- 公告列表 -->
    <div class="announcement-list">
      <div class="list-header">
        <h2>公告列表</h2>
        <button class="btn-add" @click="showAddModal">+ 发布公告</button>
      </div>
      
      <div class="announcement-table">
        <table>
          <thead>
            <tr>
              <th>公告ID</th>
              <th>发布人</th>
              <th>标题</th>
              <th>内容</th>
              <th>发布时间</th>
              <th>状态</th>
              <th>优先级</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="announcement in announcements" :key="announcement.id">
              <td>{{ announcement.id }}</td>
              <td>
                <div class="publisher-info">
                  <img :src="announcement.publisherAvatar" :alt="announcement.publisher" class="avatar">
                  <span>{{ announcement.publisher }}</span>
                </div>
              </td>
              <td>{{ announcement.title }}</td>
              <td>{{ announcement.content }}</td>
              <td>{{ announcement.publishTime }}</td>
              <td>
                <span class="status" :class="announcement.status">{{ announcement.statusText }}</span>
              </td>
              <td>
                <span class="priority" :class="announcement.priority">{{ announcement.priorityText }}</span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-edit" @click="showEditModal(announcement)">编辑</button>
                  <button class="btn-delete" @click="showDeleteConfirm(announcement)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 发布/编辑公告模态框 -->
    <div v-if="showAnnouncementModal" class="modal-overlay" @click="closeAnnouncementModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑公告' : '发布公告' }}</h3>
          <button class="modal-close" @click="closeAnnouncementModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitAnnouncement">
            <div class="form-group">
              <label>公告标题 <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="announcementForm.title" 
                placeholder="请输入公告标题..."
                maxlength="100"
                required
              >
              <div class="char-count">{{ announcementForm.title.length }}/100</div>
              <div v-if="formErrors.title" class="form-error">{{ formErrors.title }}</div>
            </div>
            
            <div class="form-group">
              <label>公告内容 <span class="required">*</span></label>
              <textarea 
                v-model="announcementForm.content" 
                placeholder="请输入公告详细内容..."
                rows="6"
                maxlength="1000"
                required
              ></textarea>
              <div class="char-count">{{ announcementForm.content.length }}/1000</div>
              <div v-if="formErrors.content" class="form-error">{{ formErrors.content }}</div>
            </div>

            <div class="form-group">
              <label>优先级 <span class="required">*</span></label>
              <select v-model="announcementForm.priority" required>
                <option value="">请选择优先级</option>
                <option value="low">普通</option>
                <option value="medium">重要</option>
                <option value="high">紧急</option>
              </select>
              <div v-if="formErrors.priority" class="form-error">{{ formErrors.priority }}</div>
            </div>

            <div class="form-group">
              <label>发布状态</label>
              <select v-model="announcementForm.status">
                <option value="published">立即发布</option>
                <option value="draft">保存为草稿</option>
              </select>
            </div>

            <div class="form-group">
              <label>是否置顶</label>
              <select v-model="announcementForm.isTop">
                <option value="false">否</option>
                <option value="true">是</option>
              </select>
            </div>

            <div class="form-group">
              <label>通知范围</label>
              <select v-model="announcementForm.scope">
                <option value="all">全公司</option>
                <option value="department">部门内</option>
                <option value="specific">指定人员</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeAnnouncementModal">取消</button>
          <button type="button" class="btn-submit" @click="submitAnnouncement" :disabled="submitting">
            <span v-if="submitting" class="loading-spinner"></span>
            {{ submitting ? (isEdit ? '更新中...' : '发布中...') : (isEdit ? '更新公告' : '发布公告') }}
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
          <p>确定要删除公告 <strong>{{ announcementToDelete?.title }}</strong> 吗？</p>
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
  name: 'Announcement',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=AnnouncementUser1'
    })
    
    // 模态框状态
    const showAnnouncementModal = ref(false)
    const showDeleteModal = ref(false)
    const isEdit = ref(false)
    const submitting = ref(false)
    const announcementToDelete = ref(null)
    const editingAnnouncement = ref(null)

    // 表单数据
    const announcementForm = ref({
      title: '',
      content: '',
      priority: '',
      status: 'published',
      isTop: 'false',
      scope: 'all'
    })

    // 表单错误
    const formErrors = ref({
      title: '',
      content: '',
      priority: ''
    })

    // 公告数据
    const announcements = ref([
      {
        id: 'ANN001',
        publisher: '张总',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement1',
        title: '公司年度总结会议通知',
        content: '定于本周五下午2点召开年度总结会议',
        publishTime: '2024-01-15 09:00:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN002',
        publisher: '李经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement2',
        title: '年终奖金发放通知',
        content: '年终奖金将于本月底前发放到账',
        publishTime: '2024-01-14 14:30:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN003',
        publisher: '王主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement3',
        title: '新员工入职培训安排',
        content: '新员工入职培训将于下周一开始',
        publishTime: '2024-01-13 10:15:00',
        status: 'draft',
        statusText: '草稿',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN004',
        publisher: '赵工程师',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement4',
        title: '系统维护升级通知',
        content: '系统将于今晚进行维护升级',
        publishTime: '2024-01-12 16:45:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN005',
        publisher: '周人事',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement5',
        title: '春节放假安排通知',
        content: '春节放假时间安排已确定',
        publishTime: '2024-01-11 11:20:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN006',
        publisher: '吴主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement6',
        title: '员工体检安排通知',
        content: '年度员工体检将于下月进行',
        publishTime: '2024-01-10 15:30:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN007',
        publisher: '郑总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement7',
        title: '季度目标完成情况',
        content: '第四季度目标完成情况汇报',
        publishTime: '2024-01-09 09:45:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN008',
        publisher: '孙经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement8',
        title: '专业技能培训计划',
        content: '专业技能培训计划已制定完成',
        publishTime: '2024-01-08 14:15:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN009',
        publisher: '钱总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement9',
        title: '项目奖金分配方案',
        content: '项目奖金分配方案已确定',
        publishTime: '2024-01-07 10:30:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN010',
        publisher: '李总',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement10',
        title: '新办公室搬迁通知',
        content: '新办公室搬迁计划已确定',
        publishTime: '2024-01-06 16:00:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN011',
        publisher: '王总',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement11',
        title: '公司成立五周年庆典',
        content: '公司成立五周年庆典活动安排',
        publishTime: '2024-01-05 13:45:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN012',
        publisher: '张总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement12',
        title: '年度业绩报告发布',
        content: '年度业绩报告已发布',
        publishTime: '2024-01-04 09:20:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN013',
        publisher: '陈主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement13',
        title: '环保倡议活动通知',
        content: '环保倡议活动即将开始',
        publishTime: '2024-01-03 15:10:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN014',
        publisher: '刘工程师',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement14',
        title: '信息安全培训通知',
        content: '信息安全培训将于下周进行',
        publishTime: '2024-01-02 11:35:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN015',
        publisher: '马经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement15',
        title: '团队建设活动安排',
        content: '团队建设活动安排已确定',
        publishTime: '2024-01-01 14:50:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN016',
        publisher: '朱总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement16',
        title: '销售业绩表彰大会',
        content: '销售业绩表彰大会通知',
        publishTime: '2023-12-31 10:15:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN017',
        publisher: '胡人事',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement17',
        title: '学历提升补贴政策',
        content: '学历提升补贴政策已更新',
        publishTime: '2023-12-30 16:30:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN018',
        publisher: '郭主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement18',
        title: '员工运动会通知',
        content: '员工运动会将于下月举行',
        publishTime: '2023-12-29 13:20:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN019',
        publisher: '何经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement19',
        title: '年度工作总结要求',
        content: '年度工作总结要求已发布',
        publishTime: '2023-12-28 09:45:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN020',
        publisher: '高主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement20',
        title: '员工生日福利发放',
        content: '员工生日福利发放通知',
        publishTime: '2023-12-27 15:15:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN021',
        publisher: '林经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement21',
        title: '考勤制度调整通知',
        content: '考勤制度调整通知',
        publishTime: '2023-12-26 11:00:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN022',
        publisher: '罗工程师',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement22',
        title: '企业微信使用指南',
        content: '企业微信使用指南已更新',
        publishTime: '2023-12-25 14:25:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN023',
        publisher: '梁总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement23',
        title: '国际化发展战略',
        content: '公司国际化发展战略发布',
        publishTime: '2023-12-24 10:40:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN024',
        publisher: '谢经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement24',
        title: '新产品发布会通知',
        content: '新产品发布会即将举行',
        publishTime: '2023-12-23 16:50:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN025',
        publisher: '王总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement25',
        title: '年度预算编制通知',
        content: '年度预算编制工作开始',
        publishTime: '2023-12-22 13:15:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      },
      {
        id: 'ANN026',
        publisher: '李总',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement26',
        title: '优秀员工评选结果',
        content: '年度优秀员工评选结果',
        publishTime: '2023-12-21 09:30:00',
        status: 'published',
        statusText: '已发布',
        priority: 'high',
        priorityText: '高'
      },
      {
        id: 'ANN027',
        publisher: '张主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement27',
        title: '图书角建设倡议',
        content: '公司图书角建设倡议',
        publishTime: '2023-12-20 15:45:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN028',
        publisher: '陈经理',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement28',
        title: '绿色办公倡议',
        content: '绿色办公倡议活动',
        publishTime: '2023-12-19 11:20:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN029',
        publisher: '刘主管',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement29',
        title: '部门团建活动安排',
        content: '部门团建活动安排通知',
        publishTime: '2023-12-18 14:10:00',
        status: 'published',
        statusText: '已发布',
        priority: 'low',
        priorityText: '低'
      },
      {
        id: 'ANN030',
        publisher: '马总监',
        publisherAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Announcement30',
        title: '月度业绩分析报告',
        content: '11月业绩分析报告发布',
        publishTime: '2023-12-17 10:25:00',
        status: 'published',
        statusText: '已发布',
        priority: 'medium',
        priorityText: '中'
      }
    ])

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

    // 显示添加公告模态框
    const showAddModal = () => {
      isEdit.value = false
      showAnnouncementModal.value = true
      announcementForm.value = {
        title: '',
        content: '',
        priority: '',
        status: 'published',
        isTop: 'false',
        scope: 'all'
      }
      formErrors.value = {
        title: '',
        content: '',
        priority: ''
      }
    }

    // 显示编辑公告模态框
    const showEditModal = (announcement) => {
      isEdit.value = true
      editingAnnouncement.value = announcement
      showAnnouncementModal.value = true
      announcementForm.value = {
        title: announcement.title,
        content: announcement.content,
        priority: announcement.priority,
        status: announcement.status,
        isTop: announcement.isTop || 'false',
        scope: announcement.scope || 'all'
      }
      formErrors.value = {
        title: '',
        content: '',
        priority: ''
      }
    }

    // 关闭公告模态框
    const closeAnnouncementModal = () => {
      showAnnouncementModal.value = false
      isEdit.value = false
      editingAnnouncement.value = null
      announcementForm.value = {
        title: '',
        content: '',
        priority: '',
        status: 'published',
        isTop: 'false',
        scope: 'all'
      }
      formErrors.value = {
        title: '',
        content: '',
        priority: ''
      }
    }

    // 提交公告
    const submitAnnouncement = async () => {
      // 表单验证
      let hasError = false
      if (!announcementForm.value.title.trim()) {
        formErrors.value.title = '请输入公告标题'
        hasError = true
      } else {
        formErrors.value.title = ''
      }

      if (!announcementForm.value.content.trim()) {
        formErrors.value.content = '请输入公告内容'
        hasError = true
      } else {
        formErrors.value.content = ''
      }

      if (!announcementForm.value.priority) {
        formErrors.value.priority = '请选择优先级'
        hasError = true
      } else {
        formErrors.value.priority = ''
      }

      if (hasError) return

      submitting.value = true

      try {
        // 模拟提交过程
        await new Promise(resolve => setTimeout(resolve, 2000))

        // 获取优先级和状态文本
        const priorityTextMap = {
          'low': '普通',
          'medium': '重要',
          'high': '紧急'
        }

        const statusTextMap = {
          'published': '已发布',
          'draft': '草稿'
        }

        if (isEdit.value && editingAnnouncement.value) {
          // 编辑模式 - 更新现有公告
          const announcement = editingAnnouncement.value
          announcement.title = announcementForm.value.title
          announcement.content = announcementForm.value.content
          announcement.priority = announcementForm.value.priority
          announcement.priorityText = priorityTextMap[announcementForm.value.priority]
          announcement.status = announcementForm.value.status
          announcement.statusText = statusTextMap[announcementForm.value.status]
          announcement.isTop = announcementForm.value.isTop
          announcement.scope = announcementForm.value.scope
          announcement.updateTime = new Date().toLocaleString('zh-CN')
          
          alert('公告更新成功！')
        } else {
          // 新建模式 - 创建新公告
          const newAnnouncement = {
            id: `ANN${String(announcements.value.length + 1).padStart(3, '0')}`,
            publisher: userInfo.value.username || '当前用户',
            publisherAvatar: userInfo.value.avatar,
            title: announcementForm.value.title,
            content: announcementForm.value.content,
            publishTime: new Date().toLocaleString('zh-CN'),
            status: announcementForm.value.status,
            statusText: statusTextMap[announcementForm.value.status],
            priority: announcementForm.value.priority,
            priorityText: priorityTextMap[announcementForm.value.priority],
            isTop: announcementForm.value.isTop,
            scope: announcementForm.value.scope
          }

          // 添加到公告列表顶部
          announcements.value.unshift(newAnnouncement)
          
          alert('公告发布成功！')
        }

        closeAnnouncementModal()
      } catch (error) {
        alert('操作失败，请重试')
        console.error('提交错误:', error)
      } finally {
        submitting.value = false
      }
    }

    // 显示删除确认
    const showDeleteConfirm = (announcement) => {
      announcementToDelete.value = announcement
      showDeleteModal.value = true
    }

    // 关闭删除确认模态框
    const closeDeleteModal = () => {
      showDeleteModal.value = false
      announcementToDelete.value = null
    }

    // 确认删除
    const confirmDelete = () => {
      if (announcementToDelete.value) {
        const index = announcements.value.findIndex(ann => ann.id === announcementToDelete.value.id)
        if (index !== -1) {
          announcements.value.splice(index, 1)
          closeDeleteModal()
          alert('公告已删除')
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
      announcements,
      goHome,
      showAnnouncementModal,
      showDeleteModal,
      isEdit,
      submitting,
      announcementToDelete,
      announcementForm,
      formErrors,
      showAddModal,
      showEditModal,
      closeAnnouncementModal,
      submitAnnouncement,
      showDeleteConfirm,
      closeDeleteModal,
      confirmDelete
    }
  }
})
</script>

<style scoped>
.announcement-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

/* 返回首页按钮 */
.back-btn {
  position: fixed !important;
  top: 24px !important;
  left: 24px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px 22px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4), 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 999999 !important;
  width: 130px;
  height: 48px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transform: translateZ(0) !important;
  will-change: transform !important;
  pointer-events: auto !important;
  isolation: isolate !important;
  contain: layout style paint !important;
}

.back-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.back-btn:hover::before {
  left: 100%;
}

.back-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.5), 0 8px 15px rgba(0, 0, 0, 0.15);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.back-btn:active {
  transform: translateY(-1px) scale(1.02);
}


/* 添加按钮样式 */
.btn-add {
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

.btn-add::before {
  content: '📢';
  font-size: 18px;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

/* 操作按钮样式 */
.btn-edit, .btn-delete {
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

.btn-edit {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.btn-edit:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-edit::before {
  content: '✏️';
  font-size: 14px;
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

.btn-delete::before {
  content: '🗑️';
  font-size: 14px;
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

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
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

.announcement-list {
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
  margin-bottom: 24px;
}

.list-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-add, .btn-filter, .btn-export {
  padding: 10px 20px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-add {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border-color: #10b981;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
}

.btn-filter:hover, .btn-export:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.announcement-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  padding: 16px 12px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.announcement-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.announcement-icon {
  font-size: 20px;
}

.announcement-title {
  font-weight: 600;
  color: #1e293b;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status.published {
  background: #d1fae5;
  color: #059669;
}

.status.draft {
  background: #f3f4f6;
  color: #6b7280;
}

.status.archived {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-view, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit {
  background: #dbeafe;
  color: #1d4ed8;
}

.btn-edit:hover {
  background: #bfdbfe;
}

.btn-view {
  background: #f3f4f6;
  color: #374151;
}

.btn-view:hover {
  background: #e5e7eb;
}

.btn-delete {
  background: #fee2e2;
  color: #dc2626;
}

.btn-delete:hover {
  background: #fecaca;
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
  max-width: 700px;
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
  border-radius: 50%;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 0 24px;
}

.modal-footer {
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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

.form-error {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
}

.btn-cancel,
.btn-submit,
.btn-delete-confirm {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-submit {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-delete-confirm {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.btn-delete-confirm:hover {
  background: linear-gradient(135deg, #cf1322 0%, #ff4d4f 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.4);
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

@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .list-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
  
  .announcement-table {
    font-size: 12px;
  }
  
  th, td {
    padding: 12px 8px;
  }
}
</style> 