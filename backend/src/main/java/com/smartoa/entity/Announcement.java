package com.smartoa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 公告实体�?
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_announcement")
public class Announcement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "priority")
    private Priority priority = Priority.NORMAL;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status = Status.PUBLISHED;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    public enum Priority {
        LOW(0, "�?),
        NORMAL(1, "普�?),
        HIGH(2, "�?),
        URGENT(3, "紧�?);
        
        private final int code;
        private final String description;
        
        Priority(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Status {
        DRAFT(0, "草稿"),
        PUBLISHED(1, "已发�?),
        ARCHIVED(2, "已归�?);
        
        private final int code;
        private final String description;
        
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 
