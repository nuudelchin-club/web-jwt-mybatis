package nuudelchin.club.web.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nuudelchin.club.web.entity.RefreshEntity;
import nuudelchin.club.web.jwt.JWTUtil;
import nuudelchin.club.web.repository.RefreshRepository;
import nuudelchin.club.web.service.RefreshService;

@Controller
@ResponseBody
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;

    public ReissueController(JWTUtil jwtUtil, RefreshService refreshService) {

        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        
        if(cookies == null ) {
        	
        	//response status code
        	return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

            	refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            
        	jwtUtil.isExpired(refreshToken);
            
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        
        //DB에 저장되어 있는지 확인
    	RefreshEntity refreshEntity = refreshService.findByRefresh(refreshToken);
    	if (refreshEntity == null) {
    		
    		//response body
    		return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    	}

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //make new JWT
        String newAccessToken = jwtUtil.createJwt("access", username, role, 60000L /*600000L*/);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 600000L /*86400000L*/);
        
        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshService.delete(refreshToken);
    	
    	refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(newRefreshToken);
        refreshEntity.setExpiration(new Date(System.currentTimeMillis() + 600000L /*86400000L*/).toString());
        
        refreshService.save(refreshEntity);

        //response
        response.setHeader("access", newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
