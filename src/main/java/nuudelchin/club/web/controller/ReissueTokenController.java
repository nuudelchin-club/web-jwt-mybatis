package nuudelchin.club.web.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.entity.TokenEntity;
import nuudelchin.club.web.jwt.JwtUtil;
import nuudelchin.club.web.service.TokenService;
import nuudelchin.club.web.service.SecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class ReissueTokenController {

    private static final Logger log = LoggerFactory.getLogger(ReissueTokenController.class);

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final SecretService secretService;

    public ReissueTokenController(
    		JwtUtil jwtUtil,
    		TokenService tokenService,
    		SecretService secretService) {

        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.secretService = secretService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            log.info("cookie is null");
        	return new ResponseEntity<>("cookie is null", HttpStatus.FORBIDDEN);
        }

        String clientRefreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                clientRefreshToken = cookie.getValue();
            }
        }

        if (clientRefreshToken == null) {
            log.info("refresh token is null");
            return new ResponseEntity<>("refresh token is null", HttpStatus.FORBIDDEN);
        }

        // DB에 저장되어 있는지 확인
        TokenEntity tokenEntity = tokenService.selectByToken(clientRefreshToken);
        if (tokenEntity == null) {
            log.info("invalid refresh token");
            return new ResponseEntity<>("invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String serverRefreshToken = tokenEntity.getToken();

        // if token is EXPIRED, return.
        try {
            
        	jwtUtil.isExpired(serverRefreshToken);
            
        } catch (ExpiredJwtException e) {
            log.info("expired refresh token");
            tokenService.deleteByToken(serverRefreshToken);
            return new ResponseEntity<>("refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.getUsername(clientRefreshToken);
        String role = jwtUtil.getRole(clientRefreshToken);
        String access = jwtUtil.createJwt("access", username, role, secretService.getJwtAccess());
// refresh token must be recreated here
        response.setHeader("Authorization", "Bearer " + access);
        
        log.info("access token is reissued");

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(secretService.getJwtRefreshCookie());
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
