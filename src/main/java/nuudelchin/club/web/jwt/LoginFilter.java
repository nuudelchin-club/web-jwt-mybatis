package nuudelchin.club.web.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.dto.CustomUserDetails;
import nuudelchin.club.web.entity.RefreshEntity;
import nuudelchin.club.web.repository.RefreshRepository;
import nuudelchin.club.web.service.SecretService;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	private final SecretService secretService;
	
	public LoginFilter(
			AuthenticationManager authenticationManager, 
			JWTUtil jwtUtil, 
			RefreshRepository refreshRepository,
			SecretService secretService) {
		
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
		this.secretService = secretService;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		System.out.println("attemptAuthentication");
		
		if (request.getContentType() != null && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
	        try {
	            
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", username, role, secretService.getJwtAccess());
        String refresh = jwtUtil.createJwt("refresh", username, role, secretService.getJwtRefresh());
        
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(new Date(System.currentTimeMillis() + secretService.getJwtRefresh()).toString());
        refreshRepository.save(refreshEntity);
        
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        
        System.out.println("access and refresh tokens are created");
        
        response.setStatus(HttpStatus.OK.value());
        //response.sendRedirect("http://localhost:3000/");
    }

		//로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

    	System.out.println("fail");
    	response.setStatus(401);
    }
    
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(secretService.getJwtRefreshCookie());	// 24 hours
        //cookie.setSecure(true);	// use case is https
        cookie.setPath("/");		// Бүх эндпойнт дээр илгээгдэх
        cookie.setHttpOnly(true);	// cannot use cookie in java script

        return cookie;
    }

}
