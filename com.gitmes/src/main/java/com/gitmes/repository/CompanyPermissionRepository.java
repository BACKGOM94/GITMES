package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gitmes.model.CompanyPermission;

@Repository
public interface CompanyPermissionRepository extends JpaRepository<CompanyPermission, Long> {
    
	@Transactional
    void deleteByCompanyId(Long companyId);
	
    // 특정 회사의 권한 목록 조회
    List<CompanyPermission> findByCompanyId(Long companyId);
    
    // 특정 회사가 특정 메뉴에 대한 권한을 가지고 있는지 확인
    boolean existsByCompanyIdAndMenuId(Long companyId, Long menuId);
}