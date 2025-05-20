package com.gitmes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitmes.model.Company;
import com.gitmes.model.CompanyPermission;
import com.gitmes.model.Menu;
import com.gitmes.model.MenuPermissionResponse;
import com.gitmes.repository.CompanyPermissionRepository;
import com.gitmes.repository.CompanyRepository;
import com.gitmes.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyPermissionService {

    private final CompanyPermissionRepository companyPermissionRepository;
    private final CompanyRepository companyRepository;
    private final MenuRepository menuRepository;
    
    public List<MenuPermissionResponse> getMenusWithPermissions(Long companyId) {
        List<Menu> allMenus = menuRepository.findAllActiveMenusWithChildren(); // 전체 메뉴 조회
        List<Long> grantedMenuIds = companyPermissionRepository.findByCompanyId(companyId)
                .stream()
                .map(permission -> permission.getMenu().getId())
                .collect(Collectors.toList()); // 해당 회사가 접근 권한을 가진 메뉴 ID 리스트

        return allMenus.stream()
                .map(menu -> new MenuPermissionResponse(
                        menu.getId(),
                        menu.getName(),
                        menu.getUrl(),
                        grantedMenuIds.contains(menu.getId()) // 해당 메뉴가 권한을 가지고 있는지 체크
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateCompanyPermissions(Long companyId, List<Long> menuIds) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사 ID: " + companyId));

        // 기존 권한 삭제
        companyPermissionRepository.deleteByCompanyId(companyId);

        // 새로운 권한 추가
        List<Menu> menus = menuRepository.findAllById(menuIds);
        List<CompanyPermission> newPermissions = menus.stream()
                .map(menu -> new CompanyPermission(company, menu))
                .collect(Collectors.toList());

        companyPermissionRepository.saveAll(newPermissions);
    }
}