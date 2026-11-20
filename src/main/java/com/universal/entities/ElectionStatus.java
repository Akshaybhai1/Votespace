package com.universal.entities;

public enum ElectionStatus {

	NOT_SCHEDULED("Not scheduled", "secondary", "bi-calendar-x"),
	UPCOMING("Upcoming", "info", "bi-hourglass-split"),
	LIVE("Voting is live", "success", "bi-broadcast"),
	ENDED("Voting closed", "dark", "bi-flag-fill");

	private final String label;
	private final String badge;
	private final String icon;

	ElectionStatus(String label, String badge, String icon) {
		this.label = label;
		this.badge = badge;
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	/** Bootstrap colour name for the badge. */
	public String getBadge() {
		return badge;
	}

	public String getIcon() {
		return icon;
	}

	public boolean isLive() {
		return this == LIVE;
	}
}
