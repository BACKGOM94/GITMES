package com.gitmes.model;

import java.time.LocalDateTime;

import com.gitmes.model.enumstyle.ClientType;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients", uniqueConstraints = {
    @UniqueConstraint(name = "uq_clients_company", columnNames = {"company_id", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 거래처 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clients_company"))
    private Company company; // 어느 회사의 거래처인지 구분

    @Column(nullable = false, length = 255)
    private String name; // 거래처명

    @Column(nullable = false, length = 100)
    private String representative; // 대표자명

    @Column(nullable = false, length = 20)
    private String phone; // 연락처

    @Column(length = 255)
    private String email; // 이메일

    @Column(columnDefinition = "TEXT")
    private String address; // 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ClientType type = ClientType.CUSTOMER; // 거래처 유형
    
    @Column(columnDefinition = "TEXT")
    private String note; // 비고

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정일

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
