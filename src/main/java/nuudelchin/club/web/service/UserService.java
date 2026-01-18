package nuudelchin.club.web.service;

import nuudelchin.club.web.entity.UserEntity;
import nuudelchin.club.web.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private final UserMapper userRepository;

    public UserService(UserMapper userRepository) {

        this.userRepository = userRepository;
    }

	public UserEntity selectByUsername(String username) {
	
		return userRepository.selectByUsername(username);
	}

	public int insert(UserEntity entity) {
		return userRepository.insert(entity);
	}

	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username) > 0;
	}

}
