package com.universal.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Everyone who can log in: admin, candidate or voter. The e-mail is the username. */
@Entity
@Table(name = "app_users")
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String fullName;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	/** BCrypt hash - the real password is never stored. */
	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;

	@Column(length = 20)
	private String phone;

	/** File name of the uploaded photo (inside app.upload-dir). */
	private String photo;

	private LocalDateTime createdAt = LocalDateTime.now();

	public AppUser() {
	}

	public AppUser(String fullName, String email, String password, Role role) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	/** URL (without context path) of the photo, or a default avatar. */
	public String getPhotoUrl() {
		return (photo == null || photo.isBlank()) ? "/images/avatar.svg" : "/uploads/" + photo;
	}

	public boolean isAdmin() {
		return role == Role.ADMIN;
	}

	public boolean isCandidate() {
		return role == Role.CANDIDATE;
	}

	public boolean isVoter() {
		return role == Role.VOTER;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
