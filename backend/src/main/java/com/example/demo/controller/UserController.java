package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        try {
            User updatedUser = userService.updateProfile(user.getId(), email, birthday, avatar);
            
            // 不返回密码
            updatedUser.setPassword(null);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> requestBody) {
        String password = requestBody.get("password");
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("密码不能为空");
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("密码错误");
        }
        
        try {
            userService.deleteUser(user.getId());
            return ResponseEntity.ok().body("账户已成功注销");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("注销账户失败: " + e.getMessage());
        }
    }
} 