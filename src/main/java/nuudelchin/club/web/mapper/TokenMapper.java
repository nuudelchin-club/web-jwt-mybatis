package nuudelchin.club.web.mapper;

import org.apache.ibatis.annotations.Mapper;

import nuudelchin.club.web.entity.TokenEntity;

import java.time.Instant;

@Mapper
public interface TokenMapper {
	
	TokenEntity selectByToken(String token);
	
	int insert(TokenEntity entity);
	
	int deleteExpiredTokens(Instant expiration);

	int deleteByToken(String token);

	int deleteByUsername(String username);
}