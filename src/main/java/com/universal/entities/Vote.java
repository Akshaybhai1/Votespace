package com.universal.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * One row = one cast vote. The UNIQUE constraint on voter_id is the final
 * safety net: a voter can never vote twice, even if two requests arrive at the
 * same moment.
 */
@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(name = "uk_vote_voter", columnNames = "voter_id"))
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "voter_id")
	private AppUser voter;

	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;

	private LocalDateTime castAt = LocalDateTime.now();

	public Vote() {
	}

	public Vote(AppUser voter, Candidate candidate) {
		this.voter = voter;
		this.candidate = candidate;
		this.castAt = LocalDateTime.now();
	}

	public String getCastAtDisplay() {
		return castAt == null ? "" : castAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AppUser getVoter() {
		return voter;
	}

	public void setVoter(AppUser voter) {
		this.voter = voter;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public LocalDateTime getCastAt() {
		return castAt;
	}

	public void setCastAt(LocalDateTime castAt) {
		this.castAt = castAt;
	}
}
