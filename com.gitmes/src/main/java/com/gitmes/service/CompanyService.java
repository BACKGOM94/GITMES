package com.gitmes.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.User;
import com.gitmes.model.enumstyle.UserRole;
import com.gitmes.repository.CompanyRepository;
import com.gitmes.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public void register_company(Company company,String managerUsername,String managerPassword) {
    	companyRepository.save(company);
    	
    	User user = new User();
    	user.setUsername(managerUsername);
    	user.setPassword(passwordEncoder.encode(managerPassword));
    	user.setName(company.getManagerName());
    	user.setPhone(company.getManagerPhone());
    	user.setAddress(company.getAddress());
    	user.setRole(UserRole.ROLE_MANAGER);
    	user.setCompany(company);
    	user.setEnabled(true);
    	
    	userRepository.save(user);
    }
    
    public List<Company> get_companyList(){
    	return companyRepository.findAll();
    }
    
    public void update_company(Company company) {
    	companyRepository.save(company);
    }       
    
    public Company getgetProductById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회사가 존재하지 않습니다."));
    }
}
