package com.universal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universal.entities.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

	Optional<Candidate> findByUserEmail(String email);

	List<Candidate> findAllByOrderByUserFullNameAsc();
}
