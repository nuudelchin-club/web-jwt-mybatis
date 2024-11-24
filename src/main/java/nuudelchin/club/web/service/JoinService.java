package nuudelchin.club.web.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import nuudelchin.club.web.dto.JoinDTO;
import nuudelchin.club.web.entity.UserEntity;
import nuudelchin.club.web.repository.UserRepository;

@Service
public class JoinService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public void joinProc(JoinDTO dto) {
		
		String username = dto.getUsername();
		String password = dto.getPassword();
		
		UserEntity userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
		
			userEntity = new UserEntity();

        	userEntity.setUsername(dto.getUsername());
        	userEntity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        	userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);
		}
	}
}
