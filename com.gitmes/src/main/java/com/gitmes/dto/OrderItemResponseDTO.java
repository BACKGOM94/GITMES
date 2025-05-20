package com.gitmes.dto;

import java.math.BigDecimal;

import com.gitmes.model.OrderData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private String itemName;
    private int quantity;
    private Long itemId;
    private String itemUnit;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public OrderItemResponseDTO(OrderData data) {
        this.itemName = data.getItem().getItemName();
        this.quantity = data.getQuantity();
        this.itemId = data.getItem().getId();
        this.itemUnit = data.getItem().getUnit();
        this.unitPrice = data.getUnitPrice();
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
