package com.smartoa.service.impl;

import com.smartoa.entity.Announcement;
import com.smartoa.repository.AnnouncementRepository;
import com.smartoa.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告服务实现�?
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        // 设置默认状态为草稿
        announcement.setStatus(Announcement.Status.DRAFT);
        return announcementRepository.save(announcement);
    }
    
    @Override
    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        Announcement existing = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
        
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setPriority(announcement.getPriority());
        
        return announcementRepository.save(existing);
    }
    
    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
        announcementRepository.delete(announcement);
    }
    
    @Override
    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
    }
    
    @Override
    public Page<Announcement> getAnnouncementsByPage(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }
    
    @Override
    public Page<Announcement> getAnnouncementsByStatus(Announcement.Status status, Pageable pageable) {
        return announcementRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
    }
    
    @Override
    public Page<Announcement> getAnnouncementsByPublisher(Long publisherId, Pageable pageable) {
        return announcementRepository.findByPublisherIdOrderByCreateTimeDesc(publisherId, pageable);
    }
    
    @Override
    public Page<Announcement> searchAnnouncementsByTitle(String title, Pageable pageable) {
        return announcementRepository.findByTitleContainingAndStatusOrderByCreateTimeDesc(title, Announcement.Status.PUBLISHED, pageable);
    }
    
    @Override
    public List<Announcement> getRecentAnnouncements(int limit) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7); // 最�?�?
        return announcementRepository.findRecentAnnouncements(startTime);
    }
    
    @Override
    public List<Announcement> getHighPriorityAnnouncements() {
        List<Announcement.Priority> highPriorities = Arrays.asList(
                Announcement.Priority.HIGH, 
                Announcement.Priority.URGENT
        );
        return announcementRepository.findByPriorityInAndStatusOrderByCreateTimeDesc(highPriorities, Announcement.Status.PUBLISHED);
    }
    
    @Override
    public Announcement publishAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
        
        announcement.setStatus(Announcement.Status.PUBLISHED);
        return announcementRepository.save(announcement);
    }
    
    @Override
    public Announcement withdrawAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
        
        announcement.setStatus(Announcement.Status.DRAFT);
        return announcementRepository.save(announcement);
    }
    
    @Override
    public Announcement archiveAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存�?));
        
        announcement.setStatus(Announcement.Status.ARCHIVED);
        return announcementRepository.save(announcement);
    }
    
    @Override
    public long countAnnouncementsByStatus(Announcement.Status status) {
        return announcementRepository.countByStatus(status);
    }
    
    @Override
    public Map<String, Object> getAnnouncementStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态公告数�?
        long draftCount = announcementRepository.countByStatus(Announcement.Status.DRAFT);
        long publishedCount = announcementRepository.countByStatus(Announcement.Status.PUBLISHED);
        long archivedCount = announcementRepository.countByStatus(Announcement.Status.ARCHIVED);
        
        // 统计各优先级公告数量
        long highPriorityCount = announcementRepository.findByPriorityAndStatusOrderByCreateTimeDesc(
                Announcement.Priority.HIGH, Announcement.Status.PUBLISHED).size();
        long urgentCount = announcementRepository.findByPriorityAndStatusOrderByCreateTimeDesc(
                Announcement.Priority.URGENT, Announcement.Status.PUBLISHED).size();
        
        statistics.put("draftCount", draftCount);
        statistics.put("publishedCount", publishedCount);
        statistics.put("archivedCount", archivedCount);
        statistics.put("highPriorityCount", highPriorityCount);
        statistics.put("urgentCount", urgentCount);
        statistics.put("totalCount", draftCount + publishedCount + archivedCount);
        
        return statistics;
    }
} 
