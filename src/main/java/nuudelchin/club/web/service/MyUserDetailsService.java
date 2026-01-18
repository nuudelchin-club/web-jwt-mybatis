package nuudelchin.club.web.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import nuudelchin.club.web.dto.CustomUserDetails;
import nuudelchin.club.web.entity.UserEntity;
import nuudelchin.club.web.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	public MyUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.selectByUsername(username);
		
		if (userEntity != null) {
			
			return new CustomUserDetails(userEntity);
		}
		
		return null;
	}

}
