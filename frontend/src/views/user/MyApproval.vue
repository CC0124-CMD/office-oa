<template>
  <div class="my-approval-container">
    <!-- 公共头部 -->
    <CommonHeader 
      title="我的审批"
      subtitle="查看和管理您的审批申请"
      icon-type="check-circle"
      :isUserSide="true"
    />
    
    <div class="approval-content">
      <CommonNavigation :isAdmin="false" />
      
      <div class="main-content">
        <!-- 统计概览 -->
        <div class="stats-overview">
          <div class="stat-card">
            <div class="stat-icon">📋</div>
            <div class="stat-content">
              <div class="stat-number">{{ myApprovals.length }}</div>
              <div class="stat-label">总申请数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⏳</div>
            <div class="stat-content">
              <div class="stat-number">{{ pendingCount }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-content">
              <div class="stat-number">{{ approvedCount }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">❌</div>
            <div class="stat-content">
              <div class="stat-number">{{ rejectedCount }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </div>

        <!-- 审批列表 -->
        <div class="approval-list">
          <div class="list-header">
            <h2>我的审批记录</h2>
            <button class="btn-add" @click="showAddModal">+ 新建审批</button>
          </div>
          
          <div class="approval-table">
            <table>
              <thead>
                <tr>
                  <th>申请ID</th>
                  <th>申请类型</th>
                  <th>申请内容</th>
                  <th>申请时间</th>
                  <th>审批状态</th>
                  <th>审批意见</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="approval in myApprovals" :key="approval.id">
                  <td>{{ approval.id }}</td>
                  <td>{{ approval.type }}</td>
                  <td>{{ approval.content }}</td>
                  <td>{{ approval.applyTime }}</td>
                  <td>
                    <span class="status" :class="approval.status">{{ approval.statusText }}</span>
                  </td>
                  <td>{{ approval.comment || '-' }}</td>
                  <td>
                    <div class="actions">
                      <button 
                        v-if="approval.status === 'pending'" 
                        class="btn-cancel" 
                        @click="cancelApproval(approval)"
                      >
                        撤回
                      </button>
                      <button 
                        v-if="approval.status === 'pending'" 
                        class="btn-edit" 
                        @click="showEditModal(approval)"
                      >
                        编辑
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建/编辑审批模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑审批' : '新建审批' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitApproval">
            <div class="form-group">
              <label>申请类型 <span class="required">*</span></label>
              <select v-model="approvalForm.type" required>
                <option value="">请选择申请类型</option>
                <option value="请假">请假申请</option>
                <option value="报销">报销申请</option>
                <option value="采购">采购申请</option>
                <option value="其他">其他申请</option>
              </select>
              <div v-if="formErrors.type" class="form-error">{{ formErrors.type }}</div>
            </div>
            
            <div class="form-group">
              <label>申请内容 <span class="required">*</span></label>
              <textarea 
                v-model="approvalForm.content" 
                placeholder="请详细描述您的申请内容..."
                rows="4"
                required
              ></textarea>
              <div v-if="formErrors.content" class="form-error">{{ formErrors.content }}</div>
            </div>

            <div class="form-group">
              <label>申请金额</label>
              <input 
                type="number" 
                v-model="approvalForm.amount" 
                placeholder="请输入申请金额（如适用）"
                min="0"
                step="0.01"
              >
            </div>

            <div class="form-group">
              <label>紧急程度</label>
              <select v-model="approvalForm.priority">
                <option value="low">普通</option>
                <option value="medium">重要</option>
                <option value="high">紧急</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeModal">取消</button>
          <button type="button" class="btn-submit" @click="submitApproval" :disabled="submitting">
            {{ submitting ? (isEditing ? '更新中...' : '提交中...') : (isEditing ? '更新审批' : '提交审批') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, computed, onMounted, provide } from 'vue'
import { message } from 'ant-design-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'

export default defineComponent({
  name: 'MyApproval',
  components: {
    CommonHeader,
    CommonNavigation
  },
  setup() {
    // 头部用户信息
    const headerUserInfo = reactive({
      username: '',
      avatar: ''
    })

    // 提供用户信息给公共头部组件
    provide('userInfo', headerUserInfo)

    const pushActivity = (detail) => {
      try {
        window.dispatchEvent(new CustomEvent('userActivity', { detail }))
        const key = 'userActivities'
        const raw = localStorage.getItem(key)
        const list = raw ? JSON.parse(raw) : []
        list.unshift({
          id: Date.now(),
          type: detail.type || 'approval',
          icon: 'notification',
          title: detail.title || '审批操作',
          time: detail.time || new Date().toTimeString().slice(0,5),
          status: detail.status || 'pending',
          statusText: detail.statusText || '待处理'
        })
        localStorage.setItem(key, JSON.stringify(list.slice(0,20)))
      } catch (e) {
        // ignore storage errors
      }
    }

    // 模态框状态
    const showModal = ref(false)
    const isEditing = ref(false)
    const submitting = ref(false)
    const editingApproval = ref(null)

    // 表单数据
    const approvalForm = ref({
      type: '',
      content: '',
      amount: '',
      priority: 'low'
    })

    // 表单错误
    const formErrors = ref({
      type: '',
      content: ''
    })

    // 我的审批数据
    const myApprovals = ref([
      {
        id: 'APP001',
        type: '请假申请',
        content: '因家中有事，申请请假3天',
        applyTime: '2024-01-15 09:00:00',
        status: 'pending',
        statusText: '待审批',
        comment: '',
        amount: '',
        priority: 'medium'
      },
      {
        id: 'APP002',
        type: '报销申请',
        content: '出差期间的交通费和住宿费报销',
        applyTime: '2024-01-14 14:30:00',
        status: 'approved',
        statusText: '已通过',
        comment: '同意报销，请提供相关票据',
        amount: '1200.00',
        priority: 'high'
      },
      {
        id: 'APP003',
        type: '采购申请',
        content: '申请购买办公用品',
        applyTime: '2024-01-13 10:15:00',
        status: 'rejected',
        statusText: '已拒绝',
        comment: '预算不足，建议延后采购',
        amount: '500.00',
        priority: 'low'
      }
    ])

    // 计算统计数量
    const pendingCount = computed(() => 
      myApprovals.value.filter(a => a.status === 'pending').length
    )
    const approvedCount = computed(() => 
      myApprovals.value.filter(a => a.status === 'approved').length
    )
    const rejectedCount = computed(() => 
      myApprovals.value.filter(a => a.status === 'rejected').length
    )

    // 加载用户信息
    const loadUserInfo = () => {
      try {
        const savedUserInfo = localStorage.getItem('savedUserInfo')
        if (savedUserInfo) {
          const parsedSavedInfo = JSON.parse(savedUserInfo)
          headerUserInfo.username = parsedSavedInfo.username || ''
          headerUserInfo.avatar = parsedSavedInfo.avatar || ''
        } else {
          const currentUser = localStorage.getItem('user')
          if (currentUser) {
            const parsedUser = JSON.parse(currentUser)
            headerUserInfo.username = parsedUser.username || localStorage.getItem('username') || 'user'
            headerUserInfo.avatar = parsedUser.avatar || ''
          }
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    // 显示新建审批模态框
    const showAddModal = () => {
      isEditing.value = false
      editingApproval.value = null
      approvalForm.value = {
        type: '',
        content: '',
        amount: '',
        priority: 'low'
      }
      formErrors.value = { type: '', content: '' }
      showModal.value = true
    }

    // 显示编辑审批模态框
    const showEditModal = (approval) => {
      isEditing.value = true
      editingApproval.value = approval
      approvalForm.value = {
        type: approval.type,
        content: approval.content,
        amount: approval.amount,
        priority: approval.priority
      }
      formErrors.value = { type: '', content: '' }
      showModal.value = true
    }

    // 关闭模态框
    const closeModal = () => {
      showModal.value = false
      isEditing.value = false
      editingApproval.value = null
    }

    // 表单验证
    const validateForm = () => {
      let isValid = true
      formErrors.value = { type: '', content: '' }

      if (!approvalForm.value.type) {
        formErrors.value.type = '请选择申请类型'
        isValid = false
      }
      if (!approvalForm.value.content.trim()) {
        formErrors.value.content = '请输入申请内容'
        isValid = false
      }

      return isValid
    }

    // 提交审批
    const submitApproval = async () => {
      if (!validateForm()) return

      submitting.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))

        if (isEditing.value && editingApproval.value) {
          // 编辑模式
          const approval = editingApproval.value
          approval.type = approvalForm.value.type
          approval.content = approvalForm.value.content
          approval.amount = approvalForm.value.amount
          approval.priority = approvalForm.value.priority
          approval.updateTime = new Date().toLocaleString('zh-CN')
          
          pushActivity({ type: 'approval', title: '更新审批申请', status: 'completed', statusText: '已更新' })
          message.success('审批申请已更新！')
        } else {
          // 新建模式
          const newApproval = {
            id: `APP${String(myApprovals.value.length + 1).padStart(3, '0')}`,
            type: approvalForm.value.type,
            content: approvalForm.value.content,
            amount: approvalForm.value.amount,
            priority: approvalForm.value.priority,
            applyTime: new Date().toLocaleString('zh-CN'),
            status: 'pending',
            statusText: '待审批',
            comment: ''
          }
          myApprovals.value.unshift(newApproval)
          
          pushActivity({ type: 'approval', title: '提交审批申请', status: 'pending', statusText: '待审批' })
          message.success('审批申请已提交！')
        }

        closeModal()
      } catch (error) {
        message.error('操作失败，请重试')
        console.error('提交错误:', error)
      } finally {
        submitting.value = false
      }
    }

    // 撤回审批
    const cancelApproval = (approval) => {
      if (confirm('确定要撤回这个审批申请吗？')) {
        const index = myApprovals.value.findIndex(a => a.id === approval.id)
        if (index !== -1) {
          myApprovals.value.splice(index, 1)
          pushActivity({ type: 'approval', title: '撤回审批申请', status: 'completed', statusText: '已撤回' })
          message.success('审批申请已撤回')
        }
      }
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      headerUserInfo,
      showModal,
      isEditing,
      submitting,
      editingApproval,
      approvalForm,
      formErrors,
      myApprovals,
      pendingCount,
      approvedCount,
      rejectedCount,
      showAddModal,
      showEditModal,
      closeModal,
      submitApproval,
      cancelApproval
    }
  }
})
</script>

<style scoped>
.my-approval-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 0;
}

.approval-content {
  display: flex;
  padding: 20px;
  gap: 20px;
  margin-top: 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* 审批列表 */
.approval-list {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.btn-add {
  padding: 10px 20px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
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

tr:hover {
  background: #f8fafc;
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
  background: #dcfce7;
  color: #166534;
}

.status.rejected {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-cancel {
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

.btn-cancel {
  background: #fee2e2;
  color: #dc2626;
}

.btn-cancel:hover {
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.required {
  color: #dc2626;
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

.btn-submit:hover:not(:disabled) {
  background: #2563eb;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  
  .my-approval-container {
    padding: 16px;
  }
  
  .approval-list {
    padding: 16px;
  }
  
  th, td {
    padding: 12px 8px;
    font-size: 12px;
  }
}
</style> 