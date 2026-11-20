package com.universal.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** The single election the admin configures (always row id = 1). */
@Entity
@Table(name = "election")
public class Election {

	private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
	private static final DateTimeFormatter INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	@Id
	private Long id = 1L;

	@Column(nullable = false, length = 150)
	private String title = "Student Council Election";

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	public Election() {
	}

	// ---- helpers for the JSP pages (not stored in the database) ----

	public String getStartDisplay() {
		return startTime == null ? "Not set" : startTime.format(DISPLAY);
	}

	public String getEndDisplay() {
		return endTime == null ? "Not set" : endTime.format(DISPLAY);
	}

	/** Value for an HTML datetime-local input. */
	public String getStartInput() {
		return startTime == null ? "" : startTime.format(INPUT);
	}

	public String getEndInput() {
		return endTime == null ? "" : endTime.format(INPUT);
	}

	public long getStartMillis() {
		return startTime == null ? 0L : startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public long getEndMillis() {
		return endTime == null ? 0L : endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}
