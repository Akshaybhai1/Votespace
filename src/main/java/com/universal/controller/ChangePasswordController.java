package com.universal.controller;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.entities.AppUser;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;

/** Every logged-in user (admin, candidate, voter) can change their own password. */
@Controller
@RequestMapping("/account/password")
public class ChangePasswordController {

	private final AppUserRepository users;
	private final PasswordEncoder encoder;

	public ChangePasswordController(AppUserRepository users, PasswordEncoder encoder) {
		this.users = users;
		this.encoder = encoder;
	}

	@GetMapping
	public String form() {
		return "account/password";
	}

	@PostMapping
	public String change(@RequestParam("current") String current, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirm") String confirm, Principal principal, RedirectAttributes ra) {
		try {
			AppUser u = users.findByEmail(principal.getName())
					.orElseThrow(() -> new AppException("Account not found."));
			if (!encoder.matches(current, u.getPassword())) {
				throw new AppException("Your current password is wrong.");
			}
			if (newPassword == null || newPassword.length() < 8) {
				throw new AppException("The new password must have at least 8 characters.");
			}
			if (!newPassword.equals(confirm)) {
				throw new AppException("The two new passwords do not match.");
			}
			if (newPassword.equals(current)) {
				throw new AppException("The new password must be different from the current one.");
			}
			u.setPassword(encoder.encode(newPassword));
			users.save(u);
			ra.addFlashAttribute("success", "Your password was changed.");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/account/password";
	}
}
