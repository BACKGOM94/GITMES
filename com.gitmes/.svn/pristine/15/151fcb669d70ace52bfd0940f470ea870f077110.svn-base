package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gitmes.model.Inventory;
import com.gitmes.model.InventoryLog;
import com.gitmes.model.Production;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long>, JpaSpecificationExecutor<InventoryLog>{
	InventoryLog findByProductionAndLogType(Production production,String logType);
	List<InventoryLog> getByInventory(Inventory inventory);
}
