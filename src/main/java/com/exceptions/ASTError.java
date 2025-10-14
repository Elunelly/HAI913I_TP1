package com.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ASTError extends Exception {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTError.class);

	private static final long serialVersionUID = 1L;
	private final LocalDateTime when = LocalDateTime.now();
	
	public ASTError() {
		super();
		logger.error(this.toString());
	}
	
	public abstract Object getContext();
	
	public abstract String getMessage();
	
	public abstract Throwable getCause();
	
	public LocalDateTime getWhen() {return when;}
	
	public String formatWhen(DateTimeFormatter formatter) {
		return when.format(formatter);
	}
	
	public String formatWhen(String formatPattern) {
		return formatWhen(DateTimeFormatter.ofPattern(formatPattern));
	}
	
	public String formatWhenOnDate() {
		return formatWhen(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
	public String formatWhenOnTime() {
		return formatWhen(DateTimeFormatter.ISO_LOCAL_TIME);
	}
	
	public String formatWhenOnDateTime() {
		return formatWhen("yyyy-MM-dd | HH:mm:ss.SSS");
	}
	
	@Override
	public String toString() {
		return String.format(this.getClass().getSimpleName()+" caused by %s: %s (cause=%s)", getContext(), getMessage(), getCause());
	}

}
