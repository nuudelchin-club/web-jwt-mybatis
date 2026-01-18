package nuudelchin.club.web.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.dto.CustomUserDetails;
import nuudelchin.club.web.entity.TokenEntity;
import nuudelchin.club.web.service.SecretService;
import nuudelchin.club.web.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger log = LoggerFactory.getLogger(MyLoginFilter.class);

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	private final TokenService tokenService;
	private final SecretService secretService;
	
	public MyLoginFilter(
			AuthenticationManager authenticationManager, 
			JwtUtil jwtUtil,
			TokenService tokenService,
			SecretService secretService) {
		
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.tokenService = tokenService;
		this.secretService = secretService;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
												HttpServletResponse response)
			throws AuthenticationException {
		
		if (request.getContentType() != null && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
	        try {

				log.info("authenticate");
	            
	            ObjectMapper objectMapper = new ObjectMapper();
	            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

	            String username = credentials.get("username");
	            String password = credentials.get("password");

	            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

	            return authenticationManager.authenticate(authToken);
	            
	        } catch (IOException e) {
	            throw new AuthenticationServiceException("Error reading JSON request", e);
	        }
	    }
		
		throw new AuthenticationServiceException("Invalid content type: Expected application/json");
	}
	
	@Override
    protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain,
											Authentication authentication) {

		log.info("authentication is successful");

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

		tokenService.deleteByUsername(username);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String newAccessToken = jwtUtil.createJwt("access", username, role, secretService.getJwtAccess());
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, secretService.getJwtRefresh());
        
        TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUsername(username);
		tokenEntity.setToken(newRefreshToken);
		tokenEntity.setExpiration(Instant.now().plusMillis(secretService.getJwtRefresh()));
		tokenService.insert(tokenEntity);

		response.setHeader("Authorization", "Bearer " + newAccessToken);

		// create client refresh-token
		Cookie cookie = new Cookie("refresh", newRefreshToken);
		cookie.setMaxAge(secretService.getJwtRefreshCookie());	// 24 hours
		//cookie.setSecure(true);	// use case is https
		cookie.setPath("/");		// Бүх эндпойнт дээр илгээгдэх
		cookie.setHttpOnly(true);	// cannot use cookie in java script
        response.addCookie(cookie);

		log.info("tokens are created successfully");
        
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response,
											  AuthenticationException failed) {
		log.info("authentication is failed");
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
