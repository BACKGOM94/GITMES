package com.gitmes.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitmes.model.User;
import com.gitmes.model.enumstyle.UserRole;
import com.gitmes.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public List<User> getUserList(Long companyId){
		return userRepository.findByCompanyId(companyId);
	}
	
	public User getuserinfo(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
	}
	
	public User getManager(Long companyId) {
		return userRepository.findByCompanyIdAndRole(companyId, UserRole.ROLE_MANAGER);
	}
	
	public User getAdmin(Long companyId) {
		return userRepository.findByCompanyIdAndRole(companyId, UserRole.ROLE_ADMIN);
	}
	
    public void save_user(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	userRepository.save(user);
    }
    
    public void delete_user(User user) {
    	System.out.println(user.getId());
    	userRepository.delete(user);
    }
    
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }        
    
}
