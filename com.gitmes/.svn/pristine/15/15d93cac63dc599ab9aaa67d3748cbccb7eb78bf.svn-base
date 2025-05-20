package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gitmes.model.Process;
import com.gitmes.model.Company;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
	List<Process> findByCompany(Company company);
}