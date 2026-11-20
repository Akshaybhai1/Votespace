package com.universal.service;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.universal.dto.RegistrationResult;
import com.universal.entities.AppUser;
import com.universal.entities.Candidate;
import com.universal.entities.Role;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.repository.CandidateRepository;
import com.universal.repository.VoteRepository;

/** Admin CRUD for voters and candidates (+ the candidate editing their own profile). */
@Service
public class UserManagementService {

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern PHONE = Pattern.compile("^[0-9+\\-() ]{6,20}$");
	private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	private final AppUserRepository users;
	private final CandidateRepository candidates;
	private final VoteRepository votes;
	private final PasswordEncoder encoder;
	private final FileStorageService files;
	private final MailService mailService;
	private final ElectionService elections;

	public UserManagementService(AppUserRepository users, CandidateRepository candidates, VoteRepository votes,
			PasswordEncoder encoder, FileStorageService files, MailService mailService, ElectionService elections) {
		this.users = users;
		this.candidates = candidates;
		this.votes = votes;
		this.encoder = encoder;
		this.files = files;
		this.mailService = mailService;
		this.elections = elections;
	}

	// ------------------------------------------------------------------ voters

	public RegistrationResult registerVoter(String fullName, String email, String phone, MultipartFile photo) {
		String mail = validateBasics(fullName, email, phone);
		if (users.existsByEmail(mail)) {
			throw new AppException("A user with this e-mail already exists.");
		}
		String photoName = files.store(photo);
		String raw = generatePassword();
		AppUser u = new AppUser(fullName.trim(), mail, encoder.encode(raw), Role.VOTER);
		u.setPhone(blankToNull(phone));
		u.setPhoto(photoName);
		try {
			users.save(u);
		} catch (RuntimeException ex) {
			files.delete(photoName);
			throw ex;
		}
		boolean sent = mailService.sendCredentials(u, raw, elections.get(), false);
		return new RegistrationResult(u, raw, sent);
	}

	public void updateVoter(Long id, String fullName, String email, String phone, MultipartFile photo) {
		AppUser u = users.findById(id).filter(x -> x.getRole() == Role.VOTER)
				.orElseThrow(() -> new AppException("Voter not found."));
		String mail = validateBasics(fullName, email, phone);
		if (users.existsByEmailAndIdNot(mail, id)) {
			throw new AppException("Another user already uses this e-mail.");
		}
		String newPhoto = files.store(photo);
		String oldPhoto = u.getPhoto();
		u.setFullName(fullName.trim());
		u.setEmail(mail);
		u.setPhone(blankToNull(phone));
		if (newPhoto != null) {
			u.setPhoto(newPhoto);
		}
		users.save(u);
		mailService.sendProfileUpdated(u);
		if (newPhoto != null) {
			files.delete(oldPhoto);
		}
	}

	@Transactional
	public void deleteVoter(Long id) {
		AppUser u = users.findById(id).filter(x -> x.getRole() == Role.VOTER)
				.orElseThrow(() -> new AppException("Voter not found."));
		votes.deleteByVoterId(id);
		users.delete(u);
		files.delete(u.getPhoto());
		mailService.sendAccountDeleted(u);
	}

	// -------------------------------------------------------------- candidates

	public RegistrationResult registerCandidate(String fullName, String email, String phone, String party,
			String manifesto, MultipartFile photo) {
		String mail = validateBasics(fullName, email, phone);
		if (users.existsByEmail(mail)) {
			throw new AppException("A user with this e-mail already exists.");
		}
		String photoName = files.store(photo);
		String raw = generatePassword();
		AppUser u = new AppUser(fullName.trim(), mail, encoder.encode(raw), Role.CANDIDATE);
		u.setPhone(blankToNull(phone));
		u.setPhoto(photoName);
		Candidate c = new Candidate();
		c.setUser(u);
		c.setParty(limit(party, 100));
		c.setManifesto(limit(manifesto, 1000));
		try {
			candidates.save(c); // also saves the user (cascade)
		} catch (RuntimeException ex) {
			files.delete(photoName);
			throw ex;
		}
		boolean sent = mailService.sendCredentials(u, raw, elections.get(), false);
		return new RegistrationResult(u, raw, sent);
	}

	public void updateCandidate(Long candidateId, String fullName, String email, String phone, String party,
			String manifesto, MultipartFile photo) {
		Candidate c = candidates.findById(candidateId).orElseThrow(() -> new AppException("Candidate not found."));
		AppUser u = c.getUser();
		String mail = validateBasics(fullName, email, phone);
		if (users.existsByEmailAndIdNot(mail, u.getId())) {
			throw new AppException("Another user already uses this e-mail.");
		}
		applyCandidateChanges(c, fullName, mail, phone, party, manifesto, photo);
	}

	/** A candidate editing their own profile (name and e-mail stay fixed). */
	public void updateOwnProfile(String email, String phone, String party, String manifesto, MultipartFile photo) {
		Candidate c = candidates.findByUserEmail(email)
				.orElseThrow(() -> new AppException("Candidate profile not found."));
		if (phone != null && !phone.isBlank() && !PHONE.matcher(phone.trim()).matches()) {
			throw new AppException("Please enter a valid phone number.");
		}
		applyCandidateChanges(c, c.getUser().getFullName(), c.getUser().getEmail(), phone, party, manifesto, photo);
	}

	private void applyCandidateChanges(Candidate c, String fullName, String mail, String phone, String party,
			String manifesto, MultipartFile photo) {
		AppUser u = c.getUser();
		String newPhoto = files.store(photo);
		String oldPhoto = u.getPhoto();
		u.setFullName(fullName.trim());
		u.setEmail(mail);
		u.setPhone(blankToNull(phone));
		if (newPhoto != null) {
			u.setPhoto(newPhoto);
		}
		c.setParty(limit(party, 100));
		c.setManifesto(limit(manifesto, 1000));
		candidates.save(c);
		if (newPhoto != null) {
			files.delete(oldPhoto);
		}
	}

	@Transactional
	public void deleteCandidate(Long candidateId) {
		Candidate c = candidates.findById(candidateId).orElseThrow(() -> new AppException("Candidate not found."));
		String photo = c.getUser().getPhoto();
		votes.deleteByCandidateId(candidateId);
		candidates.delete(c); // also deletes the user (cascade)
		files.delete(photo);
	}

	// --------------------------------------------------------------- passwords

	public RegistrationResult resetPassword(Long userId) {
		AppUser u = users.findById(userId).filter(x -> x.getRole() != Role.ADMIN)
				.orElseThrow(() -> new AppException("User not found."));
		String raw = generatePassword();
		u.setPassword(encoder.encode(raw));
		users.save(u);
		boolean sent = mailService.sendCredentials(u, raw, elections.get(), true);
		return new RegistrationResult(u, raw, sent);
	}

	// ----------------------------------------------------------------- helpers
	// ------------------------------------------------------------------ admins

	/** An existing admin creates another admin. A password is generated and e-mailed. */
	public RegistrationResult registerAdmin(String fullName, String email) {
		String mail = validateBasics(fullName, email, null);
		if (users.existsByEmail(mail)) {
			throw new AppException("A user with this e-mail already exists.");
		}
		String raw = generatePassword();
		AppUser u = new AppUser(fullName.trim(), mail, encoder.encode(raw), Role.ADMIN);
		users.save(u);
		boolean sent = mailService.sendCredentials(u, raw, elections.get(), false);
		return new RegistrationResult(u, raw, sent);
	}
	@Transactional
	public void promoteVoterToAdmin(Long id) {
		AppUser voter = users.findById(id)
				.filter(user -> user.getRole() == Role.VOTER)
				.orElseThrow(() -> new AppException("Voter not found."));

		voter.setRole(Role.ADMIN);
		users.save(voter);
	}

	@Transactional
	public void deleteAdmin(Long id, String currentAdminEmail) {
		AppUser u = users.findById(id).filter(x -> x.getRole() == Role.ADMIN)
				.orElseThrow(() -> new AppException("Admin not found."));
		if (u.getEmail().equalsIgnoreCase(currentAdminEmail)) {
			throw new AppException("You cannot delete your own account.");
		}
		if (users.countByRole(Role.ADMIN) <= 1) {
			throw new AppException("At least one admin must remain.");
		}
		users.delete(u);
		files.delete(u.getPhoto());
		
	}

	private String validateBasics(String fullName, String email, String phone) {
		if (fullName == null || fullName.isBlank()) {
			throw new AppException("Please enter the full name.");
		}
		if (fullName.trim().length() > 120) {
			throw new AppException("The name is too long.");
		}
		if (email == null || !EMAIL.matcher(email.trim()).matches()) {
			throw new AppException("Please enter a valid e-mail address.");
		}
		if (phone != null && !phone.isBlank() && !PHONE.matcher(phone.trim()).matches()) {
			throw new AppException("Please enter a valid phone number.");
		}
		return email.trim().toLowerCase();
	}

	private static String generatePassword() {
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
		}
		return sb.toString();
	}

	private static String blankToNull(String s) {
		return (s == null || s.isBlank()) ? null : s.trim();
	}

	private static String limit(String s, int max) {
		if (s == null || s.isBlank()) {
			return null;
		}
		String t = s.trim();
		return t.length() > max ? t.substring(0, max) : t;
	}
}
