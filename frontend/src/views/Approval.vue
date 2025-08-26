<template>
  <div class="approval-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-section">
          <h1 class="page-title">
            <a-icon type="check-circle" class="title-icon" />
            审批管理
          </h1>
          <p class="page-subtitle">管理公司各类审批流程、申请和审核状态</p>
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
        <div class="stat-icon">📋</div>
        <div class="stat-content">
          <div class="stat-number">23</div>
          <div class="stat-label">待审批</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">156</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">❌</div>
        <div class="stat-content">
          <div class="stat-number">8</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏱️</div>
        <div class="stat-content">
          <div class="stat-number">2.4</div>
          <div class="stat-label">平均处理时间(天)</div>
        </div>
      </div>
    </div>

    <!-- 审批列表 -->
    <div class="approval-list">
      <div class="list-header">
        <h2>审批列表</h2>
        <button class="btn-add" @click="showAddModal">+ 新建审批</button>
      </div>
      
      <div class="approval-table">
        <table>
          <thead>
            <tr>
              <th>申请编号</th>
              <th>申请人</th>
              <th>申请类型</th>
              <th>申请内容</th>
              <th>申请时间</th>
              <th>审批状态</th>
              <th>审批人</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="approval in approvals" :key="approval.id">
              <td>{{ approval.id }}</td>
              <td>
                <div class="applicant-info">
                  <img :src="approval.applicantAvatar" :alt="approval.applicant" class="avatar">
                  <span>{{ approval.applicant }}</span>
                </div>
              </td>
              <td>{{ approval.type }}</td>
              <td>{{ approval.content }}</td>
              <td>{{ approval.applyTime }}</td>
              <td>
                <span class="status" :class="approval.status">{{ approval.statusText }}</span>
              </td>
              <td>{{ approval.approver }}</td>
              <td>
                <div class="actions">
                  <button class="btn-approve" @click="approveApproval(approval)" :disabled="approval.status !== 'pending'">通过</button>
                  <button class="btn-reject" @click="rejectApproval(approval)" :disabled="approval.status !== 'pending'">拒绝</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新建审批模态框 -->
    <div v-if="showAddApprovalModal" class="modal-overlay" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>新建审批申请</h3>
          <button class="modal-close" @click="closeAddModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitApproval">
            <div class="form-group">
              <label>申请类型 <span class="required">*</span></label>
              <select v-model="approvalForm.type" required>
                <option value="">请选择申请类型</option>
                <option value="请假申请">请假申请</option>
                <option value="加班申请">加班申请</option>
                <option value="报销申请">报销申请</option>
                <option value="采购申请">采购申请</option>
                <option value="转岗申请">转岗申请</option>
                <option value="离职申请">离职申请</option>
                <option value="其他申请">其他申请</option>
              </select>
              <div v-if="formErrors.type" class="form-error">{{ formErrors.type }}</div>
            </div>
            
            <div class="form-group">
              <label>申请内容 <span class="required">*</span></label>
              <textarea 
                v-model="approvalForm.content" 
                placeholder="请详细描述您的申请内容..."
                rows="4"
                maxlength="500"
                required
              ></textarea>
              <div class="char-count">{{ approvalForm.content.length }}/500</div>
              <div v-if="formErrors.content" class="form-error">{{ formErrors.content }}</div>
            </div>

            <div class="form-group" v-if="approvalForm.type === '请假申请'">
              <label>请假时间</label>
              <div class="date-range">
                <input type="date" v-model="approvalForm.startDate" placeholder="开始日期">
                <span class="date-separator">至</span>
                <input type="date" v-model="approvalForm.endDate" placeholder="结束日期">
              </div>
            </div>

            <div class="form-group" v-if="approvalForm.type === '报销申请'">
              <label>报销金额</label>
              <input type="number" v-model="approvalForm.amount" placeholder="请输入报销金额" step="0.01" min="0">
            </div>

            <div class="form-group">
              <label>紧急程度</label>
              <select v-model="approvalForm.urgency">
                <option value="low">普通</option>
                <option value="medium">紧急</option>
                <option value="high">非常紧急</option>
              </select>
            </div>

            <div class="form-group">
              <label>审批人 <span class="required">*</span></label>
              <select v-model="approvalForm.approver" required>
                <option value="">请选择审批人</option>
                <option value="张经理">张经理</option>
                <option value="李主管">李主管</option>
                <option value="王总监">王总监</option>
                <option value="赵总裁">赵总裁</option>
              </select>
              <div v-if="formErrors.approver" class="form-error">{{ formErrors.approver }}</div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeAddModal">取消</button>
          <button type="button" class="btn-submit" @click="submitApproval" :disabled="submitting">
            <span v-if="submitting" class="loading-spinner"></span>
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 审批确认模态框 -->
    <div v-if="showConfirmModal" class="modal-overlay" @click="closeConfirmModal">
      <div class="modal-content confirm-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ confirmAction === 'approve' ? '审批通过' : '审批拒绝' }}</h3>
          <button class="modal-close" @click="closeConfirmModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>确定要{{ confirmAction === 'approve' ? '通过' : '拒绝' }}申请 <strong>{{ selectedApproval?.id }}</strong> 吗？</p>
          <div class="form-group" v-if="confirmAction === 'reject'">
            <label>拒绝原因 <span class="required">*</span></label>
            <textarea 
              v-model="rejectReason" 
              placeholder="请输入拒绝原因..."
              rows="3"
              maxlength="200"
            ></textarea>
            <div class="char-count">{{ rejectReason.length }}/200</div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeConfirmModal">取消</button>
          <button type="button" :class="confirmAction === 'approve' ? 'btn-approve-confirm' : 'btn-reject-confirm'" @click="confirmApproval">
            {{ confirmAction === 'approve' ? '确认通过' : '确认拒绝' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'Approval',
  setup() {
    const router = useRouter()
    
    // 用户信息
    const userInfo = ref({
      username: '',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=ApprovalUser1'
    })
    
    // 模态框状态
    const showAddApprovalModal = ref(false)
    const showConfirmModal = ref(false)
    const confirmAction = ref('')
    const selectedApproval = ref(null)
    const submitting = ref(false)
    const rejectReason = ref('')

    // 表单数据
    const approvalForm = ref({
      type: '',
      content: '',
      startDate: '',
      endDate: '',
      amount: '',
      urgency: 'low',
      approver: ''
    })

    // 表单错误
    const formErrors = ref({
      type: '',
      content: '',
      approver: ''
    })

    // 审批数据
    const approvals = ref([
      {
        id: 'AP001',
        applicant: '张三',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval1',
        type: '请假申请',
        content: '年假申请3天',
        applyTime: '2024-01-15 09:30:00',
        status: 'pending',
        statusText: '待审批',
        approver: '李经理'
      },
      {
        id: 'AP002',
        applicant: '李四',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval2',
        type: '采购申请',
        content: '办公设备采购',
        applyTime: '2024-01-14 14:20:00',
        status: 'approved',
        statusText: '已通过',
        approver: '王总监'
      },
      {
        id: 'AP003',
        applicant: '王五',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval3',
        type: '报销申请',
        content: '差旅费用报销',
        applyTime: '2024-01-13 16:45:00',
        status: 'rejected',
        statusText: '已拒绝',
        approver: '赵经理'
      },
      {
        id: 'AP004',
        applicant: '赵六',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval4',
        type: '加班申请',
        content: '项目紧急加班',
        applyTime: '2024-01-12 18:45:00',
        status: 'pending',
        statusText: '待审批',
        approver: '孙主管'
      },
      {
        id: 'AP005',
        applicant: '钱七',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval5',
        type: '培训申请',
        content: '专业技能培训',
        applyTime: '2024-01-11 16:30:00',
        status: 'approved',
        statusText: '已通过',
        approver: '周经理'
      },
      {
        id: 'AP006',
        applicant: '孙八',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval6',
        type: '调岗申请',
        content: '部门内部调岗',
        applyTime: '2024-01-10 10:20:00',
        status: 'pending',
        statusText: '待审批',
        approver: '吴总监'
      },
      {
        id: 'AP007',
        applicant: '周九',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval7',
        type: '设备申请',
        content: '新员工设备配置',
        applyTime: '2024-01-09 15:10:00',
        status: 'approved',
        statusText: '已通过',
        approver: '郑经理'
      },
      {
        id: 'AP008',
        applicant: '吴十',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval8',
        type: '会议申请',
        content: '项目评审会议',
        applyTime: '2024-01-08 13:25:00',
        status: 'pending',
        statusText: '待审批',
        approver: '王经理'
      },
      {
        id: 'AP009',
        applicant: '郑十一',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval9',
        type: '外勤申请',
        content: '客户现场服务',
        applyTime: '2024-01-07 09:15:00',
        status: 'approved',
        statusText: '已通过',
        approver: '李总监'
      },
      {
        id: 'AP010',
        applicant: '王十二',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval10',
        type: '预算申请',
        content: '季度营销预算',
        applyTime: '2024-01-06 14:40:00',
        status: 'pending',
        statusText: '待审批',
        approver: '张总监'
      },
      {
        id: 'AP011',
        applicant: '李十三',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval11',
        type: '合同申请',
        content: '供应商合同签署',
        applyTime: '2024-01-05 11:30:00',
        status: 'approved',
        statusText: '已通过',
        approver: '赵经理'
      },
      {
        id: 'AP012',
        applicant: '张十四',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval12',
        type: '招聘申请',
        content: '技术岗位招聘',
        applyTime: '2024-01-04 16:20:00',
        status: 'pending',
        statusText: '待审批',
        approver: '钱主管'
      },
      {
        id: 'AP013',
        applicant: '刘十五',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval13',
        type: '福利申请',
        content: '员工生日福利',
        applyTime: '2024-01-03 10:15:00',
        status: 'approved',
        statusText: '已通过',
        approver: '孙经理'
      },
      {
        id: 'AP014',
        applicant: '陈十六',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval14',
        type: '项目申请',
        content: '新产品开发项目',
        applyTime: '2024-01-02 14:50:00',
        status: 'pending',
        statusText: '待审批',
        approver: '周总监'
      },
      {
        id: 'AP015',
        applicant: '杨十七',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval15',
        type: '培训申请',
        content: '管理技能培训',
        applyTime: '2024-01-01 09:00:00',
        status: 'approved',
        statusText: '已通过',
        approver: '吴经理'
      },
      {
        id: 'AP016',
        applicant: '黄十八',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval16',
        type: '设备申请',
        content: '服务器升级',
        applyTime: '2023-12-31 17:30:00',
        status: 'pending',
        statusText: '待审批',
        approver: '郑总监'
      },
      {
        id: 'AP017',
        applicant: '赵十九',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval17',
        type: '活动申请',
        content: '团队建设活动',
        applyTime: '2023-12-30 13:45:00',
        status: 'approved',
        statusText: '已通过',
        approver: '王经理'
      },
      {
        id: 'AP018',
        applicant: '吴二十',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval18',
        type: '报销申请',
        content: '业务招待费',
        applyTime: '2023-12-29 15:20:00',
        status: 'pending',
        statusText: '待审批',
        approver: '李主管'
      },
      {
        id: 'AP019',
        applicant: '徐二一',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval19',
        type: '请假申请',
        content: '病假申请2天',
        applyTime: '2023-12-28 08:30:00',
        status: 'approved',
        statusText: '已通过',
        approver: '张经理'
      },
      {
        id: 'AP020',
        applicant: '孙二二',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval20',
        type: '采购申请',
        content: '办公用品采购',
        applyTime: '2023-12-27 11:15:00',
        status: 'pending',
        statusText: '待审批',
        approver: '周经理'
      },
      {
        id: 'AP021',
        applicant: '马二三',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval21',
        type: '加班申请',
        content: '系统维护加班',
        applyTime: '2023-12-26 19:00:00',
        status: 'approved',
        statusText: '已通过',
        approver: '赵主管'
      },
      {
        id: 'AP022',
        applicant: '朱二四',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval22',
        type: '培训申请',
        content: '新系统培训',
        applyTime: '2023-12-25 14:30:00',
        status: 'pending',
        statusText: '待审批',
        approver: '钱经理'
      },
      {
        id: 'AP023',
        applicant: '胡二五',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval23',
        type: '合同申请',
        content: '客户合同续签',
        applyTime: '2023-12-24 10:45:00',
        status: 'approved',
        statusText: '已通过',
        approver: '孙总监'
      },
      {
        id: 'AP024',
        applicant: '郭二六',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval24',
        type: '设备申请',
        content: '测试设备配置',
        applyTime: '2023-12-23 16:20:00',
        status: 'pending',
        statusText: '待审批',
        approver: '周经理'
      },
      {
        id: 'AP025',
        applicant: '何二七',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval25',
        type: '会议申请',
        content: '季度总结会议',
        applyTime: '2023-12-22 13:15:00',
        status: 'approved',
        statusText: '已通过',
        approver: '吴主管'
      },
      {
        id: 'AP026',
        applicant: '高二八',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval26',
        type: '外勤申请',
        content: '技术支持外勤',
        applyTime: '2023-12-21 09:30:00',
        status: 'pending',
        statusText: '待审批',
        approver: '郑经理'
      },
      {
        id: 'AP027',
        applicant: '林二九',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval27',
        type: '预算申请',
        content: '技术研发预算',
        applyTime: '2023-12-20 15:40:00',
        status: 'approved',
        statusText: '已通过',
        approver: '王总监'
      },
      {
        id: 'AP028',
        applicant: '罗三十',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval28',
        type: '招聘申请',
        content: '销售岗位招聘',
        applyTime: '2023-12-19 11:25:00',
        status: 'pending',
        statusText: '待审批',
        approver: '李经理'
      },
      {
        id: 'AP029',
        applicant: '梁三一',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval29',
        type: '福利申请',
        content: '年终奖发放',
        applyTime: '2023-12-18 14:50:00',
        status: 'approved',
        statusText: '已通过',
        approver: '张总监'
      },
      {
        id: 'AP030',
        applicant: '谢三二',
        applicantAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Approval30',
        type: '项目申请',
        content: '市场调研项目',
        applyTime: '2023-12-17 10:15:00',
        status: 'pending',
        statusText: '待审批',
        approver: '周经理'
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

    // 显示新建审批模态框
    const showAddModal = () => {
      showAddApprovalModal.value = true
      approvalForm.value = {
        type: '',
        content: '',
        startDate: '',
        endDate: '',
        amount: '',
        urgency: 'low',
        approver: ''
      }
      formErrors.value = {
        type: '',
        content: '',
        approver: ''
      }
    }

    // 关闭新建审批模态框
    const closeAddModal = () => {
      showAddApprovalModal.value = false
      approvalForm.value = {
        type: '',
        content: '',
        startDate: '',
        endDate: '',
        amount: '',
        urgency: 'low',
        approver: ''
      }
      formErrors.value = {
        type: '',
        content: '',
        approver: ''
      }
    }

    // 提交审批申请
    const submitApproval = async () => {
      // 表单验证
      let hasError = false
      if (!approvalForm.value.type) {
        formErrors.value.type = '请选择申请类型'
        hasError = true
      } else {
        formErrors.value.type = ''
      }

      if (!approvalForm.value.content.trim()) {
        formErrors.value.content = '请输入申请内容'
        hasError = true
      } else {
        formErrors.value.content = ''
      }

      if (!approvalForm.value.approver) {
        formErrors.value.approver = '请选择审批人'
        hasError = true
      } else {
        formErrors.value.approver = ''
      }

      if (hasError) return

      submitting.value = true

      try {
        // 模拟提交过程
        await new Promise(resolve => setTimeout(resolve, 2000))

        // 创建新的审批记录
        const newApproval = {
          id: `AP${String(approvals.value.length + 1).padStart(3, '0')}`,
          applicant: userInfo.value.username || '当前用户',
          applicantAvatar: userInfo.value.avatar,
          type: approvalForm.value.type,
          content: approvalForm.value.content,
          applyTime: new Date().toLocaleString('zh-CN'),
          status: 'pending',
          statusText: '待审批',
          approver: approvalForm.value.approver,
          startDate: approvalForm.value.startDate,
          endDate: approvalForm.value.endDate,
          amount: approvalForm.value.amount,
          urgency: approvalForm.value.urgency
        }

        // 添加到审批列表顶部
        approvals.value.unshift(newApproval)
        
        closeAddModal()
        alert('审批申请提交成功！')
      } catch (error) {
        alert('提交失败，请重试')
        console.error('提交错误:', error)
      } finally {
        submitting.value = false
      }
    }

    // 审批通过
    const approveApproval = (approval) => {
      if (approval.status !== 'pending') return
      selectedApproval.value = approval
      confirmAction.value = 'approve'
      showConfirmModal.value = true
    }

    // 审批拒绝
    const rejectApproval = (approval) => {
      if (approval.status !== 'pending') return
      selectedApproval.value = approval
      confirmAction.value = 'reject'
      rejectReason.value = ''
      showConfirmModal.value = true
    }

    // 关闭确认模态框
    const closeConfirmModal = () => {
      showConfirmModal.value = false
      selectedApproval.value = null
      confirmAction.value = ''
      rejectReason.value = ''
    }

    // 确认审批操作
    const confirmApproval = () => {
      if (!selectedApproval.value) return

      if (confirmAction.value === 'reject' && !rejectReason.value.trim()) {
        alert('请输入拒绝原因')
        return
      }

      const approval = selectedApproval.value
      const currentTime = new Date().toLocaleString('zh-CN')

      if (confirmAction.value === 'approve') {
        approval.status = 'approved'
        approval.statusText = '已通过'
        approval.approveTime = currentTime
        alert(`审批 ${approval.id} 已通过`)
      } else {
        approval.status = 'rejected'
        approval.statusText = '已拒绝'
        approval.approveTime = currentTime
        approval.rejectReason = rejectReason.value
        alert(`审批 ${approval.id} 已拒绝`)
      }

      closeConfirmModal()
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
      approvals,
      goHome,
      showAddApprovalModal,
      showConfirmModal,
      confirmAction,
      selectedApproval,
      submitting,
      rejectReason,
      approvalForm,
      formErrors,
      showAddModal,
      closeAddModal,
      submitApproval,
      approveApproval,
      rejectApproval,
      closeConfirmModal,
      confirmApproval
    }
  }
})
</script>

<style scoped>
.approval-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
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

.approval-list {
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

.btn-filter, .btn-export {
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

.btn-filter:hover, .btn-export:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.approval-table {
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

.applicant-info {
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

.status.pending {
  background: #fef3c7;
  color: #d97706;
}

.status.approved {
  background: #d1fae5;
  color: #059669;
}

.status.rejected {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
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
  content: '📝';
  font-size: 18px;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

/* 操作按钮样式 */
.btn-approve, .btn-reject {
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

.btn-approve {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
}

.btn-approve:hover:not(:disabled) {
  background: linear-gradient(135deg, #389e0d 0%, #52c41a 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.btn-approve::before {
  content: '✓';
  font-size: 14px;
}

.btn-reject {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.btn-reject:hover:not(:disabled) {
  background: linear-gradient(135deg, #cf1322 0%, #ff4d4f 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.btn-reject::before {
  content: '✗';
  font-size: 14px;
}

.btn-approve:disabled,
.btn-reject:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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
  max-width: 600px;
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

.date-range {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-separator {
  color: #6b7280;
  font-weight: 500;
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
.btn-approve-confirm,
.btn-reject-confirm {
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

.btn-approve-confirm {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
}

.btn-approve-confirm:hover {
  background: linear-gradient(135deg, #389e0d 0%, #52c41a 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(82, 196, 26, 0.4);
}

.btn-reject-confirm {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.btn-reject-confirm:hover {
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
  
  .approval-table {
    font-size: 12px;
  }
  
  th, td {
    padding: 12px 8px;
  }
}
</style> 
