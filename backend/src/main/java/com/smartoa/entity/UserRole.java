package com.smartoa.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体�?
 * 
 * @author SmartOA
 * @since 2025-08-06
 */
@Entity
@Table(name = "user_roles", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    /**
     * 关联ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 角色ID
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 分配时间
     */
    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    /**
     * 分配人ID
     */
    @Column(name = "assigned_by")
    private Long assignedBy;

    /**
     * 状态：1-有效�?-无效
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 用户实体关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 角色实体关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    /**
     * 状态枚�?
     */
    public enum Status {
        INACTIVE(0, "无效"),
        ACTIVE(1, "有效");

        private final Integer value;
        private final String description;

        Status(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 构造函�?- 用于创建用户角色关联
     */
    public UserRole(Long userId, Long roleId, Long assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
        this.status = Status.ACTIVE.getValue();
    }

    /**
     * 检查关联是否有�?
     */
    public boolean isActive() {
        return Status.ACTIVE.getValue().equals(this.status);
    }

    /**
     * 激活关�?
     */
    public void activate() {
        this.status = Status.ACTIVE.getValue();
    }

    /**
     * 停用关联
     */
    public void deactivate() {
        this.status = Status.INACTIVE.getValue();
    }
}
