/**
 * 用户数据管理工具
 * 为每个用户提供独立的数据存储空间
 */

// 获取当前登录用户
const getCurrentUser = () => {
  try {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  } catch (e) {
    return null
  }
}

// 获取当前用户名
const getCurrentUsernameFromStorage = () => {
  const user = getCurrentUser()
  return user ? user.username : localStorage.getItem('username')
}

// 生成用户特定的存储键
const getUserStorageKey = (baseKey, username = null) => {
  const currentUser = username || getCurrentUsernameFromStorage()
  return currentUser ? `${currentUser}_${baseKey}` : baseKey
}

// 用户数据管理类
class UserDataManager {
  constructor() {
    this.currentUser = getCurrentUsernameFromStorage()
  }

  // 设置当前用户
  setCurrentUser(username) {
    this.currentUser = username
  }

  // 获取用户特定的数据
  getUserData(key, defaultValue = null) {
    try {
      const storageKey = getUserStorageKey(key, this.currentUser)
      const data = localStorage.getItem(storageKey)
      return data ? JSON.parse(data) : defaultValue
    } catch (e) {
      console.error('获取用户数据失败:', e)
      return defaultValue
    }
  }

  // 保存用户特定的数据
  saveUserData(key, data) {
    try {
      const storageKey = getUserStorageKey(key, this.currentUser)
      localStorage.setItem(storageKey, JSON.stringify(data))
      return true
    } catch (e) {
      console.error('保存用户数据失败:', e)
      return false
    }
  }

  // 删除用户特定的数据
  removeUserData(key) {
    try {
      const storageKey = getUserStorageKey(key, this.currentUser)
      localStorage.removeItem(storageKey)
      return true
    } catch (e) {
      console.error('删除用户数据失败:', e)
      return false
    }
  }

  // 获取所有用户数据键
  getAllUserDataKeys() {
    const keys = []
    const prefix = this.currentUser ? `${this.currentUser}_` : ''
    
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (key && key.startsWith(prefix)) {
        keys.push(key)
      }
    }
    
    return keys
  }

  // 清除当前用户的所有数据
  clearCurrentUserData() {
    try {
      const keys = this.getAllUserDataKeys()
      keys.forEach(key => localStorage.removeItem(key))
      return true
    } catch (e) {
      console.error('清除用户数据失败:', e)
      return false
    }
  }

  // 获取所有用户列表
  getAllUsers() {
    try {
      const users = localStorage.getItem('users')
      return users ? JSON.parse(users) : []
    } catch (e) {
      return []
    }
  }

  // 检查用户是否存在
  userExists(username) {
    const users = this.getAllUsers()
    return users.some(user => user.username === username)
  }

  // 获取用户头像
  getUserAvatar(username) {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
  }

  // 保存用户活动记录
  saveUserActivity(activity) {
    const key = 'userActivities'
    const activities = this.getUserData(key, [])
    activities.unshift({
      id: Date.now(),
      ...activity,
      timestamp: new Date().toISOString()
    })
    
    // 只保留最近20条记录
    const limitedActivities = activities.slice(0, 20)
    this.saveUserData(key, limitedActivities)
  }

  // 获取用户活动记录
  getUserActivities() {
    return this.getUserData('userActivities', [])
  }

  // 保存考勤记录
  saveAttendanceRecords(records) {
    this.saveUserData('attendanceRecords', records)
  }

  // 获取考勤记录
  getAttendanceRecords() {
    return this.getUserData('attendanceRecords', [])
  }

  // 保存审批记录
  saveApprovalRecords(records) {
    this.saveUserData('approvalRecords', records)
  }

  // 获取审批记录
  getApprovalRecords() {
    return this.getUserData('approvalRecords', [])
  }

  // 保存文件记录
  saveFileRecords(records) {
    this.saveUserData('fileRecords', records)
  }

  // 获取文件记录
  getFileRecords() {
    return this.getUserData('fileRecords', [])
  }

  // 保存个人信息
  saveUserProfile(profile) {
    this.saveUserData('savedUserInfo', profile)
  }

  // 获取个人信息
  getUserProfile() {
    return this.getUserData('savedUserInfo', null)
  }

  // 保存最近活动
  saveRecentActivities(activities) {
    this.saveUserData('recentActivities', activities)
  }

  // 获取最近活动
  getRecentActivities() {
    return this.getUserData('recentActivities', [])
  }

  // 导出用户数据（用于备份）
  exportUserData() {
    const userData = {}
    const keys = this.getAllUserDataKeys()
    
    keys.forEach(key => {
      const baseKey = key.replace(`${this.currentUser}_`, '')
      userData[baseKey] = this.getUserData(baseKey)
    })
    
    return {
      username: this.currentUser,
      exportTime: new Date().toISOString(),
      data: userData
    }
  }

  // 导入用户数据（用于恢复）
  importUserData(importData) {
    try {
      if (importData.username && importData.data) {
        Object.keys(importData.data).forEach(key => {
          this.saveUserData(key, importData.data[key])
        })
        return true
      }
      return false
    } catch (e) {
      console.error('导入用户数据失败:', e)
      return false
    }
  }
}

// 创建全局实例
const userDataManager = new UserDataManager()

// 导出工具函数
export const getUserData = (key, defaultValue = null) => userDataManager.getUserData(key, defaultValue)
export const saveUserData = (key, data) => userDataManager.saveUserData(key, data)
export const removeUserData = (key) => userDataManager.removeUserData(key)
export const saveUserActivity = (activity) => userDataManager.saveUserActivity(activity)
export const getUserActivities = () => userDataManager.getUserActivities()
export const saveAttendanceRecords = (records) => userDataManager.saveAttendanceRecords(records)
export const getAttendanceRecords = () => userDataManager.getAttendanceRecords()
export const saveApprovalRecords = (records) => userDataManager.saveApprovalRecords(records)
export const getApprovalRecords = () => userDataManager.getApprovalRecords()
export const saveFileRecords = (records) => userDataManager.saveFileRecords(records)
export const getFileRecords = () => userDataManager.getFileRecords()
export const saveUserProfile = (profile) => userDataManager.saveUserProfile(profile)
export const getUserProfile = () => userDataManager.getUserProfile()
export const saveRecentActivities = (activities) => userDataManager.saveRecentActivities(activities)
export const getRecentActivities = () => userDataManager.getRecentActivities()
export const setCurrentUser = (username) => userDataManager.setCurrentUser(username)
export const getCurrentUsername = () => userDataManager.currentUser
export const clearCurrentUserData = () => userDataManager.clearCurrentUserData()
export const exportUserData = () => userDataManager.exportUserData()
export const importUserData = (data) => userDataManager.importUserData(data)

export default userDataManager 