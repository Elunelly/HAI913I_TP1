package com.parser.exceptions;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exceptions.ASTError;

public class ParsingFileError extends ASTError {
	
	private static final Logger logger = LoggerFactory.getLogger(ParsingFileError.class);
	
	private static final long serialVersionUID = 1L;
	private final File file;
	private final String message;
	private final Exception cause;

	public ParsingFileError(File file, String message, Exception cause) {
		super();
		this.file = file;
		this.message = message;
		this.cause = cause;
		logger.error(this.toString());
	}

	@Override
	public File getContext() {
		logger.trace("{} -> getContext()",file.getName());
		return file;
	}

	@Override
	public String getMessage() {
		logger.trace("{} -> getMessage()",file.getName());
		return message;
	}

	@Override
	public Exception getCause() {
		logger.trace("{} -> getCause()",file.getName());
		return cause;
	}

	@Override
	public String toString() {
		logger.trace("{} -> toString()",file.getName());
		return String.format(this.getClass().getSimpleName()+" on %s: %s", file.getName(), message);
	}

}
