package com.gitmes.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gitmes.model.Company;
import com.gitmes.model.User;
import com.gitmes.service.CompanyPermissionService;
import com.gitmes.service.CompanyService;
import com.gitmes.service.UserPermissionService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/m99")
public class M99Controller {

	private final CompanyService companyService;
	private final CompanyPermissionService companyPermissionService;
	private final UserPermissionService userPermissionService;
	private final UserService userService;
	
	//회사 등록 폼
    @GetMapping("/s01")
    public String move_company(Model model) {
    	model.addAttribute("companies", companyService.get_companyList());
        return "admin/company";
    }
    
    @PostMapping("/s01/register")
    public String register_company(@ModelAttribute Company company,
    		@RequestParam("detailAddress") String detailAddress,
    		@RequestParam("managerUsername") String managerUsername,
            @RequestParam("managerPassword") String managerPassword) {
    	
    	company.setAddress(company.getAddress() + detailAddress);
    	
    	companyService.register_company(company, managerUsername, managerPassword);
    	
        return "redirect:/m99/s01";
    }
    
    @GetMapping("/s01/edit/{companyId}")
    public String move_editcompany(@PathVariable Long companyId, Model model) {    	
        model.addAttribute("company", companyService.getgetProductById(companyId));
        model.addAttribute("menuList", companyPermissionService.getMenusWithPermissions(companyId));            
        
        return "admin/companyEdit";
    }
    
    @PostMapping("/s01/update")
    public String update_company(@ModelAttribute Company company) {
    	companyService.update_company(company);
    	return "redirect:/m99/s01";
    }
    
    @PostMapping("/s01/updatePermissions")
    public String updateCompanyPermissions(@RequestParam Long companyId,@RequestParam List<Long> menuIds) {

        companyPermissionService.updateCompanyPermissions(companyId, menuIds);
        User user = userService.getManager(companyId);
        if (user == null) user = userService.getAdmin(companyId);
        userPermissionService.updateUserPermissions(user.getId(), companyId, menuIds);
        
        return "redirect:/m99/s01";
    }
    
}
