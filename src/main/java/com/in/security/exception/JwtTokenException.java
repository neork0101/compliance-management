package com.in.security.exception;

public class JwtTokenException extends RuntimeException {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public JwtTokenException(String message) {
        super(message);
    }

}
