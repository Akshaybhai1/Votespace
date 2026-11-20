package com.universal.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.universal.entities.AppUser;
import com.universal.repository.AppUserRepository;

/** Puts the logged-in user and the server clock into every JSP page. */
@ControllerAdvice(basePackages = "com.universal.controller")
public class GlobalModelAdvice {

	private final AppUserRepository users;

	public GlobalModelAdvice(AppUserRepository users) {
		this.users = users;
	}

	@ModelAttribute("currentUser")
	public AppUser currentUser(Principal principal) {
		if (principal == null) {
			return null;
		}
		return users.findByEmail(principal.getName()).orElse(null);
	}

	@ModelAttribute("serverNow")
	public long serverNow() {
		return System.currentTimeMillis();
	}
}
