package com.universal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.universal.entities.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	boolean existsByVoterId(Long voterId);

	Optional<Vote> findByVoterId(Long voterId);

	/** Rows of [candidateId, numberOfVotes]. */
	@Query("select v.candidate.id, count(v) from Vote v group by v.candidate.id")
	List<Object[]> countVotesPerCandidate();

	/** Ids of every voter who has already voted. */
	@Query("select v.voter.id from Vote v")
	List<Long> findVotedVoterIds();

	void deleteByVoterId(Long voterId);

	void deleteByCandidateId(Long candidateId);
}
