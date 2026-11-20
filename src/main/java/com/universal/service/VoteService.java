package com.universal.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/** All the rules about casting a vote live here. */
@Service
public class VoteService {

	static final String ALREADY_VOTED = "You have already voted. Each voter can vote only once.";

	private final AppUserRepository users;
	private final CandidateRepository candidates;
	private final VoteRepository votes;
	private final ElectionService elections;

	public VoteService(AppUserRepository users, CandidateRepository candidates, VoteRepository votes,
			ElectionService elections) {
		this.users = users;
		this.candidates = candidates;
		this.votes = votes;
		this.elections = elections;
	}

	@Transactional
	public Vote castVote(String voterEmail, Long candidateId) {
		AppUser voter = users.findByEmail(voterEmail).orElseThrow(() -> new AppException("Voter account not found."));
		if (voter.getRole() != Role.VOTER) {
			throw new AppException("Only registered voters can cast a vote.");
		}

		Election election = elections.get();
		if (elections.status(election) != ElectionStatus.LIVE) {
			throw new AppException("Voting is not open right now.");
		}

		if (votes.existsByVoterId(voter.getId())) {
			throw new AppException(ALREADY_VOTED);
		}

		Candidate candidate = candidates.findById(candidateId)
				.orElseThrow(() -> new AppException("The selected candidate was not found."));

		try {
			return votes.saveAndFlush(new Vote(voter, candidate));
		} catch (DataIntegrityViolationException ex) {
			// two clicks at the same moment: the UNIQUE(voter_id) constraint stopped the second one
			throw new AppException(ALREADY_VOTED);
		}
	}

	@Transactional(readOnly = true)
	public Optional<Vote> findVote(Long voterId) {
		return votes.findByVoterId(voterId);
	}

	/** Admin action: wipes all votes so a new round can start. */
	@Transactional
	public void resetVotes() {
		votes.deleteAllInBatch();
	}
}
