package com.universal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.universal.entities.AppUser;
import com.universal.entities.Candidate;
import com.universal.entities.Election;
import com.universal.entities.ElectionStatus;
import com.universal.entities.Role;
import com.universal.entities.Vote;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.repository.CandidateRepository;
import com.universal.repository.VoteRepository;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

	@Mock
	AppUserRepository users;
	@Mock
	CandidateRepository candidates;
	@Mock
	VoteRepository votes;
	@Mock
	ElectionService elections;

	@InjectMocks
	VoteService service;

	private AppUser voter(Role role) {
		AppUser u = new AppUser("Test Voter", "v@x.com", "hash", role);
		u.setId(1L);
		return u;
	}

	@Test
	void voteIsSavedWhenElectionIsLiveAndVoterHasNotVoted() {
		Election e = new Election();
		Candidate c = new Candidate();
		c.setId(5L);
		when(users.findByEmail("v@x.com")).thenReturn(Optional.of(voter(Role.VOTER)));
		when(elections.get()).thenReturn(e);
		when(elections.status(e)).thenReturn(ElectionStatus.LIVE);
		when(votes.existsByVoterId(1L)).thenReturn(false);
		when(candidates.findById(5L)).thenReturn(Optional.of(c));
		when(votes.saveAndFlush(any(Vote.class))).thenAnswer(i -> i.getArgument(0));

		Vote v = service.castVote("v@x.com", 5L);

		assertEquals(c, v.getCandidate());
		verify(votes).saveAndFlush(any(Vote.class));
	}

	@Test
	void secondVoteIsRejected() {
		Election e = new Election();
		when(users.findByEmail("v@x.com")).thenReturn(Optional.of(voter(Role.VOTER)));
		when(elections.get()).thenReturn(e);
		when(elections.status(e)).thenReturn(ElectionStatus.LIVE);
		when(votes.existsByVoterId(1L)).thenReturn(true);

		assertThrows(AppException.class, () -> service.castVote("v@x.com", 5L));
		verify(votes, never()).saveAndFlush(any(Vote.class));
	}

	@Test
	void voteIsRejectedWhenElectionIsNotLive() {
		Election e = new Election();
		when(users.findByEmail("v@x.com")).thenReturn(Optional.of(voter(Role.VOTER)));
		when(elections.get()).thenReturn(e);
		when(elections.status(e)).thenReturn(ElectionStatus.ENDED);

		assertThrows(AppException.class, () -> service.castVote("v@x.com", 5L));
		verify(votes, never()).saveAndFlush(any(Vote.class));
	}

	@Test
	void adminCannotVote() {
		when(users.findByEmail("v@x.com")).thenReturn(Optional.of(voter(Role.ADMIN)));

		assertThrows(AppException.class, () -> service.castVote("v@x.com", 5L));
		verify(votes, never()).saveAndFlush(any(Vote.class));
	}

	@Test
	void databaseUniqueConstraintIsReportedAsAlreadyVoted() {
		Election e = new Election();
		Candidate c = new Candidate();
		c.setId(5L);
		when(users.findByEmail("v@x.com")).thenReturn(Optional.of(voter(Role.VOTER)));
		when(elections.get()).thenReturn(e);
		when(elections.status(e)).thenReturn(ElectionStatus.LIVE);
		when(votes.existsByVoterId(1L)).thenReturn(false);
		when(candidates.findById(5L)).thenReturn(Optional.of(c));
		when(votes.saveAndFlush(any(Vote.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

		AppException ex = assertThrows(AppException.class, () -> service.castVote("v@x.com", 5L));
		assertEquals(VoteService.ALREADY_VOTED, ex.getMessage());
	}
}
