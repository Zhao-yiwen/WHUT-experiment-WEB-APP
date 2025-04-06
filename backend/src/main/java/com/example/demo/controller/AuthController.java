package com.example.demo.controller;

import com.example.demo.config.JwtConfig;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtConfig.generateToken((UserDetails) authentication.getPrincipal());
        
        User user = userService.findByUsername(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        
        // 不返回密码
        user.setPassword(null);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        
        try {
            User user = userService.register(username, password, email, birthday, avatar);
            
            // 不返回密码
            user.setPassword(null);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordRequest) {
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        boolean updated = userService.updatePassword(user.getId(), oldPassword, newPassword);
        
        if (updated) {
            return ResponseEntity.ok().body("密码更新成功");
        } else {
            return ResponseEntity.badRequest().body("原密码错误");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT是无状态的，客户端只需要删除token即可
        // 这里仅作为API端点，方便前端调用
        return ResponseEntity.ok().body("注销成功");
    }
} 