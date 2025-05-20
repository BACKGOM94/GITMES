package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gitmes.model.UserPermissions;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissions, Long> {
    
    @Transactional
    void deleteByUserId(Long userId);
    
    List<UserPermissions> findByUserId(Long userId);

    boolean existsByUserIdAndMenuId(Long userId, Long menuId);
}