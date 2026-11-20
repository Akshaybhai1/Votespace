package com.universal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.universal.entities.Election;
import com.universal.entities.ElectionStatus;
import com.universal.exception.AppException;
import com.universal.repository.ElectionRepository;

class ElectionServiceTest {

	private final ElectionService service = new ElectionService(Mockito.mock(ElectionRepository.class));

	private Election election(LocalDateTime start, LocalDateTime end) {
		Election e = new Election();
		e.setStartTime(start);
		e.setEndTime(end);
		return e;
	}

	@Test
	void statusFollowsTheClock() {
		LocalDateTime start = LocalDateTime.of(2026, 11, 20, 10, 0);
		LocalDateTime end = LocalDateTime.of(2026, 11, 20, 18, 0);
		Election e = election(start, end);

		assertEquals(ElectionStatus.UPCOMING, service.status(e, start.minusMinutes(1)));
		assertEquals(ElectionStatus.LIVE, service.status(e, start.plusHours(1)));
		assertEquals(ElectionStatus.ENDED, service.status(e, end.plusMinutes(1)));
	}

	@Test
	void noScheduleMeansNotScheduled() {
		assertEquals(ElectionStatus.NOT_SCHEDULED, service.status(new Election(), LocalDateTime.now()));
	}

	@Test
	void endMustBeAfterStart() {
		LocalDateTime t = LocalDateTime.of(2026, 11, 20, 10, 0);
		assertThrows(AppException.class, () -> service.update("Test", t, t));
	}
}
