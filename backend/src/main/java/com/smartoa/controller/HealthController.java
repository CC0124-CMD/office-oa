package com.smartoa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping("/check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "智能OA系统后端服务运行正常");
        result.put("timestamp", System.currentTimeMillis());
        result.put("version", "1.0.0");
        return result;
    }
    
    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("system", "智能OA系统");
        info.put("version", "1.0.0");
        info.put("author", "陈志�?);
        info.put("java", System.getProperty("java.version"));
        info.put("os", System.getProperty("os.name"));
        info.put("port", "8080");
        return info;
    }
} 
