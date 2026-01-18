package nuudelchin.club.web.repository;

import org.apache.ibatis.annotations.Mapper;

import nuudelchin.club.web.entity.UserEntity;

@Mapper
public interface UserRepository {
	
	UserEntity selectByUsername(String username);
	
	int insert(UserEntity entity);

	int existsByUsername(String username);
}
