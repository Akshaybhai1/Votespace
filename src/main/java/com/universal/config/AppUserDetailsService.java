package com.universal.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.universal.entities.AppUser;
import com.universal.repository.AppUserRepository;

/** Tells Spring Security how to load a user (by e-mail) from MySQL. */
@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository users;

	public AppUserDetailsService(AppUserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser u = users.findByEmail(username.trim().toLowerCase())
				.orElseThrow(() -> new UsernameNotFoundException("No account for " + username));
		return User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build();
	}
}
