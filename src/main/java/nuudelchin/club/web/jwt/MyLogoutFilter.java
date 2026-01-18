package nuudelchin.club.web.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.entity.TokenEntity;
import nuudelchin.club.web.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class MyLogoutFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(MyLogoutFilter.class);

	private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public MyLogoutFilter(JwtUtil jwtUtil, TokenService tokenService) {

        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }
    
	@Override
	public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
			throws IOException, ServletException {
		
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain filterChain)
            throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            log.info("filter for logout");
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            log.info("logout method is wrong");
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            log.info("cookie is null");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String clientRefreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                clientRefreshToken = cookie.getValue();
            }
        }

        if (clientRefreshToken == null) {
            log.info("refresh token is null");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // DB에 저장되어 있는지 확인
        TokenEntity tokenEntity = tokenService.selectByToken(clientRefreshToken);
        if (tokenEntity == null) {
            log.info("invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String serverRefreshToken = tokenEntity.getToken();

        // if token is EXPIRED, return.
        try {
            
        	jwtUtil.isExpired(serverRefreshToken);
            
        } catch (ExpiredJwtException e) {
            log.info("expired refresh token");
            tokenService.deleteByToken(serverRefreshToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 로그아웃 진행
        tokenService.deleteByToken(serverRefreshToken);

        // set client refresh-token null
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
