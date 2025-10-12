package com.ui.cli;

public enum CLIOutputTypes {
	
	OFF		(0, 0, 0),
	FATAL	(6, 100, 0),
	ERROR	(5, 200, 0),
	WARN	(4, 300, 0),
	INFO	(3, 400, 0),
	DEBUG	(2, 500, 0),
	TRACE	(1, 600, 0);
	
	private final int severityLevel;
	private final int verbosityValue;
	private final int color;
	
	private CLIOutputTypes(int severity, int verbosity, int color) {
		this.severityLevel = severity;
		this.verbosityValue = verbosity;
		this.color = color;
	}

}
