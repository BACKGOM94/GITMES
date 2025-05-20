package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.OrderData;
import com.gitmes.model.Sales;
import com.gitmes.model.SalesData;

public interface SalesDataRepository extends JpaRepository<SalesData, Long> {
	List<SalesData> findBySales(Sales sales);
	SalesData findBySalesAndItem(Sales sales,Item item);
	void deleteBySales(Sales sales);
}