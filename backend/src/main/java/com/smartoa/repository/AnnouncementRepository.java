package com.smartoa.repository;

import com.smartoa.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告Repository
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    /**
     * 根据状态查询公�?
     */
    Page<Announcement> findByStatusOrderByCreateTimeDesc(Announcement.Status status, Pageable pageable);
    
    /**
     * 根据优先级查询公�?
     */
    List<Announcement> findByPriorityAndStatusOrderByCreateTimeDesc(Announcement.Priority priority, Announcement.Status status);
    
    /**
     * 根据发布者查询公�?
     */
    Page<Announcement> findByPublisherIdOrderByCreateTimeDesc(Long publisherId, Pageable pageable);
    
    /**
     * 根据标题模糊查询
     */
    Page<Announcement> findByTitleContainingAndStatusOrderByCreateTimeDesc(String title, Announcement.Status status, Pageable pageable);
    
    /**
     * 查询最近发布的公告
     */
    @Query("SELECT a FROM Announcement a WHERE a.status = 1 AND a.createTime >= :startTime ORDER BY a.createTime DESC")
    List<Announcement> findRecentAnnouncements(@org.springframework.data.repository.query.Param("startTime") LocalDateTime startTime);
    
    /**
     * 统计各状态的公告数量
     */
    long countByStatus(Announcement.Status status);
    
    /**
     * 查询高优先级公告
     */
    List<Announcement> findByPriorityInAndStatusOrderByCreateTimeDesc(List<Announcement.Priority> priorities, Announcement.Status status);
} 
