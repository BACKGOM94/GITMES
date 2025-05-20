package com.gitmes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint COMMENT '발주 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "order_ibfk_1"))
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clients_id", nullable = false, foreignKey = @ForeignKey(name = "order_ibfk_2"))
    private Client client;
    
    @Column(name = "order_date", nullable = false, columnDefinition = "date COMMENT '발주일자'")
    private LocalDate orderDate;
    
    @Column(name = "delivery_date", columnDefinition = "date COMMENT '입고일자'")
    private LocalDate deliveryDate;

    @Column(name = "is_complete", nullable = false, columnDefinition = "tinyint DEFAULT 2 COMMENT '완료 여부 (1: 완료, 2: 미완료)'")
    private Integer isComplete = 2;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "datetime DEFAULT current_timestamp() COMMENT '생성 일시'") 
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정 일시'")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderData> orderDataList = new ArrayList<>();
}