package nuudelchin.club.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class SecretService {
	
	private Long jwtAccess;
	private Long jwtRefresh;
	private int jwtRefreshCookie;

	public SecretService(
			@Value("${jwt.access}")Long jwtAccess,
			@Value("${jwt.refresh}")Long jwtRefresh,
			@Value("${jwt.refresh.cookie}")int jwtRefreshCookie) {
		
		this.jwtAccess = jwtAccess;
		this.jwtRefresh = jwtRefresh;
		this.jwtRefreshCookie = jwtRefreshCookie;
	}
}
