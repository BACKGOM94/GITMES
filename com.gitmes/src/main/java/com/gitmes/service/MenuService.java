package com.gitmes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gitmes.model.Menu;
import com.gitmes.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    
    public List<Menu> get_menuList(){
    	return menuRepository.findAllActiveMenusWithChildren();
    }
}
