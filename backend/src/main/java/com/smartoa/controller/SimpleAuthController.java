package com.smartoa.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class SimpleAuthController {

    // Simple in-memory user storage for testing
    private static final Map<String, String> users = new HashMap<>();
    
    static {
        // Add test users with BCrypt hashes
        users.put("admin", "$2a$10$FMjxWMmtWLM79yqe3xv2S.RVmALJGpKp.qkBD5OgkACbk13kDuV3u");
        users.put("manager", "$2a$10$UsqZqKCoBPXV.iRwB8Ti2.ZBbsshnEFZd8ZyYXxm5wXOejl.PCd8i");
        users.put("employee1", "$2a$10$lIogEfki3GJx.SQqsJZyAeuKnm2XHBG8coFe7j629a5AdVHj5UVZy");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (users.containsKey(username)) {
            // For testing, accept any password
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", "test-token-" + username);
            response.put("user", Map.of(
                "username", username,
                "role", username.equals("admin") ? "ADMIN" : "EMPLOYEE"
            ));
        } else {
            response.put("success", false);
            response.put("message", "Username or password incorrect");
        }
        
        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (users.containsKey(username)) {
            response.put("success", false);
            response.put("message", "Username already exists");
        } else {
            users.put(username, password); // In real app, hash the password
            response.put("success", true);
            response.put("message", "Registration successful");
        }
        
        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "Simple Auth Controller is working!";
    }
} 