package com.gitmes.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.gitmes.model.enumstyle.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint COMMENT '사용자 ID (PK)'")
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50, unique = true, columnDefinition = "varchar(50) COMMENT '로그인 ID (고유값)'")
    private String username;
    
    @Column(name = "password", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT '비밀번호 (BCrypt 암호화 필요)'")
    private String password;
    
    @Column(name = "phone", length = 20, columnDefinition = "varchar(20) COMMENT '연락처'")
    private String phone;
    
    @Column(name = "name", nullable = false, length = 50, columnDefinition = "varchar(50) COMMENT '사용자 이름'")
    private String name;
    
    @Column(name = "address", length = 255, columnDefinition = "varchar(255) COMMENT '사용자 주소'")
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "enum('ROLE_USER','ROLE_MANAGER','ROLE_ADMIN') COMMENT '사용자 역할 (일반, 매니저, 관리자)'")
    private UserRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "users_ibfk_1"))
    private Company company;
    
    @Column(name = "enabled", columnDefinition = "tinyint(1) DEFAULT 1 COMMENT '계정 활성화 여부'")
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp DEFAULT current_timestamp() COMMENT '계정 생성일'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '계정 수정일'")
    private LocalDateTime updatedAt;
}