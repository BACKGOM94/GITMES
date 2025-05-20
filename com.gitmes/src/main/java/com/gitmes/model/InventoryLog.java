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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '재고 로그 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false, columnDefinition = "BIGINT COMMENT '재고 ID (FK)'")
    private Inventory inventory;
    
    @Column(name = "log_type", length = 20, nullable = false, columnDefinition = "VARCHAR(20) COMMENT '로그 유형 (입고, 생산, 불량,자재투입, 출고, 재고조정)'")
    private String logType;
    
    @Column(name = "quantity", nullable = false, columnDefinition = "INT COMMENT '변동 수량'")
    private BigDecimal quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", columnDefinition = "BIGINT DEFAULT NULL COMMENT '관련 발주 ID (nullable)'")
    private Order order;
    
    @Column(name = "material_input_production_id", length = 100, columnDefinition = "VARCHAR(100) DEFAULT NULL COMMENT '재료투입시 생산ID 모음'")
    private String materialInputProductionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", columnDefinition = "BIGINT DEFAULT NULL COMMENT '관련 생산 ID (nullable)'")
    private Production production;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", columnDefinition = "BIGINT DEFAULT NULL COMMENT '관련 판매 ID (nullable)'")
    private Sales sales;
    
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'")
    private LocalDateTime createdAt;
    
    /**
     * 로그 유형 상수 정의
     */
    public static final String LOG_TYPE_INPUT = "입고";
    public static final String LOG_TYPE_PRODUCTION = "생산";
    public static final String LOG_TYPE_DEFECT = "불량";
    public static final String LOG_TYPE_MATERIAL = "자재투입";
    public static final String LOG_TYPE_OUTPUT = "출고";
    public static final String LOG_TYPE_ADJUSTMENT = "재고조정";
    
    /**
     * 로그 유형에 따른 설명 반환
     * 
     * @return 로그 유형 설명
     */
    public String getLogTypeDescription() {
        switch (this.logType) {
            case LOG_TYPE_INPUT:
                return "입고 (재고 증가)";
            case LOG_TYPE_PRODUCTION:
                return "생산 (재고 증가)";
            case LOG_TYPE_MATERIAL:
            	return "자재투입 (재고 감소)";
            case LOG_TYPE_OUTPUT:
                return "출고 (재고 감소)";
            case LOG_TYPE_ADJUSTMENT:
                return "재고조정 (수량 조정)";
            default:
                return "알 수 없는 유형";
        }
    }
    
    /**
     * 로그 출처 정보 반환
     * 
     * @return 로그 출처 정보 문자열
     */
    public String getSourceInfo() {
        if (order != null) {
            return "발주 ID: " + order.getId();
        } else if (production != null) {
            return "생산 ID: " + production.getId();
        } else if (sales != null) {
            return "판매 ID: " + sales.getId();
        } else {
            return "직접 조정";
        }
    }
        
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}