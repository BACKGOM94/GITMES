package com.gitmes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '판매 상세 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false, columnDefinition = "BIGINT COMMENT '판매 ID (FK)'")
    private Sales sales;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "BIGINT COMMENT '아이템 ID (FK)'")
    private Item item;
    
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2) COMMENT '단가'")
    private BigDecimal unitPrice;
    
    @Column(name = "quantity", nullable = false, columnDefinition = "INT COMMENT '판매 예정 수량'")
    private Integer quantity;
    
    @Column(name = "shipped_quantity", columnDefinition = "INT DEFAULT 0 COMMENT '실제 입고된 수량'")
    private Integer shippedQuantity = 0;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'")
    private LocalDateTime updatedAt;
    
    /**
     * 판매 항목의 총액 계산
     * 
     * @return 단가 * 판매 예정 수량의 총액
     */
    public BigDecimal getTotalAmount() {
        return this.unitPrice.multiply(new BigDecimal(this.quantity));
    }
    
    /**
     * 출고 완료된 금액 계산
     * 
     * @return 단가 * 실제 출고 수량의 총액
     */
    public BigDecimal getShippedAmount() {
        return this.unitPrice.multiply(new BigDecimal(this.shippedQuantity));
    }
    
    /**
     * 남은 출고 수량 계산
     * 
     * @return 남은 출고 수량
     */
    public Integer getRemainingQuantity() {
        return this.quantity - this.shippedQuantity;
    }
    
    /**
     * 출고 완료 여부 확인
     * 
     * @return 출고 완료 여부
     */
    public boolean isFullyShipped() {
        return this.quantity.equals(this.shippedQuantity);
    }
    
    /**
     * 출고 수량 증가
     * 
     * @param amount 증가시킬 출고 수량
     */
    public void addShippedQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("출고 수량은 0보다 커야 합니다.");
        }
        
        int newShippedQuantity = this.shippedQuantity + amount;
        
        if (newShippedQuantity > this.quantity) {
            throw new IllegalStateException(
                    "출고 수량(" + newShippedQuantity + ")이 판매 예정 수량(" + this.quantity + ")을 초과할 수 없습니다.");
        }
        
        this.shippedQuantity = newShippedQuantity;
    }
    
    /**
     * 출고 진행률 계산 (백분율)
     * 
     * @return 출고 진행률 (0-100%)
     */
    public int getShippingProgressPercentage() {
        if (this.quantity == 0) {
            return 0;
        }
        return (int) (((double) this.shippedQuantity / this.quantity) * 100);
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}