package com.smartoa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 任务实体�?
 * 
 * @author 陈志�?
 * @version 1.0.0
 * @since 2024-01-01
 */
public class Task {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 流程实例ID
     */
    private Long processInstanceId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 执行人ID
     */
    private Long assignee;
    
    /**
     * 执行人姓�?
     */
    private String assigneeName;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    
    /**
     * 优先�?0:普�?1:重要 2:紧�?
     */
    private Integer priority;
    
    /**
     * 状�?1:待办 2:进行�?3:已完�?
     */
    private Integer status;
    
    /**
     * 流程实例信息
     */
    private ProcessInstance processInstance;
    
    /**
     * 表单数据
     */
    private String formData;

    // 构造函�?
    public Task() {}

    public Task(Long id, String taskName, Long assignee) {
        this.id = id;
        this.taskName = taskName;
        this.assignee = assignee;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getAssignee() {
        return assignee;
    }

    public void setAssignee(Long assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", processInstanceId=" + processInstanceId +
                ", taskName='" + taskName + '\'' +
                ", assignee=" + assignee +
                ", assigneeName='" + assigneeName + '\'' +
                ", createTime=" + createTime +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
} 
