package com.gitmes.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '아이템 고유 ID'")
    private Long id;

    @Column(name = "item_name", length = 100, nullable = false, columnDefinition = "VARCHAR(100) COMMENT '아이템 이름'")
    private String itemName;

    @Column(name = "category", length = 50, columnDefinition = "VARCHAR(50) COMMENT '아이템 카테고리'")
    private String category;

    @Column(name = "unit", length = 20, columnDefinition = "VARCHAR(20) COMMENT '단위 (예: 개, kg, m 등)'")
    private String unit;

    @Column(name = "price", precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0 COMMENT '기본 가격'")
    private BigDecimal price = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, columnDefinition = "BIGINT COMMENT '회사 ID (companies 테이블 참조)'")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", columnDefinition = "BIGINT NULL COMMENT '공정 ID (processes 테이블 참조, NULL 가능)'")
    private Process process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", columnDefinition = "BIGINT DEFAULT NULL COMMENT '발주 시 변환될 아이템 ID (셀프 조인)'")
    private Item orderItem;

    @OneToMany(mappedBy = "orderItem")
    private List<Item> relatedItems;

    @Column(name = "conversion_factor", precision = 10, scale = 4, columnDefinition = "DECIMAL(10,4) DEFAULT 1.0 COMMENT '환산 계수 (발주 시 변환 비율)'")
    private BigDecimal conversionFactor = BigDecimal.ONE;

    @Column(name = "is_purchasable", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '발주 가능 여부'")
    private Boolean isPurchasable = true;

    @Column(name = "is_producible", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '생산 가능 여부'")
    private Boolean isProducible = true;

    @Column(name = "is_sellable", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '판매 가능 여부'")
    private Boolean isSellable = true;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '아이템 사용 여부'")
    private Boolean isActive = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'")
    private LocalDateTime updatedAt;

    public String getFormattedPrice() {
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        return format.format(this.price) + " 원";
    }

    public String getFormattedConversionFactor() {
        return String.format("%.2f", this.conversionFactor);
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