package nuudelchin.club.web.service;

import org.springframework.stereotype.Service;

import nuudelchin.club.web.entity.UserEntity;
import nuudelchin.club.web.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

	public UserEntity findByUsername(String username) {
	
		return userRepository.findByUsername(username);
	}
}
