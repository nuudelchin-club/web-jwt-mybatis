package nuudelchin.club.web.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.dto.CustomUserDetails;
import nuudelchin.club.web.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class MyJwtFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(MyJwtFilter.class);

	private final JwtUtil jwtUtil;

    public MyJwtFilter(JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {

		// header 에 authorization 없다면 다음 필터로 간다.
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			log.debug("No Authorization header or not Bearer token");
			filterChain.doFilter(request, response);
			return;
		}

		// if token is EXPIRED, return.
		String token = authorization.substring(7);
		try {

			jwtUtil.isExpired(token);

		} catch (ExpiredJwtException e) {

			log.warn("Expired access token");
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("access token is expired.");

		    return;
		}

		log.info("Valid access token");

		String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);

        CustomUserDetails userDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
	}
}
