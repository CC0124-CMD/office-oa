package com.smartoa.controller;

import com.smartoa.common.Result;
import com.smartoa.entity.Announcement;
import com.smartoa.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公告管理控制�?
 */
@Slf4j
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Announcement> createAnnouncement(@Validated @RequestBody Announcement announcement) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long publisherId = Long.valueOf(authentication.getName());
            announcement.setPublisherId(publisherId);
            
            Announcement created = announcementService.createAnnouncement(announcement);
            return Result.success("公告创建成功", created);
        } catch (Exception e) {
            log.error("创建公告失败", e);
            return Result.error("创建公告失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @Validated @RequestBody Announcement announcement) {
        try {
            Announcement updated = announcementService.updateAnnouncement(id, announcement);
            return Result.success("公告更新成功", updated);
        } catch (Exception e) {
            log.error("更新公告失败", e);
            return Result.error("更新公告失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("公告删除成功", null);
        } catch (Exception e) {
            log.error("删除公告失败", e);
            return Result.error("删除公告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            return Result.success(announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.error("获取公告详情失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    public Result<Page<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Announcement> announcements;
            
            if (status != null && !status.isEmpty()) {
                Announcement.Status announcementStatus = Announcement.Status.valueOf(status.toUpperCase());
                announcements = announcementService.getAnnouncementsByStatus(announcementStatus, pageable);
            } else {
                announcements = announcementService.getAnnouncementsByPage(pageable);
            }
            
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error("获取公告列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/published")
    public Result<Page<Announcement>> getPublishedAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Announcement> announcements = announcementService.getAnnouncementsByStatus(
                    Announcement.Status.PUBLISHED, pageable);
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取已发布公告失�?, e);
            return Result.error("获取已发布公告失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Page<Announcement>> getMyAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long publisherId = Long.valueOf(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<Announcement> announcements = announcementService.getAnnouncementsByPublisher(publisherId, pageable);
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取我的公告失败", e);
            return Result.error("获取我的公告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public Result<Page<Announcement>> searchAnnouncements(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Announcement> announcements = announcementService.searchAnnouncementsByTitle(title, pageable);
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("搜索公告失败", e);
            return Result.error("搜索公告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/recent")
    public Result<List<Announcement>> getRecentAnnouncements(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Announcement> announcements = announcementService.getRecentAnnouncements(limit);
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取最近公告失�?, e);
            return Result.error("获取最近公告失�? " + e.getMessage());
        }
    }
    
    @GetMapping("/high-priority")
    public Result<List<Announcement>> getHighPriorityAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getHighPriorityAnnouncements();
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取高优先级公告失败", e);
            return Result.error("获取高优先级公告失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Announcement> publishAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.publishAnnouncement(id);
            return Result.success("公告发布成功", announcement);
        } catch (Exception e) {
            log.error("发布公告失败", e);
            return Result.error("发布公告失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Announcement> withdrawAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.withdrawAnnouncement(id);
            return Result.success("公告撤回成功", announcement);
        } catch (Exception e) {
            log.error("撤回公告失败", e);
            return Result.error("撤回公告失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Announcement> archiveAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.archiveAnnouncement(id);
            return Result.success("公告归档成功", announcement);
        } catch (Exception e) {
            log.error("归档公告失败", e);
            return Result.error("归档公告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getAnnouncementStatistics() {
        try {
            Map<String, Object> statistics = announcementService.getAnnouncementStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取公告统计失败", e);
            return Result.error("获取公告统计失败: " + e.getMessage());
        }
    }
} 
