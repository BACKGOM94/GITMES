package com.gitmes.dto;

import java.time.LocalDate;

import com.gitmes.model.Item;
import com.gitmes.model.Production;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDto {

	private Long ProductionId;
	private Long itemId;
	private String itemName;
	private String itemUnit;
	private Integer quantity;
	private String memo;
	private Integer productionSequence;	
	private String productionStage;
	private LocalDate productionDate;
	
	public ProductionDto(Production production) {
		this.ProductionId = production.getId();
		this.itemId = production.getItem().getId();
		this.itemName = production.getItem().getItemName();
		this.itemUnit = production.getItem().getUnit();
		this.quantity = production.getQuantity();
		this.memo = production.getMemo();
		this.productionSequence = production.getProductionSequence();
		this.productionDate = production.getProductionDate();
		if (this.productionSequence == -1) this.productionStage = "대기중";
		else if  (this.productionSequence == 0) this.productionStage = "완료";
		else this.productionStage = production.getItem().getProcess().getTaskMappings().get(production.getProductionSequence() - 1).getTask().getName();		
	}
}
