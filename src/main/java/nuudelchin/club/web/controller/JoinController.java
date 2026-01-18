package nuudelchin.club.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

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
	public ResponseEntity<?> join(JoinDTO dto) {
		try {
			joinService.join(dto);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}