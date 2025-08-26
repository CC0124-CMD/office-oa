package com.smartoa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 流程实例实体�?
 * 
 * @author 陈志�?
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ProcessInstance {
    
    /**
     * 流程实例ID
     */
    private Long id;
    
    /**
     * 流程定义ID
     */
    private Long processDefinitionId;
    
    /**
     * 流程定义名称
     */
    private String processDefinitionName;
    
    /**
     * 业务标识
     */
    private String businessKey;
    
    /**
     * 发起人ID
     */
    private Long initiator;
    
    /**
     * 发起人姓�?
     */
    private String initiatorName;
    
    /**
     * 开始时�?
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 状�?1:进行�?2:已完�?3:已终�?
     */
    private Integer status;
    
    /**
     * 当前任务名称
     */
    private String currentTaskName;
    
    /**
     * 当前执行�?
     */
    private String currentAssignee;

    // 构造函�?
    public ProcessInstance() {}

    public ProcessInstance(Long id, String businessKey, Long initiator) {
        this.id = id;
        this.businessKey = businessKey;
        this.initiator = initiator;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(Long processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Long getInitiator() {
        return initiator;
    }

    public void setInitiator(Long initiator) {
        this.initiator = initiator;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    public String getCurrentAssignee() {
        return currentAssignee;
    }

    public void setCurrentAssignee(String currentAssignee) {
        this.currentAssignee = currentAssignee;
    }

    @Override
    public String toString() {
        return "ProcessInstance{" +
                "id=" + id +
                ", processDefinitionId=" + processDefinitionId +
                ", processDefinitionName='" + processDefinitionName + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", initiator=" + initiator +
                ", initiatorName='" + initiatorName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", currentTaskName='" + currentTaskName + '\'' +
                ", currentAssignee='" + currentAssignee + '\'' +
                '}';
    }
} 
