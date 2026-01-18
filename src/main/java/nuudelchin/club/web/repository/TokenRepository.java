package nuudelchin.club.web.repository;

import org.apache.ibatis.annotations.Mapper;

import nuudelchin.club.web.entity.TokenEntity;

import java.time.Instant;

@Mapper
public interface TokenRepository {
	
	TokenEntity selectByToken(String token);
	
	int save(TokenEntity entity);
	
	int deleteExpiredTokens(Instant expiration);

	int deleteByToken(String token);

	int deleteByUsername(String username);
}