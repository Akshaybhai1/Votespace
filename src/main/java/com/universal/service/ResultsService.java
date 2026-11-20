package com.universal.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universal.dto.CandidateResult;
import com.universal.dto.ResultsSummary;
import com.universal.entities.Candidate;
import com.universal.entities.Election;
import com.universal.entities.ElectionStatus;
import com.universal.entities.Role;
import com.universal.repository.AppUserRepository;
import com.universal.repository.CandidateRepository;
import com.universal.repository.VoteRepository;

@Service
public class ResultsService {

	private final CandidateRepository candidates;
	private final VoteRepository votes;
	private final AppUserRepository users;
	private final ElectionService elections;

	public ResultsService(CandidateRepository candidates, VoteRepository votes, AppUserRepository users,
			ElectionService elections) {
		this.candidates = candidates;
		this.votes = votes;
		this.users = users;
		this.elections = elections;
	}

	@Transactional(readOnly = true)
	public Map<Long, Long> voteCounts() {
		Map<Long, Long> counts = new HashMap<>();
		for (Object[] row : votes.countVotesPerCandidate()) {
			counts.put((Long) row[0], (Long) row[1]);
		}
		return counts;
	}

	@Transactional(readOnly = true)
	public ResultsSummary summary() {
		Map<Long, Long> counts = voteCounts();
		long totalVotes = votes.count();
		long totalVoters = users.countByRole(Role.VOTER);

		List<CandidateResult> rows = new ArrayList<>();
		for (Candidate c : candidates.findAll()) {
			rows.add(new CandidateResult(c.getId(), c.getUser().getFullName(), c.getParty(),
					c.getUser().getPhotoUrl(), counts.getOrDefault(c.getId(), 0L)));
		}
		rows.sort(Comparator.comparingLong(CandidateResult::getVotes).reversed()
				.thenComparing(CandidateResult::getName, String.CASE_INSENSITIVE_ORDER));

		int rank = 0;
		long previousVotes = -1;
		for (int i = 0; i < rows.size(); i++) {
			CandidateResult r = rows.get(i);
			if (r.getVotes() != previousVotes) {
				rank = i + 1;
				previousVotes = r.getVotes();
			}
			r.setRank(rank);
			r.setPercent(totalVotes == 0 ? 0.0 : round1(r.getVotes() * 100.0 / totalVotes));
		}

		Election election = elections.get();
		ElectionStatus status = elections.status(election);
		double turnout = totalVoters == 0 ? 0.0 : round1(totalVotes * 100.0 / totalVoters);
		return new ResultsSummary(election.getTitle(), status.name(), status.getLabel(), totalVotes, totalVoters,
				turnout, rows);
	}

	private static double round1(double v) {
		return Math.round(v * 10.0) / 10.0;
	}
}
