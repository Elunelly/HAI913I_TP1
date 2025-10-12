package com.utils.exceptions;

public class TypeMismatchException extends Exception {
	
	private static final long serialVersionUID = 7216670384367050286L;
	private Exception exception;

	public TypeMismatchException() {
		super("TypeMismatchException: ");
		this.exception = null;
	}
	
	public TypeMismatchException(String errorMessage) {
		super("TypeMismatchException: "+errorMessage);
		this.exception = null;
	}
	
	public TypeMismatchException(Exception e, String errorMessage) {
		super("TypeMismatchException: " + errorMessage + ((e!=null)?("\n\t triggered by:" + e.toString()):""));
		this.exception = e;
	}

	public Exception getException() {return this.exception;}
	
}