package com.gitmes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint COMMENT '발주 상세 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "order_data_ibfk_1"))
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "order_data_ibfk_2"))
    private Item item;
    
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2, columnDefinition = "decimal(12,2) COMMENT '단가'")
    private BigDecimal unitPrice;
    
    @Column(name = "quantity", nullable = false, columnDefinition = "int COMMENT '입고 예정 수량'")
    private Integer quantity;
    
    @Column(name = "received_quantity", columnDefinition = "int DEFAULT 0 COMMENT '실제 입고된 수량'")
    private Integer receivedQuantity = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "datetime DEFAULT current_timestamp() COMMENT '생성 일시'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정 일시'")
    private LocalDateTime updatedAt;
}