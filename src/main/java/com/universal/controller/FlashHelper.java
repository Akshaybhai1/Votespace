package com.universal.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.dto.RegistrationResult;

final class FlashHelper {

	private FlashHelper() {
	}

	/** Success message if the e-mail went out, otherwise show the password to the admin. */
	static void registration(RedirectAttributes ra, RegistrationResult r, String what, boolean reset) {
		String action = reset ? "Password reset for " : what + " registered: ";
		if (r.isEmailSent()) {
			ra.addFlashAttribute("success",
					action + r.getUser().getFullName() + ". Login details were e-mailed to " + r.getUser().getEmail() + ".");
		} else {
			ra.addFlashAttribute("warning",
					action + r.getUser().getFullName() + ", but the e-mail could not be sent (check the mail settings). "
							+ "Share these details manually - Username: " + r.getUser().getEmail() + " | Password: "
							+ r.getTemporaryPassword());
		}
	}
}
