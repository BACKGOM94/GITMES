package com.gitmes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitmes.model.Company;
import com.gitmes.model.Item;
import com.gitmes.model.Sales;
import com.gitmes.model.SalesData;
import com.gitmes.repository.SalesDataRepository;
import com.gitmes.repository.SalesRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {
	
    private final SalesRepository salesRepository;
    private final SalesDataRepository salesDataRepository;
    
    public List<Sales> today_SalesList(Company company){
    	return salesRepository.findByCompanyAndSaleDate(company, LocalDate.now());
    }
    
    public List<Sales> getSalesByDate(Company company,LocalDate date){
    	return salesRepository.findByCompanyAndSaleDate(company, date);
    }
    
    public List<Sales> findByCompanyAndIsComplete(Company company,int isComplete){
    	return salesRepository.findByCompanyAndIsComplete(company, isComplete);
    }
    
    public SalesData findBySalesAndItem(Sales sales,Item item){
    	return salesDataRepository.findBySalesAndItem(sales, item);
    }
    
    public Sales getSales(Long salesId) {
    	return salesRepository.findById(salesId)
    			.orElseThrow(() -> new UsernameNotFoundException("수주서를 찾을 수 없습니다."));
    }
    public List<SalesData> getSalesData(Sales sales) {
    	return salesDataRepository.findBySales(sales);
    }
    
    public List<Sales> findSales(Long companyId, LocalDate startDate, LocalDate endDate, Long clientId) {
        return salesRepository.findAll((Specification<Sales>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("id"), companyId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("saleDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("saleDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("saleDate"), endDate));
            }

            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
    
    public void insertSales(Sales sales) {
    	salesRepository.save(sales);
    }    
    public void insertSalesData(SalesData salesData) {
    	salesDataRepository.save(salesData);
    }
    
    @Transactional
    public void deleteSalesData(Sales sales) {
    	salesDataRepository.deleteBySales(sales);
    }
    
    @Transactional
    public void deleteSales(Sales sales) {
    	salesRepository.delete(sales);
    }
}
