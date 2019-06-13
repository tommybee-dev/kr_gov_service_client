package com.tobee.net.http;

public class KMAException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public KMAException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
	
	public KMAException(String errorMessage) {
        super(errorMessage);
    }
	
}
