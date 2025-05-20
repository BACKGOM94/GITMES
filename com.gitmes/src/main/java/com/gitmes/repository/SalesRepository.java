package com.gitmes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gitmes.model.Company;
import com.gitmes.model.Order;
import com.gitmes.model.Sales;

public interface SalesRepository extends JpaRepository<Sales, Long>, JpaSpecificationExecutor<Sales> {	
	List<Sales> findByCompanyAndSaleDate(Company company,LocalDate saleDate);
	List<Sales> findByCompanyAndIsComplete(Company company, int isComplete);
}