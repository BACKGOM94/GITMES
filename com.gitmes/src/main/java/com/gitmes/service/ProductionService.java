package com.gitmes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.Production;
import com.gitmes.model.Task;
import com.gitmes.repository.ProductionRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionService {
	
	private final ProductionRepository productionRepository;
	
	public List<LocalDate> findproductionDate(Long companyId){
		return productionRepository.findProductionDates(companyId);
	}
	
	public List<LocalDate> findproductionDates(Long companyId, LocalDate startDate, LocalDate endDate){
		return productionRepository.findProductionDates(companyId, startDate, endDate);
	}
	
	public Production findById(Long id) {
		return productionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 생산지시서가 존재하지 않습니다."));
	}
	
	public List<Production> findByCompanyAndProductionDate(Company company, LocalDate productionDate) {
		return productionRepository.findByCompanyAndProductionDate(company, productionDate);
	}
	
	public List<Production> findByCompanyAndProductionDateAndProductionSequence(Company company, LocalDate productionDate,int productionSequence) {
		return productionRepository.findByCompanyAndProductionDateAndProductionSequence(company, productionDate,productionSequence);
	}
	
	public List<Production> findByCompanyAndProductionSequence(Company company) {
		return productionRepository.findByCompanyAndProductionSequence(company,-1);
	}
	
	public Production save(Production production) {		
		return productionRepository.save(production);
	}
	
	public List<Production> findByCompanyIdAndTaskId(Company company,Long taskId){		
		return productionRepository.findByCompanyIdAndTaskId(company.getId(),taskId);
	}
	
    public List<Production> findproductions(Long companyId, LocalDate startDate, LocalDate endDate, Long itemId) {
        return productionRepository.findAll((Specification<Production>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("id"), companyId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("productionDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("productionDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("productionDate"), endDate));
            }

            if (itemId != null) {
                predicates.add(cb.equal(root.get("item").get("id"), itemId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
	
	@Transactional
	public void deleteByCompanyAndProductionDate(Company company, LocalDate productionDate) {
		productionRepository.deleteByCompanyAndProductionDate(company,productionDate);
	}
}
