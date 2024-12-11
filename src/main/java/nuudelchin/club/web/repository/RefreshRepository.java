package nuudelchin.club.web.repository;

import org.apache.ibatis.annotations.Mapper;

import nuudelchin.club.web.entity.RefreshEntity;

@Mapper
public interface RefreshRepository {
	
	RefreshEntity findByRefresh(String refresh);
	
	int save(RefreshEntity entity);
	
	int delete(String refresh);
	
	int deleteExpiredRefreshTokens(String expiration);
}