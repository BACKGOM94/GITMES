package com.gitmes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.Inventory;
import com.gitmes.model.InventoryLog;
import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.Production;
import com.gitmes.model.Sales;
import com.gitmes.repository.InventoryLogRepository;
import com.gitmes.repository.InventoryRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
	
	private final InventoryRepository inventoryRepository;
	private final InventoryLogRepository inventoryLogRepository;
	
	public Inventory findById(Long inventoryId) {
		return inventoryRepository.findById(inventoryId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 재고가 존재하지 않습니다."));
	}
	
	public List<Inventory> getInventory(Company company,Item item) {
		return inventoryRepository.findByCompanyAndItem(company,item);
	}
	
	public List<Inventory> findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(Company company,BigDecimal quantity) {
		return inventoryRepository.findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(company,quantity);
	}
	public List<InventoryLog> findInventoryLog(Inventory inventory) {
		return inventoryLogRepository.getByInventory(inventory);
	}
    public List<InventoryLog> findInventoryLog(Long companyId, LocalDate startDate, LocalDate endDate, String logType) {
        return inventoryLogRepository.findAll((Specification<InventoryLog>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (companyId != null) {
                predicates.add(cb.equal(root.get("inventory").get("company").get("id"), companyId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            if (logType != null && !"전체".equals(logType)) {
                predicates.add(cb.equal(root.get("logType"), logType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
	
	public BigDecimal getItemQuantity(Company company,Item item) {
		BigDecimal value = new BigDecimal(0);
		List<Inventory> list = getInventory(company, item);
		
		for(Inventory inventory : list) {
			value = value.add(inventory.getQuantity());
		}
		return value;
	}
	
	public InventoryLog findByProductionAndLogType(Production production,String logType) {
		return inventoryLogRepository.findByProductionAndLogType(production,logType);
	}
	
	public void orderSave(Inventory inventory,Order order,BigDecimal quantity) {
		InventoryLog inventoryLog = new InventoryLog();
		inventoryLog.setInventory(inventory);
		inventoryLog.setLogType("입고");
		inventoryLog.setQuantity(quantity);
		inventoryLog.setOrder(order);
		
		inventoryRepository.save(inventory);
		inventoryLogRepository.save(inventoryLog);
	}
	
	public void productSave(Inventory inventory,Production production) {
		InventoryLog inventoryLog = new InventoryLog();
		inventoryLog.setInventory(inventory);
		inventoryLog.setLogType("생산");
		inventoryLog.setQuantity(inventory.getQuantity());
		inventoryLog.setProduction(production);
		
		inventoryRepository.save(inventory);
		inventoryLogRepository.save(inventoryLog);
	}
	
	public void save(Inventory inventory,InventoryLog inventoryLog) {
		inventoryRepository.save(inventory);
		inventoryLogRepository.save(inventoryLog);		
	}
	
	public void inventoryMinus(Company company, Item item, BigDecimal quantity, String material_input_production_id) {
	    // 사용해야 할 총 수량
	    BigDecimal remainingToUse = quantity;
	    
	    while (remainingToUse.compareTo(BigDecimal.ZERO) > 0) {
	        // 수량이 0보다 큰 첫 번째 인벤토리 찾기
	        Inventory inventory = inventoryRepository.findFirstByCompanyAndItemAndQuantityGreaterThanOrderByCreatedAtAsc(company, item, BigDecimal.ZERO);
	        
	        // 더 이상 사용 가능한 인벤토리가 없으면 중단
	        if (inventory == null) {
	            // 재고 부족 처리 (예: 예외 발생 또는 로그 기록)
	            throw new RuntimeException("Item " + item.getItemName() + " out of stock. Still need: " + remainingToUse);
	        }
	        
	        // 현재 인벤토리에서 사용할 수 있는 수량 계산
	        BigDecimal availableQuantity = inventory.getQuantity();
	        BigDecimal quantityToUse;
	        
	        if (availableQuantity.compareTo(remainingToUse) >= 0) {
	            // 현재 인벤토리로 모든 수량을 처리할 수 있는 경우
	            quantityToUse = remainingToUse;
	            inventory.setQuantity(availableQuantity.subtract(remainingToUse));
	            remainingToUse = BigDecimal.ZERO;
	        } else {
	            // 현재 인벤토리로 일부만 처리할 수 있는 경우
	            quantityToUse = availableQuantity;
	            inventory.setQuantity(BigDecimal.ZERO);
	            remainingToUse = remainingToUse.subtract(availableQuantity);
	        }
	        
	        // 인벤토리 로그 생성
	        InventoryLog inventoryLog = new InventoryLog();
	        inventoryLog.setInventory(inventory);
	        inventoryLog.setLogType("자재투입");
	        inventoryLog.setQuantity(quantityToUse);
	        inventoryLog.setMaterialInputProductionId(material_input_production_id);
	        
	        // 인벤토리 및 로그 저장
	        inventoryRepository.save(inventory);
	        inventoryLogRepository.save(inventoryLog);
	    }
	}
	
	public void inventoryMinus(Company company, Item item, BigDecimal quantity, Sales sales) {
	    // 사용해야 할 총 수량
	    BigDecimal remainingToUse = quantity;
	    
	    while (remainingToUse.compareTo(BigDecimal.ZERO) > 0) {
	        // 수량이 0보다 큰 첫 번째 인벤토리 찾기
	        Inventory inventory = inventoryRepository.findFirstByCompanyAndItemAndQuantityGreaterThanOrderByCreatedAtAsc(company, item, BigDecimal.ZERO);
	        
	        // 더 이상 사용 가능한 인벤토리가 없으면 중단
	        if (inventory == null) {
	            // 재고 부족 처리 (예: 예외 발생 또는 로그 기록)
	            throw new RuntimeException("Item " + item.getItemName() + " out of stock. Still need: " + remainingToUse);
	        }
	        
	        // 현재 인벤토리에서 사용할 수 있는 수량 계산
	        BigDecimal availableQuantity = inventory.getQuantity();
	        BigDecimal quantityToUse;
	        
	        if (availableQuantity.compareTo(remainingToUse) >= 0) {
	            // 현재 인벤토리로 모든 수량을 처리할 수 있는 경우
	            quantityToUse = remainingToUse;
	            inventory.setQuantity(availableQuantity.subtract(remainingToUse));
	            remainingToUse = BigDecimal.ZERO;
	        } else {
	            // 현재 인벤토리로 일부만 처리할 수 있는 경우
	            quantityToUse = availableQuantity;
	            inventory.setQuantity(BigDecimal.ZERO);
	            remainingToUse = remainingToUse.subtract(availableQuantity);
	        }
	        
	        // 인벤토리 로그 생성
	        InventoryLog inventoryLog = new InventoryLog();
	        inventoryLog.setInventory(inventory);
	        inventoryLog.setLogType("출고");
	        inventoryLog.setQuantity(quantityToUse);
	        inventoryLog.setSales(sales);
	        
	        // 인벤토리 및 로그 저장
	        inventoryRepository.save(inventory);
	        inventoryLogRepository.save(inventoryLog);
	    }
	}

}
