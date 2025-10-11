package com.core;

import java.nio.file.Path;

class ExplorationError extends Exception {
	
	private static final long serialVersionUID = 1L;
	private final Path path;
	private final String message;
	private final Exception cause;

	public ExplorationError(Path path, String message, Exception cause) {
		this.path = path;
		this.message = message;
		this.cause = cause;
	}

	public Path getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}

	public Exception getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return String.format("Error at %s: %s", path, message);
	}
	
}