package nuudelchin.club.web.repository;

import org.apache.ibatis.annotations.Mapper;

import nuudelchin.club.web.entity.UserEntity;

@Mapper
public interface UserRepository {
	
	UserEntity findByUsername(String username);
	
	int save(UserEntity userEntity);
}
