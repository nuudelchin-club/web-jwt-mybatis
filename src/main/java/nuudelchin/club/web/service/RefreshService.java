package nuudelchin.club.web.service;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import nuudelchin.club.web.entity.RefreshEntity;
import nuudelchin.club.web.repository.RefreshRepository;

@Service
public class RefreshService {

	private final RefreshRepository refreshRepository;
	
	public RefreshService(RefreshRepository refreshRepository) {

        this.refreshRepository = refreshRepository;
    }
	
	public RefreshEntity findByRefresh(String refresh) {
		return refreshRepository.findByRefresh(refresh);
	}
	
	public int save(RefreshEntity entity) {
		return refreshRepository.save(entity);
	}
	
	public int delete(String refresh) {
		return refreshRepository.delete(refresh);
	}
	
	@Scheduled(fixedRate = 60000)
	public void deleteExpiredRefreshTokens() {
    	
    	String expiration = new Date(System.currentTimeMillis()).toString();
    	
    	System.out.println("DB refresh check time: " + expiration);
    	
    	refreshRepository.deleteExpiredRefreshTokens(expiration);
    	
    }
}
