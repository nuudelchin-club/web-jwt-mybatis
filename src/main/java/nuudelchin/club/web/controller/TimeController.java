package nuudelchin.club.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeController {

	@GetMapping("/time")
	public Object timeAPI() {
		
		System.out.println("timeAPI");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String time = new Date(System.currentTimeMillis()).toString();
		
		result.put("time", time);
		
		return result;
	}
}