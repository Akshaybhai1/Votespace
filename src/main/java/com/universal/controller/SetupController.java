package com.universal.controller;

import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.universal.config.SetupState;
import com.universal.entities.AppUser;
import com.universal.entities.Role;
import com.universal.repository.AppUserRepository;

@Controller
@RequestMapping("/setup")
public class SetupController {

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final AppUserRepository users;
	private final PasswordEncoder encoder;
	private final SetupState setupState;

	public SetupController(AppUserRepository users, PasswordEncoder encoder, SetupState setupState) {
		this.users = users;
		this.encoder = encoder;
		this.setupState = setupState;
	}

	@GetMapping
	public String form(Model model) {
		if (users.existsByRole(Role.ADMIN)) {
			return "redirect:/login";
		}
		setupState.ensureCode();
		model.addAttribute("needCode", setupState.isCodeRequired());
		return "setup";
	}

	@PostMapping
	public synchronized String create(@RequestParam(value = "setupCode", required = false) String setupCode,
			@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("confirm") String confirm, Model model) {
		if (users.existsByRole(Role.ADMIN)) {
			return "redirect:/login";
		}
		String error = null;
		if (!setupState.matches(setupCode)) {
			error = "The setup code is wrong. Ask the person who installed the app.";
		} else if (fullName == null || fullName.isBlank()) {
			error = "Please enter your name.";
		} else if (email == null || !EMAIL.matcher(email.trim()).matches()) {
			error = "Please enter a valid e-mail address.";
		} else if (password == null || password.length() < 8) {
			error = "The password must have at least 8 characters.";
		} else if (!password.equals(confirm)) {
			error = "The two passwords do not match.";
		} else if (users.existsByEmail(email.trim().toLowerCase())) {
			error = "This e-mail is already used.";
		}
		if (error != null) {
			model.addAttribute("error", error);
			model.addAttribute("needCode", setupState.isCodeRequired());
			model.addAttribute("fullName", fullName);
			model.addAttribute("email", email);
			return "setup";
		}
		users.save(new AppUser(fullName.trim(), email.trim().toLowerCase(), encoder.encode(password), Role.ADMIN));
		setupState.clear();
		return "redirect:/login?created";
	}
}