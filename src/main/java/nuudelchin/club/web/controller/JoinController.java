package nuudelchin.club.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nuudelchin.club.web.dto.JoinDTO;
import nuudelchin.club.web.service.JoinService;

@Controller
@ResponseBody
public class JoinController {

	private final JoinService joinService;
	
	public JoinController(JoinService joinService) {
		
		this.joinService = joinService;
	}
	
	@PostMapping("/join")
	public String joinProc(JoinDTO dto) {
		
		this.joinService.joinProc(dto);
		
		return "Okay";
	}
}