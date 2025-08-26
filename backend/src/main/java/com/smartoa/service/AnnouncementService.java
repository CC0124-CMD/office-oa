package com.smartoa.service;

import com.smartoa.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 创建公告
     */
    Announcement createAnnouncement(Announcement announcement);
    
    /**
     * 更新公告
     */
    Announcement updateAnnouncement(Long id, Announcement announcement);
    
    /**
     * 删除公告
     */
    void deleteAnnouncement(Long id);
    
    /**
     * 根据ID查询公告
     */
    Announcement getAnnouncementById(Long id);
    
    /**
     * 分页查询公告
     */
    Page<Announcement> getAnnouncementsByPage(Pageable pageable);
    
    /**
     * 根据状态查询公�?
     */
    Page<Announcement> getAnnouncementsByStatus(Announcement.Status status, Pageable pageable);
    
    /**
     * 根据发布者查询公�?
     */
    Page<Announcement> getAnnouncementsByPublisher(Long publisherId, Pageable pageable);
    
    /**
     * 根据标题搜索公告
     */
    Page<Announcement> searchAnnouncementsByTitle(String title, Pageable pageable);
    
    /**
     * 获取最近发布的公告
     */
    List<Announcement> getRecentAnnouncements(int limit);
    
    /**
     * 获取高优先级公告
     */
    List<Announcement> getHighPriorityAnnouncements();
    
    /**
     * 发布公告
     */
    Announcement publishAnnouncement(Long id);
    
    /**
     * 撤回公告
     */
    Announcement withdrawAnnouncement(Long id);
    
    /**
     * 归档公告
     */
    Announcement archiveAnnouncement(Long id);
    
    /**
     * 统计各状态公告数�?
     */
    long countAnnouncementsByStatus(Announcement.Status status);
    
    /**
     * 获取公告统计信息
     */
    java.util.Map<String, Object> getAnnouncementStatistics();
} 
