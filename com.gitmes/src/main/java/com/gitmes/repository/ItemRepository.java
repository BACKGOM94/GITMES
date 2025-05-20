package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.Company;
import com.gitmes.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long>{
	List<Item> findByCompany(Company company);
	List<Item> findByCompanyAndIsProducibleTrue(Company company);
	List<Item> findByCompanyAndIsPurchasableTrue(Company company);
	List<Item> findByCompanyAndIsSellableTrue(Company company);
}
