package com.universal.exception;

/** A business-rule problem that is shown to the user (duplicate e-mail, voting closed, ...). */
public class AppException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AppException(String message) {
		super(message);
	}
}
