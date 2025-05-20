package com.gitmes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.Defect;
import com.gitmes.model.Item;
import com.gitmes.model.Production;
import com.gitmes.model.Task;
import com.gitmes.repository.DefectRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefectService {
	
	private final DefectRepository defectRepository;
	
	public Defect findByProduction(Production production) {
	    return defectRepository.findByProduction(production);
	}
	
	public Defect findById(Long defectId) {
	    return defectRepository.findById(defectId)
	    		.orElseThrow(() -> new IllegalArgumentException("해당 ID의 불량 내역이 존재하지 않습니다."));
	}
	
	public void save(Defect defect) {
		defectRepository.save(defect);
	}
	
    public List<Defect> findDefects(Long companyId, LocalDate startDate, LocalDate endDate, Long itemId, Long taskId) {
        return defectRepository.findAll((Specification<Defect>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (companyId != null) {
                predicates.add(cb.equal(root.get("production").get("company").get("id"), companyId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("production").get("productionDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("production").get("productionDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("production").get("productionDate"), endDate));
            }

            if (itemId != null) {
                predicates.add(cb.equal(root.get("production").get("item").get("id"), itemId));
            }
            
            if (taskId != null) {
                predicates.add(cb.equal(root.get("task").get("id"), taskId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
