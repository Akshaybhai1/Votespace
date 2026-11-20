package com.universal.dto;

/** One candidate's line in the results (JSON API + charts). */
public class CandidateResult {

	private final Long id;
	private final String name;
	private final String party;
	private final String photoUrl;
	private final long votes;
	private double percent;
	private int rank;

	public CandidateResult(Long id, String name, String party, String photoUrl, long votes) {
		this.id = id;
		this.name = name;
		this.party = party == null ? "" : party;
		this.photoUrl = photoUrl;
		this.votes = votes;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getParty() {
		return party;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public long getVotes() {
		return votes;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
