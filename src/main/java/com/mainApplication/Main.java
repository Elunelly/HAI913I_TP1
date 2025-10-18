package com.mainApplication;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;
import com.core.AnalysisResult;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;

public class Main {
	private static final Logger logger;
	static {
		if (System.getProperty("log.mode") == null)
			System.setProperty("log.mode", "DETAILED");
		if (System.getProperty("log.level") == null)
			System.setProperty("log.level", "debug");
		
		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(String[] args) {
		Path path = Path.of("/home/luna/Documents/java/luna.java.utils");
		System.out.println("Starting analyzing project from "+path.toString());
		ASTProcessor ast = new ASTProcessor();
		AnalysisResult result = ast.processProject(path.toString());
		System.out.println("End of analyzing...");
		System.out.println();
		ProjectInfo project = result.getProject();
		System.out.println("Informations about Project '"+project.getName()+"':");
		System.out.println(project.toStringTable());
		for (PackageInfo p : project.copyPackages()) {
			System.out.println(p.toStringTable()+p.withUnits());
			System.out.println();
		}
	}

}
