package com.gitmes.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.Company;
import com.gitmes.model.Inventory;
import com.gitmes.model.Item;


public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	Inventory findFirstByCompanyAndItemAndQuantityGreaterThanOrderByCreatedAtAsc(Company company, Item item, BigDecimal quantity);
	List<Inventory> findByCompanyAndItem(Company company,Item item);
	List<Inventory> findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(Company company, BigDecimal quantity);
}
