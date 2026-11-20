package com.universal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.universal.entities.AppUser;
import com.universal.entities.Role;
import com.universal.repository.AppUserRepository;
import com.universal.service.ElectionService;

@Component
public class DataInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final AppUserRepository users;
	private final PasswordEncoder encoder;
	private final ElectionService elections;
	private final SetupState setupState;
	private final String adminName;
	private final String adminEmail;
	private final String adminPassword;

	public DataInitializer(AppUserRepository users, PasswordEncoder encoder, ElectionService elections,
			SetupState setupState, @Value("${app.admin.name:Administrator}") String adminName,
			@Value("${app.admin.email:}") String adminEmail, @Value("${app.admin.password:}") String adminPassword) {
		this.users = users;
		this.encoder = encoder;
		this.elections = elections;
		this.setupState = setupState;
		this.adminName = adminName;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
	}

	@Override
	public void run(String... args) {
		if (!users.existsByRole(Role.ADMIN)) {
			if (!adminEmail.isBlank() && !adminPassword.isBlank()) {
				users.save(new AppUser(adminName.isBlank() ? "Administrator" : adminName,
						adminEmail.trim().toLowerCase(), encoder.encode(adminPassword), Role.ADMIN));
				log.info("Admin created from configuration: {}", adminEmail);
			} else {
				setupState.ensureCode();
			}
		}
		elections.get();
	}
}