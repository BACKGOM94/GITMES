package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.OrderData;

public interface OrderDataRepository extends JpaRepository<OrderData, Long> {
	List<OrderData> findByOrder(Order order);
	OrderData findByOrderAndItem(Order order,Item item);
	void deleteByOrder(Order order);
}