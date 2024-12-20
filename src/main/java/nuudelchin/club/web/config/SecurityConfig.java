package nuudelchin.club.web.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import nuudelchin.club.web.jwt.CustomLogoutFilter;
import nuudelchin.club.web.jwt.JWTFilter;
import nuudelchin.club.web.jwt.JWTUtil;
import nuudelchin.club.web.jwt.LoginFilter;
import nuudelchin.club.web.repository.RefreshRepository;
import nuudelchin.club.web.service.SecretService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	private final SecretService secretService;

    public SecurityConfig(
    		AuthenticationConfiguration authenticationConfiguration, 
    		JWTUtil jwtUtil, 
    		RefreshRepository refreshRepository,
    		SecretService secretService) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.secretService = secretService;
    }
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
	
	@Bean
	public BCryptPasswordEncoder bCryptoPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
		.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
			
				CorsConfiguration configuration = new CorsConfiguration();
				
				configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);
				
				configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
				configuration.setExposedHeaders(Collections.singletonList("access"));
				
				return configuration;
			}
		}));
		
		http.csrf((auth) -> auth.disable());
		
		http.formLogin((auth) -> auth.disable());
		
		http.httpBasic((auth) -> auth.disable());
		
		http.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/login", "/", "join", "/reissue").permitAll()
				.anyRequest().authenticated());
		
		http.addFilterAt(new LoginFilter(
				authenticationManager(authenticationConfiguration), 
				jwtUtil, 
				refreshRepository,
				secretService), UsernamePasswordAuthenticationFilter.class);
		
		http.addFilterAfter(new JWTFilter(jwtUtil), LoginFilter.class);
		
		http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
		
		http.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
	}

}
