package com.utils.exceptions;

public class OutOfBoundsException extends Exception {
	
	private static final long serialVersionUID = 7216670384367050286L;
	private Exception exception;

	public OutOfBoundsException() {
		super("OutOfBoundsException: ");
		this.exception = null;
	}
	
	public OutOfBoundsException(String errorMessage) {
		super("OutOfBoundsException: "+errorMessage);
		this.exception = null;
	}
	
	public OutOfBoundsException(Exception e, String errorMessage) {
		super("OutOfBoundsException: " + errorMessage + ((e!=null)?("\n\t triggered by:" + e.toString()):""));
		this.exception = e;
	}

	public Exception getException() {return this.exception;}
	
}