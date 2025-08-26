<template>
  <div class="user-data-manager">
    <CommonHeader :isUserSide="true" />
    <div class="content-wrapper">
      <CommonNavigation />
      <div class="main-content">
        <div class="page-header">
          <h1>我的数据管理</h1>
          <p>管理您的个人数据，包括备份、恢复和清除功能</p>
        </div>

        <div class="data-stats">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-card>
                <div class="stat-item">
                  <div class="stat-number">{{ dataStats.attendanceCount }}</div>
                  <div class="stat-label">考勤记录</div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="6">
              <a-card>
                <div class="stat-item">
                  <div class="stat-number">{{ dataStats.approvalCount }}</div>
                  <div class="stat-label">审批记录</div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="6">
              <a-card>
                <div class="stat-item">
                  <div class="stat-number">{{ dataStats.fileCount }}</div>
                  <div class="stat-label">文件记录</div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="6">
              <a-card>
                <div class="stat-item">
                  <div class="stat-number">{{ dataStats.activityCount }}</div>
                  <div class="stat-label">活动记录</div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>

        <div class="data-actions">
          <a-row :gutter="16">
            <a-col :span="8">
              <a-card title="数据备份" class="action-card">
                <template #extra>
                  <a-icon type="download" />
                </template>
                <p>导出您的所有个人数据，包括考勤、审批、文件等记录</p>
                <a-button type="primary" @click="exportData" :loading="exporting">
                  <a-icon type="download" />
                  导出数据
                </a-button>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card title="数据恢复" class="action-card">
                <template #extra>
                  <a-icon type="upload" />
                </template>
                <p>从备份文件中恢复您的个人数据</p>
                <a-upload
                  :showUploadList="false"
                  :beforeUpload="importData"
                  accept=".json"
                >
                  <a-button type="primary">
                    <a-icon type="upload" />
                    导入数据
                  </a-button>
                </a-upload>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card title="数据清除" class="action-card">
                <template #extra>
                  <a-icon type="delete" />
                </template>
                <p>清除您的所有个人数据（不可恢复）</p>
                <a-button type="danger" @click="showClearConfirm">
                  <a-icon type="delete" />
                  清除数据
                </a-button>
              </a-card>
            </a-col>
          </a-row>
        </div>

        <div class="data-preview">
          <a-card title="数据预览">
            <a-tabs>
              <a-tab-pane key="attendance" tab="考勤记录">
                <a-table
                  :columns="attendanceColumns"
                  :dataSource="attendanceData"
                  :pagination="{ pageSize: 5 }"
                  size="small"
                />
              </a-tab-pane>
              <a-tab-pane key="approval" tab="审批记录">
                <a-table
                  :columns="approvalColumns"
                  :dataSource="approvalData"
                  :pagination="{ pageSize: 5 }"
                  size="small"
                />
              </a-tab-pane>
              <a-tab-pane key="files" tab="文件记录">
                <a-table
                  :columns="fileColumns"
                  :dataSource="fileData"
                  :pagination="{ pageSize: 5 }"
                  size="small"
                />
              </a-tab-pane>
              <a-tab-pane key="activities" tab="活动记录">
                <a-table
                  :columns="activityColumns"
                  :dataSource="activityData"
                  :pagination="{ pageSize: 5 }"
                  size="small"
                />
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </div>
      </div>
    </div>

    <!-- 清除确认对话框 -->
    <a-modal
      title="确认清除数据"
      :visible="showClearModal"
      @ok="clearData"
      @cancel="showClearModal = false"
      okText="确认清除"
      cancelText="取消"
      okType="danger"
    >
      <p>您确定要清除所有个人数据吗？此操作不可恢复！</p>
      <p>将清除以下数据：</p>
      <ul>
        <li>考勤记录</li>
        <li>审批记录</li>
        <li>文件记录</li>
        <li>活动记录</li>
        <li>个人信息设置</li>
      </ul>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import CommonHeader from '@/components/CommonHeader.vue'
import CommonNavigation from '@/components/CommonNavigation.vue'
import {
  getAttendanceRecords,
  getApprovalRecords,
  getFileRecords,
  getUserActivities,
  exportUserData,
  importUserData,
  clearCurrentUserData
} from '@/utils/userDataManager'

export default defineComponent({
  name: 'UserDataManager',
  components: {
    CommonHeader,
    CommonNavigation
  },
  setup() {
    const exporting = ref(false)
    const showClearModal = ref(false)

    // 数据统计
    const dataStats = reactive({
      attendanceCount: 0,
      approvalCount: 0,
      fileCount: 0,
      activityCount: 0
    })

    // 预览数据
    const attendanceData = ref([])
    const approvalData = ref([])
    const fileData = ref([])
    const activityData = ref([])

    // 表格列定义
    const attendanceColumns = [
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '签到时间', dataIndex: 'checkInTime', key: 'checkInTime' },
      { title: '签退时间', dataIndex: 'checkOutTime', key: 'checkOutTime' },
      { title: '状态', dataIndex: 'statusText', key: 'statusText' }
    ]

    const approvalColumns = [
      { title: '申请类型', dataIndex: 'type', key: 'type' },
      { title: '申请内容', dataIndex: 'content', key: 'content' },
      { title: '申请时间', dataIndex: 'applyTime', key: 'applyTime' },
      { title: '状态', dataIndex: 'status', key: 'status' }
    ]

    const fileColumns = [
      { title: '文件名', dataIndex: 'name', key: 'name' },
      { title: '类型', dataIndex: 'type', key: 'type' },
      { title: '大小', dataIndex: 'size', key: 'size' },
      { title: '上传时间', dataIndex: 'uploadTime', key: 'uploadTime' }
    ]

    const activityColumns = [
      { title: '活动类型', dataIndex: 'type', key: 'type' },
      { title: '活动内容', dataIndex: 'title', key: 'title' },
      { title: '时间', dataIndex: 'time', key: 'time' },
      { title: '状态', dataIndex: 'statusText', key: 'statusText' }
    ]

    // 加载数据
    const loadData = () => {
      const attendance = getAttendanceRecords()
      const approval = getApprovalRecords()
      const files = getFileRecords()
      const activities = getUserActivities()

      dataStats.attendanceCount = attendance.length
      dataStats.approvalCount = approval.length
      dataStats.fileCount = files.length
      dataStats.activityCount = activities.length

      attendanceData.value = attendance.slice(0, 10)
      approvalData.value = approval.slice(0, 10)
      fileData.value = files.slice(0, 10)
      activityData.value = activities.slice(0, 10)
    }

    // 导出数据
    const exportData = async () => {
      exporting.value = true
      try {
        const data = exportUserData()
        const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `user_data_${data.username}_${new Date().toISOString().split('T')[0]}.json`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        message.success('数据导出成功！')
      } catch (error) {
        message.error('数据导出失败！')
        console.error('导出错误:', error)
      } finally {
        exporting.value = false
      }
    }

    // 导入数据
    const importData = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target.result)
          if (importUserData(data)) {
            message.success('数据导入成功！')
            loadData()
          } else {
            message.error('数据导入失败！')
          }
        } catch (error) {
          message.error('文件格式错误！')
          console.error('导入错误:', error)
        }
      }
      reader.readAsText(file)
      return false // 阻止自动上传
    }

    // 显示清除确认
    const showClearConfirm = () => {
      showClearModal.value = true
    }

    // 清除数据
    const clearData = () => {
      try {
        if (clearCurrentUserData()) {
          message.success('数据清除成功！')
          loadData()
        } else {
          message.error('数据清除失败！')
        }
      } catch (error) {
        message.error('数据清除失败！')
        console.error('清除错误:', error)
      } finally {
        showClearModal.value = false
      }
    }

    onMounted(() => {
      loadData()
    })

    return {
      exporting,
      showClearModal,
      dataStats,
      attendanceData,
      approvalData,
      fileData,
      activityData,
      attendanceColumns,
      approvalColumns,
      fileColumns,
      activityColumns,
      exportData,
      importData,
      showClearConfirm,
      clearData
    }
  }
})
</script>

<style scoped>
.user-data-manager {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content-wrapper {
  display: flex;
  min-height: calc(100vh - 64px);
}

.main-content {
  flex: 1;
  padding: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  margin: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h1 {
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.data-stats {
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 8px;
}

.stat-label {
  color: #7f8c8d;
  font-size: 14px;
}

.data-actions {
  margin-bottom: 24px;
}

.action-card {
  text-align: center;
  height: 100%;
}

.action-card p {
  margin-bottom: 16px;
  color: #7f8c8d;
}

.data-preview {
  margin-top: 24px;
}

.ant-table {
  background: white;
  border-radius: 8px;
}

.ant-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style> 