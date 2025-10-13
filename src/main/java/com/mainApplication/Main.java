package com.mainApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;

public class Main {
	private static final Logger logger;
	static {
		if (System.getProperty("log.mode") == null)
			System.setProperty("log.mode", "DETAILED");
		if (System.getProperty("log.level") == null)
			System.setProperty("log.level", "info");
		
		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(String[] args) {
		ASTProcessor ast = new ASTProcessor();
		logger.debug("Created default ASTProcessor: "+ast);
		logger.debug(ast.processProject("/home/luna/Documents/java").toString());
	}

}
