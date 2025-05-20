package com.gitmes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gitmes.model.Defect;
import com.gitmes.model.Production;

public interface DefectRepository  extends JpaRepository<Defect, Long>, JpaSpecificationExecutor<Defect>{
	Defect findByProduction(Production production);
}
