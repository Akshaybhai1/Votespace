package com.universal.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.universal.entities.AppUser;
import com.universal.entities.Election;

import jakarta.mail.internet.MimeMessage;

/**
 * Sends the HTML e-mails. Never throws: a mail problem must not break
 * registration.
 */
@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final ObjectProvider<JavaMailSender> senderProvider;
	private final String from;
	private final String baseUrl;

	public MailService(ObjectProvider<JavaMailSender> senderProvider, @Value("${app.mail.from}") String from,
			@Value("${app.base-url}") String baseUrl) {
		this.senderProvider = senderProvider;
		this.from = from;
		this.baseUrl = baseUrl;
	}

	/**
	 * Login details for a newly registered user (or after a password reset).
	 * Returns true if sent.
	 */
	public boolean sendCredentials(AppUser user, String rawPassword, Election election, boolean isReset) {
		String role = user.isCandidate() ? "candidate" : "voter";
		String subject = isReset ? "VoteSpace - your password was reset" : "Welcome to VoteSpace - your login details";

		StringBuilder b = new StringBuilder();
		b.append("<p>Hello <strong>").append(esc(user.getFullName())).append("</strong>,</p>");
		if (isReset) {
			b.append("<p>The election admin has reset your password. Use the new login details below.</p>");
		} else {
			b.append("<p>You have been registered as a <strong>").append(role)
					.append("</strong> on VoteSpace. Use the details below to sign in.</p>");
		}
		b.append("<table style='border-collapse:collapse;margin:12px 0'>")
				.append("<tr><td style='padding:6px 14px 6px 0;color:#64748b'>Username</td><td><strong>")
				.append(esc(user.getEmail())).append("</strong></td></tr>")
				.append("<tr><td style='padding:6px 14px 6px 0;color:#64748b'>Password</td><td><strong>")
				.append(esc(rawPassword)).append("</strong></td></tr></table>");
		b.append(scheduleHtml(election));
		b.append("<p><a href='").append(esc(baseUrl + "/login"))
				.append("' style='background:#4f46e5;color:#fff;padding:10px 18px;border-radius:8px;")
				.append("text-decoration:none;display:inline-block'>Sign in to VoteSpace</a></p>");
		b.append("<p style='color:#64748b;font-size:13px'>Keep these details private. ")
				.append("Each voter can vote only once.</p>");
		return send(user.getEmail(), subject, wrap("Your VoteSpace account", b.toString()));
	}

	/**
	 * Tells everybody about the (new) election schedule. Runs in the background.
	 */
	@Async
	public void sendElectionNotice(List<AppUser> recipients, Election election) {
		for (AppUser u : recipients) {
			StringBuilder b = new StringBuilder();
			b.append("<p>Hello <strong>").append(esc(u.getFullName())).append("</strong>,</p>");
			b.append("<p>The election schedule has been announced.</p>");
			b.append(scheduleHtml(election));
			b.append("<p><a href='").append(esc(baseUrl + "/login")).append("'>Sign in to VoteSpace</a></p>");
			send(u.getEmail(), "Election schedule: " + election.getTitle(), wrap("Election schedule", b.toString()));
		}
	}

	private String scheduleHtml(Election e) {
		if (e == null || e.getStartTime() == null || e.getEndTime() == null) {
			return "<p style='color:#64748b'>The election date and time have not been announced yet.</p>";
		}
		return "<div style='background:#eef2ff;border-radius:10px;padding:12px 16px;margin:12px 0'>" + "<strong>"
				+ esc(e.getTitle()) + "</strong><br>Voting opens: " + esc(e.getStartDisplay()) + "<br>Voting closes: "
				+ esc(e.getEndDisplay()) + "</div>";
	}

	private String wrap(String heading, String inner) {
		return "<div style='font-family:Arial,sans-serif;max-width:560px;margin:auto;border:1px solid #e2e8f0;"
				+ "border-radius:14px;overflow:hidden'>"
				+ "<div style='background:linear-gradient(135deg,#4f46e5,#7c3aed);color:#fff;padding:18px 24px'>"
				+ "<h2 style='margin:0'>" + esc(heading) + "</h2></div><div style='padding:20px 24px;color:#0f172a'>"
				+ inner + "</div></div>";
	}

	public boolean sendProfileUpdated(AppUser user) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");
		String updatedAt = LocalDateTime.now().format(formatter);

		String phone = (user.getPhone() == null || user.getPhone().isBlank())
				? "Not provided"
				: user.getPhone();

		String body = "<p>Hello <strong>" + esc(user.getFullName()) + "</strong>,</p>"
				+ "<p>An election admin updated your VoteSpace profile.</p>"
				+ "<p><strong>Updated on:</strong> " + esc(updatedAt) + "</p>"
				+ "<table style='border-collapse:collapse;margin:12px 0'>"
				+ "<tr><td style='padding:6px 14px 6px 0;color:#64748b'>Full name</td><td><strong>"
				+ esc(user.getFullName()) + "</strong></td></tr>"
				+ "<tr><td style='padding:6px 14px 6px 0;color:#64748b'>E-mail</td><td><strong>"
				+ esc(user.getEmail()) + "</strong></td></tr>"
				+ "<tr><td style='padding:6px 14px 6px 0;color:#64748b'>Phone</td><td><strong>"
				+ esc(phone) + "</strong></td></tr>"
				+ "</table>"
				+ "<p>If you did not expect this change, contact the election admin.</p>";

		return send(user.getEmail(), "VoteSpace - profile updated",
				wrap("Your profile was updated", body));
	}

	public boolean sendAccountDeleted(AppUser user) {
		String body = "<p>Hello <strong>" + esc(user.getFullName()) + "</strong>,</p>"
				+ "<p>Your VoteSpace account has been removed by an election admin.</p>"
				+ "<p>You can no longer sign in. Contact the election admin if you think this is a mistake.</p>";

		return send(user.getEmail(), "VoteSpace - account removed", wrap("Your account was removed", body));
	}

	private boolean send(String to, String subject, String html) {
		JavaMailSender sender = senderProvider.getIfAvailable();
		if (sender == null) {
			log.warn("No mail sender configured - e-mail to {} skipped", to);
			return false;
		}
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(html, true);
			sender.send(message);
			return true;
		} catch (Exception ex) {
			log.warn("E-mail to {} failed: {}", to, ex.getMessage());
			return false;
		}
	}

	private static String esc(String s) {
		return HtmlUtils.htmlEscape(s == null ? "" : s);
	}
}
