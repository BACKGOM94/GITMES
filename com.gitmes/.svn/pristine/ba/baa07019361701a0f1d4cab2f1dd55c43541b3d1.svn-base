package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.Bom;
import com.gitmes.model.Company;

public interface BomRepository extends JpaRepository<Bom, Long> {
	List<Bom> findByCompany(Company company);
	List<Bom> findByParentItem_Id(Long parentItemId);
	void deleteByParentItemId(Long parentItemId);
}