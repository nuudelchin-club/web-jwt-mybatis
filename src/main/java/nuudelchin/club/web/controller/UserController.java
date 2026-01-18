package nuudelchin.club.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nuudelchin.club.web.entity.UserEntity;
import nuudelchin.club.web.service.UserService;

@Controller
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		
		this.userService = userService;
	}

	@GetMapping("/user")
	@ResponseBody
	public Object user() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String username = authentication.getName();
		
		UserEntity userEntity = userService.selectByUsername(username);
		
		Map<String, Object> result = new HashMap<String, Object>(); 
		
		if(userEntity != null) {
			
			String role = userEntity.getRole();
			
			result.put("username", username);
			result.put("role", role);
		}
		System.out.println(result);		
		return result;
	}
}
