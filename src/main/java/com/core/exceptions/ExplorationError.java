package com.core.exceptions;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exceptions.ASTError;

public class ExplorationError extends ASTError {
	
	private static final Logger logger = LoggerFactory.getLogger(ExplorationError.class);
	
	private static final long serialVersionUID = 1L;
	private final Path path;
	private final String message;
	private final Exception cause;

	public ExplorationError(Path path, String message, Exception cause) {
		super();
		this.path = path;
		this.message = message;
		this.cause = cause;
		logger.error(this.toString());
	}

	@Override
	public Path getContext() {
		return path;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Exception getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return String.format("ExplorationError at %s: %s", path, message);
	}
	
}