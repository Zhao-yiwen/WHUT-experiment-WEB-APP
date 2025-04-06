package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Transactional
    public User register(String username, String password, String email, String birthday, MultipartFile avatar) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userMapper.findByEmail(email) != null) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        // 设置余额，admin用户初始余额为500，其他用户为0
        if ("admin".equals(username)) {
            user.setBalance(new BigDecimal("500.00"));
        } else {
            user.setBalance(new BigDecimal("0.00"));
        }

        if (birthday != null && !birthday.isEmpty()) {
            user.setBirthday(LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        // 处理头像上传
        if (avatar != null && !avatar.isEmpty()) {
            String fileName = fileStorageService.storeFile(avatar);
            user.setAvatar(fileStorageService.getFileUrl(fileName));
        }

        userMapper.insert(user);
        return user;
    }

    @Transactional
    public User updateProfile(Long userId, String email, String birthday, MultipartFile avatar) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (email != null && !email.equals(user.getEmail())) {
            // 检查新邮箱是否已存在
            if (userMapper.findByEmail(email) != null) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(email);
        }

        if (birthday != null && !birthday.isEmpty()) {
            user.setBirthday(LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        // 处理头像上传
        if (avatar != null && !avatar.isEmpty()) {
            // 如果用户已有头像，则删除旧头像
            if (user.getAvatar() != null) {
                String oldFileName = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
                fileStorageService.deleteFile(oldFileName);
            }

            String fileName = fileStorageService.storeFile(avatar);
            user.setAvatar(fileStorageService.getFileUrl(fileName));
        }

        userMapper.update(user);
        return user;
    }

    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
        return true;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 删除用户头像文件（如果有）
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                String fileName = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
                fileStorageService.deleteFile(fileName);
            } catch (Exception e) {
                // 日志记录异常但继续执行
                System.err.println("删除用户头像失败: " + e.getMessage());
            }
        }
        
        // 删除用户记录
        userMapper.deleteById(userId);
    }
} 