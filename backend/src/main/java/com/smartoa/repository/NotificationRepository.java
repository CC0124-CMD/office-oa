package com.smartoa.repository;

import com.smartoa.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知Repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据用户ID查询通知
     */
    Page<Notification> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和类型查询通知
     */
    Page<Notification> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, Notification.Type type, Pageable pageable);
    
    /**
     * 查询用户未读通知
     */
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 统计用户未读通知数量
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * 根据类型统计用户未读通知数量
     */
    long countByUserIdAndTypeAndIsReadFalse(Long userId, Notification.Type type);
    
    /**
     * 标记通知为已�?
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
    
    /**
     * 批量标记通知为已�?
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有通知
     */
    void deleteByUserId(Long userId);
    
    /**
     * 查询最近的通知
     */
    List<Notification> findTop10ByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据类型查询通知
     */
    List<Notification> findByTypeOrderByCreateTimeDesc(Notification.Type type);
} 
