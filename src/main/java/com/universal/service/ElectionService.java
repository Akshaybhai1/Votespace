package com.universal.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universal.entities.Election;
import com.universal.entities.ElectionStatus;
import com.universal.exception.AppException;
import com.universal.repository.ElectionRepository;

@Service
public class ElectionService {

	private static final Long ELECTION_ID = 1L;

	private final ElectionRepository repo;

	public ElectionService(ElectionRepository repo) {
		this.repo = repo;
	}

	/** Returns the election, creating the default row the first time. */
	@Transactional
	public Election get() {
		return repo.findById(ELECTION_ID).orElseGet(() -> repo.save(new Election()));
	}

	public ElectionStatus status(Election e) {
		return status(e, LocalDateTime.now());
	}

	public ElectionStatus status(Election e, LocalDateTime now) {
		if (e == null || e.getStartTime() == null || e.getEndTime() == null) {
			return ElectionStatus.NOT_SCHEDULED;
		}
		if (now.isBefore(e.getStartTime())) {
			return ElectionStatus.UPCOMING;
		}
		if (now.isBefore(e.getEndTime())) {
			return ElectionStatus.LIVE;
		}
		return ElectionStatus.ENDED;
	}

	@Transactional
	public Election update(String title, LocalDateTime start, LocalDateTime end) {
		if (title == null || title.isBlank()) {
			throw new AppException("Please enter an election title.");
		}
		if (start == null || end == null) {
			throw new AppException("Please choose both the start and the end date & time.");
		}
		if (!end.isAfter(start)) {
			throw new AppException("The end time must be after the start time.");
		}
		Election e = get();
		e.setTitle(title.trim());
		e.setStartTime(start);
		e.setEndTime(end);
		return repo.save(e);
	}
}
