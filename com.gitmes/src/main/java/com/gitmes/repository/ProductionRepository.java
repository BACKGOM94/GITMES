package com.gitmes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gitmes.model.Company;
import com.gitmes.model.Order;
import com.gitmes.model.Production;

public interface ProductionRepository extends JpaRepository<Production, Long>, JpaSpecificationExecutor<Production> {
    
	@Query("SELECT DISTINCT p.productionDate FROM Production p WHERE p.company.id = :companyId AND p.productionSequence <> 0 ORDER BY p.productionDate")
	List<LocalDate> findProductionDates(@Param("companyId") Long companyId);	
	List<Production> findByCompanyAndProductionDateAndProductionSequence(Company company,LocalDate productionDate,Integer productionSequence);
	List<Production> findByCompanyAndProductionDate(Company company,LocalDate productionDate);
	List<Production> findByCompanyAndProductionSequence(Company company,Integer productionSequence);
	
    @Query(value = """
            SELECT A.*
            FROM production A
            LEFT JOIN items B ON B.id = A.item_id AND B.company_id = A.company_id
            LEFT JOIN processes C ON C.id = B.process_id AND C.company_id = A.company_id
            LEFT JOIN task_process_mappings D ON D.process_id = C.id AND D.sequence = A.production_sequence
            LEFT JOIN tasks E ON E.id = D.task_id AND E.company_id = A.company_id
            WHERE A.company_id = :companyId
            AND A.production_sequence > 0
            AND E.id = :taskId
            """, nativeQuery = true)
        List<Production> findByCompanyIdAndTaskId(Long companyId, Long taskId);
    
    @Query("SELECT DISTINCT p.productionDate FROM Production p WHERE p.company.id = :companyId " +
            "AND (:startDate IS NULL OR p.productionDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.productionDate <= :endDate) " +
            "ORDER BY p.productionDate")
     List<LocalDate> findProductionDates(@Param("companyId") Long companyId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
	void deleteByCompanyAndProductionDate(Company company,LocalDate productionDate);
}