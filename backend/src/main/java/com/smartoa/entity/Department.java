package com.smartoa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 部门实体�?
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_department")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dept_name", nullable = false, length = 50)
    private String deptName;
    
    @Column(name = "parent_id")
    private Long parentId = 0L;
    
    @Column(name = "dept_code", length = 50)
    private String deptCode;
    
    @Column(name = "leader", length = 50)
    private String leader;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status = Status.ENABLED;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");
        
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
