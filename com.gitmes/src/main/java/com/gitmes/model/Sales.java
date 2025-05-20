package com.gitmes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '판매 고유 ID'")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, columnDefinition = "BIGINT COMMENT '회사 ID (FK)'")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clients_id", nullable = false, columnDefinition = "BIGINT COMMENT '거래처 ID (FK)'")
    private Client client;
    
    @Column(name = "sale_date", nullable = false, columnDefinition = "date COMMENT '수주일자'")
    private LocalDate saleDate;
    
    @Column(name = "delivery_date", columnDefinition = "date COMMENT '배송일자'")
    private LocalDate deliveryDate;
    
    @Column(name = "is_complete", nullable = false, columnDefinition = "TINYINT DEFAULT 2 COMMENT '완료 여부 (1: 완료, 2: 미완료)'")
    private Integer isComplete = 2;
    
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "sales")
    private List<SalesData> salesDataList;
    
    /**
     * 완료 상태 상수
     */
    public static final int STATUS_COMPLETE = 1;
    public static final int STATUS_INCOMPLETE = 2;
    
    /**
     * 판매 완료 상태 여부
     * 
     * @return 판매 완료 여부
     */
    public boolean isCompleted() {
        return this.isComplete == STATUS_COMPLETE;
    }
    
    /**
     * 판매 완료로 상태 변경
     */
    public void markAsComplete() {
        this.isComplete = STATUS_COMPLETE;
    }
    
    /**
     * 판매 미완료로 상태 변경
     */
    public void markAsIncomplete() {
        this.isComplete = STATUS_INCOMPLETE;
    }
    
    /**
     * 판매 상태 문자열 반환
     * 
     * @return 판매 상태 문자열
     */
    public String getStatusText() {
        return isCompleted() ? "완료" : "미완료";
    }
    
    /**
     * 총 판매 예정 수량 계산
     * 
     * @return 총 판매 예정 수량
     */
    public int getTotalPlannedQuantity() {
        if (salesDataList == null || salesDataList.isEmpty()) {
            return 0;
        }
        
        return salesDataList.stream()
                .mapToInt(SalesData::getQuantity)
                .sum();
    }
    
    /**
     * 총 출고 수량 계산
     * 
     * @return 총 출고 수량
     */
    public int getTotalShippedQuantity() {
        if (salesDataList == null || salesDataList.isEmpty()) {
            return 0;
        }
        
        return salesDataList.stream()
                .mapToInt(SalesData::getShippedQuantity)
                .sum();
    }
    
    /**
     * 모든 항목이 완전히 출고되었는지 확인
     * 
     * @return 모든 항목 출고 완료 여부
     */
    public boolean isFullyShipped() {
        if (salesDataList == null || salesDataList.isEmpty()) {
            return false;
        }
        
        return salesDataList.stream()
                .allMatch(data -> data.getQuantity().equals(data.getShippedQuantity()));
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