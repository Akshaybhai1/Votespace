package com.universal.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** After login, send each role to its own dashboard. */
@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		response.sendRedirect(request.getContextPath() + dashboardFor(authentication));
	}

	public static String dashboardFor(Authentication auth) {
		if (auth != null) {
			for (var a : auth.getAuthorities()) {
				switch (a.getAuthority()) {
				case "ROLE_ADMIN":
					return "/admin/dashboard";
				case "ROLE_CANDIDATE":
					return "/candidate/dashboard";
				case "ROLE_VOTER":
					return "/voter/dashboard";
				default:
					break;
				}
			}
		}
		return "/login";
	}
}
