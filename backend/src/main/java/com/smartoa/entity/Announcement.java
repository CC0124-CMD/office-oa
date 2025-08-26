package com.smartoa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * ÂÖ¨ÂëäÂÆû‰ΩìÁ±?
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
        LOW(0, "‰Ω?),
        NORMAL(1, "ÊôÆÈÄ?),
        HIGH(2, "È´?),
        URGENT(3, "Á¥ßÊÄ?);
        
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
        DRAFT(0, "ËçâÁ®ø"),
        PUBLISHED(1, "Â∑≤ÂèëÂ∏?),
        ARCHIVED(2, "Â∑≤ÂΩíÊ°?);
        
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
