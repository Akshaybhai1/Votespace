package com.universal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	 PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, RoleBasedSuccessHandler successHandler)
			throws Exception {
		http
		.authorizeHttpRequests(auth -> auth
			    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()   // <-- NEW LINE HERE
			    .requestMatchers("/", "/login","/setup", "/error", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
			    .requestMatchers("/admin/**").hasRole("ADMIN")
			    .requestMatchers("/candidate/**").hasRole("CANDIDATE")
			    .requestMatchers("/voter/**").hasRole("VOTER")
			    .anyRequest().authenticated())
			
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.successHandler(successHandler)
				.failureUrl("/login?error")
				.permitAll())
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll());
		return http.build();
	}
}
