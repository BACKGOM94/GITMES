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
import com.gitmes.model.Order;
import com.gitmes.model.OrderData;
import com.gitmes.repository.OrderDataRepository;
import com.gitmes.repository.OrderRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDataRepository orderDataRepository;
    
    public List<Order> today_OrderList(Company company){
    	return orderRepository.findByCompanyAndOrderDate(company, LocalDate.now());
    }
    
    public List<Order> getOrdersByDate(Company company,LocalDate date){
    	return orderRepository.findByCompanyAndOrderDate(company, date);
    }
    
    public List<Order> findByCompanyAndIsComplete(Company company,int isComplete){
    	return orderRepository.findByCompanyAndIsComplete(company, isComplete);
    }
    
    public OrderData findByOrderAndItem(Order order,Item item){
    	return orderDataRepository.findByOrderAndItem(order, item);
    }
    
    public Order getOrder(Long orderId) {
    	return orderRepository.findById(orderId)
    			.orElseThrow(() -> new UsernameNotFoundException("발주서를 찾을 수 없습니다."));
    }
    public List<OrderData> getOrderData(Order order) {
    	return orderDataRepository.findByOrder(order);
    }
    
    public List<Order> findOrders(Long companyId, LocalDate startDate, LocalDate endDate, Long clientId) {
        return orderRepository.findAll((Specification<Order>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("id"), companyId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("orderDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }

            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
    
    public void insertOrder(Order order) {
    	orderRepository.save(order);
    }    
    public void insertOrderData(OrderData orderData) {
    	orderDataRepository.save(orderData);
    }
    
    @Transactional
    public void deleteOrderData(Order order) {
    	orderDataRepository.deleteByOrder(order);
    }
    
    @Transactional
    public void deleteOrder(Order order) {
    	orderRepository.delete(order);
    }
}
