package com.universal.dto;

import java.util.List;

/** Everything the dashboards and charts need to show live results. */
public class ResultsSummary {

	private final String electionTitle;
	private final String status;
	private final String statusLabel;
	private final long totalVotes;
	private final long totalVoters;
	private final double turnoutPercent;
	private final List<CandidateResult> candidates;

	public ResultsSummary(String electionTitle, String status, String statusLabel, long totalVotes, long totalVoters,
			double turnoutPercent, List<CandidateResult> candidates) {
		this.electionTitle = electionTitle;
		this.status = status;
		this.statusLabel = statusLabel;
		this.totalVotes = totalVotes;
		this.totalVoters = totalVoters;
		this.turnoutPercent = turnoutPercent;
		this.candidates = candidates;
	}

	public String getElectionTitle() {
		return electionTitle;
	}

	public String getStatus() {
		return status;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public long getTotalVotes() {
		return totalVotes;
	}

	public long getTotalVoters() {
		return totalVoters;
	}

	public double getTurnoutPercent() {
		return turnoutPercent;
	}

	public List<CandidateResult> getCandidates() {
		return candidates;
	}
}
