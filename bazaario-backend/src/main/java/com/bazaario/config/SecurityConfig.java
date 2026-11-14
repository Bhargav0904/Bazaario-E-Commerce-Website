package com.bazaario.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bazaario.security.CustomUserDetailsService;
import com.bazaario.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;
	private final CustomUserDetailsService userDetailsService;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// Replaces the JDBC version's hand-written SHA-256 in PasswordUtil - BCrypt is
		// the standard choice here: it's slow-by-design (resists brute force) and salts
		// automatically per password.
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		// Spring Security 7: the UserDetailsService is passed in the constructor
		// (the no-arg constructor + setUserDetailsService() were removed).
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) // not needed - we're not using cookies/sessions
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/images/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		// Angular's default dev server port. Add your deployed frontend's real URL here too
		// once you host it somewhere other than localhost.
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
