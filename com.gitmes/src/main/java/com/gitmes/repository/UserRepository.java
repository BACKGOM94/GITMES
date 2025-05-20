package com.gitmes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gitmes.model.User;
import com.gitmes.model.enumstyle.UserRole;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByCompanyId(Long companyId);
    User findByCompanyIdAndRole(Long companyId,UserRole userRole);
}
