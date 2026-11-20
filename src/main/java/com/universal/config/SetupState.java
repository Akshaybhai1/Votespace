package com.universal.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SetupState {

	private static final Logger log = LoggerFactory.getLogger(SetupState.class);
	private static final SecureRandom RANDOM = new SecureRandom();

	private final String configuredCode;
	private final boolean requireCode;
	private volatile String code;

	public SetupState(@Value("${app.setup.code:}") String configuredCode,
			@Value("${app.setup.require-code:true}") boolean requireCode) {
		this.configuredCode = configuredCode == null ? "" : configuredCode.trim();
		this.requireCode = requireCode;
	}

	public boolean isCodeRequired() {
		return requireCode;
	}

	public synchronized void ensureCode() {
		if (!requireCode) {
			log.warn("SETUP: no setup code required. The first person who opens /setup becomes admin.");
			return;
		}
		if (code != null) {
			return;
		}
		if (!configuredCode.isEmpty()) {
			code = configuredCode;
			log.warn("SETUP: no admin yet. Open /setup and enter the code from 'app.setup.code'.");
			return;
		}
		byte[] b = new byte[6];
		RANDOM.nextBytes(b);
		code = HexFormat.of().formatHex(b).toUpperCase();
		log.warn("SETUP CODE: {}", code);
	}

	public boolean matches(String input) {
		if (!requireCode) {
			return true;
		}
		String c = code;
		if (c == null || input == null) {
			return false;
		}
		return MessageDigest.isEqual(c.getBytes(StandardCharsets.UTF_8),
				input.trim().getBytes(StandardCharsets.UTF_8));
	}

	public void clear() {
		code = null;
	}
}