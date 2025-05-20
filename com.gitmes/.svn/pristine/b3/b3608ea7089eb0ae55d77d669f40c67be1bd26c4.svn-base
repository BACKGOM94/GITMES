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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '재고 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "BIGINT COMMENT '아이템 ID (FK)'")
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, columnDefinition = "BIGINT COMMENT '회사 ID (FK)'")
    private Company company;
    
    @Column(name = "quantity", nullable = false, columnDefinition = "INT COMMENT '현재 재고 수량'")
    private BigDecimal quantity;
    
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "inventory")
    private List<InventoryLog> inventoryLogs;
    
    /**
     * 재고 수량 증가
     * 
     * @param amount 증가시킬 수량
     */
    public void increaseQuantity(BigDecimal amount) {
        if (amount.doubleValue() < 0) {
            throw new IllegalArgumentException("증가량은 0보다 커야 합니다.");
        }
        if (this.quantity == null) this.quantity = new BigDecimal(0);
        this.quantity = this.quantity.add(amount);
    }
    
    /**
     * 재고 수량 감소
     * 
     * @param amount 감소시킬 수량
     */
    public void decreaseQuantity(BigDecimal amount) {
        if (amount.doubleValue() < 0) {
            throw new IllegalArgumentException("감소량은 0보다 커야 합니다.");
        }
        if (this.quantity.doubleValue() < amount.doubleValue()) {
            throw new IllegalStateException("현재 재고(" + this.quantity + ")보다 감소량(" + amount + ")이 많습니다.");
        }
        this.quantity = this.quantity.subtract(amount);
    }
    
    /**
     * 재고 수량 직접 설정
     * 
     * @param newQuantity 새로운 재고 수량
     */
    public void setQuantityDirectly(BigDecimal newQuantity) {
        if (newQuantity.doubleValue() < 0) {
            throw new IllegalArgumentException("재고 수량은 0보다 작을 수 없습니다.");
        }
        this.quantity = newQuantity;
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