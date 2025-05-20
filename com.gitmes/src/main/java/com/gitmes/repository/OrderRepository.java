package com.gitmes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gitmes.model.Company;
import com.gitmes.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {	
	List<Order> findByCompanyAndOrderDate(Company company,LocalDate orderDate);
	List<Order> findByCompanyAndIsComplete(Company company, int isComplete);
}