package com.gitmes.ControllerAdvice;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gitmes.model.Menu;
import com.gitmes.model.User;
import com.gitmes.service.UserPermissionService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

	private final UserService userService;
	private final UserPermissionService userPermissionService;
	
	@ModelAttribute("menus")
	public List<Menu> getSideMenus(Principal principal){
		if (principal == null) return null;
			User user = userService.getuserinfo(principal.getName());
		
		return userPermissionService.getUserMenu(user);
	}
}
