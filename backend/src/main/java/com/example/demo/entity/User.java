package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDate birthday;
    private String avatar;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 