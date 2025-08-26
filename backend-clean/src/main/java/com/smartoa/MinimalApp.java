package com.smartoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class MinimalApp {

    public static void main(String[] args) {
        SpringApplication.run(MinimalApp.class, args);
    }

    @Configuration
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/test/**").permitAll()
                .anyRequest().authenticated();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("*");
            configuration.addAllowedMethod("*");
            configuration.addAllowedHeader("*");
            
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }

    @RestController
    @RequestMapping("/api/auth")
    public static class AuthController {

        private final PasswordEncoder passwordEncoder;
        private final Map<String, String> users = new HashMap<>();

        public AuthController(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            // Initialize test users
            users.put("admin", passwordEncoder.encode("admin123"));
            users.put("manager", passwordEncoder.encode("manager123"));
            users.put("employee1", passwordEncoder.encode("123456"));
        }

        @PostMapping("/login")
        public Map<String, Object> login(@RequestBody Map<String, String> request) {
            String username = request.get("username");
            String password = request.get("password");
            
            Map<String, Object> response = new HashMap<>();
            
            if (users.containsKey(username) && passwordEncoder.matches(password, users.get(username))) {
                // 创建用户信息
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("role", username.equals("admin") ? "ADMIN" : "EMPLOYEE");
                
                // 创建数据包装器
                Map<String, Object> data = new HashMap<>();
                data.put("token", "test-token-" + username);
                data.put("user", userInfo);
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
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
                response.put("message", "用户名已存在");
            } else {
                users.put(username, passwordEncoder.encode(password));
                response.put("success", true);
                response.put("message", "注册成功");
            }
            
            return response;
        }

        @GetMapping("/me")
        public Map<String, Object> getCurrentUser() {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Backend service is running");
            response.put("data", new HashMap<>());
            return response;
        }
    }

    @RestController
    @RequestMapping("/test")
    public static class TestController {

        @GetMapping("/health")
        public String health() {
            return "OK";
        }

        @GetMapping("/users")
        public Map<String, String> getUsers() {
            return new HashMap<>();
        }
    }
} 