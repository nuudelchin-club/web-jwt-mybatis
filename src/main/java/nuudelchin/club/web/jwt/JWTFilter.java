package nuudelchin.club.web.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.dto.CustomUserDetails;
import nuudelchin.club.web.entity.UserEntity;

public class JWTFilter extends OncePerRequestFilter {
	
	private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authorization= request.getHeader("Authorization");
		
		if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            return;
        }
		
		System.out.println("authorization now");
		
		//Bearer 부분 제거 후 순수 토큰만 획득
		String token = authorization.split(" ")[1];
		
		if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

						//조건이 해당되면 메소드 종료 (필수)
            return;
        }
		
		String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);
        
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
	}

    
}
