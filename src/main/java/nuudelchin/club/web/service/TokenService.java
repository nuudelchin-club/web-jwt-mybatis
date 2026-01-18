package nuudelchin.club.web.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import nuudelchin.club.web.entity.TokenEntity;
import nuudelchin.club.web.mapper.TokenMapper;

@Service
public class TokenService {

	private final TokenMapper tokenRepository;
	
	public TokenService(TokenMapper tokenRepository) {

        this.tokenRepository = tokenRepository;
    }
	
	public TokenEntity selectByToken(String token) {
		return tokenRepository.selectByToken(token);
	}
	
	public int insert(TokenEntity entity) {
		return tokenRepository.insert(entity);
	}
	
	@Scheduled(fixedRate = 60000)
	public void deleteExpiredTokens() {
//    	Instant expiration = Instant.now();
//    	System.out.println("DB refresh check time: " + expiration);
//    	tokenRepository.deleteExpiredTokens(expiration);
    }

	public int deleteByToken(String token) {
		return tokenRepository.deleteByToken(token);
	}

	// one username must have only one refresh token
	public int deleteByUsername(String username) {
		return tokenRepository.deleteByUsername(username);
	}
}
