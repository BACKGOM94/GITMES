package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gitmes.model.Client;
import com.gitmes.model.Company;
import com.gitmes.model.enumstyle.ClientType;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	List<Client> findByCompany(Company company);
	List<Client> findByCompanyAndTypeNot(Company company, ClientType type);
}