package com.universal.dto;

import com.universal.entities.AppUser;

/** Outcome of registering a user or resetting a password. */
public class RegistrationResult {

	private final AppUser user;
	private final String temporaryPassword;
	private final boolean emailSent;

	public RegistrationResult(AppUser user, String temporaryPassword, boolean emailSent) {
		this.user = user;
		this.temporaryPassword = temporaryPassword;
		this.emailSent = emailSent;
	}

	public AppUser getUser() {
		return user;
	}

	public String getTemporaryPassword() {
		return temporaryPassword;
	}

	public boolean isEmailSent() {
		return emailSent;
	}
}
