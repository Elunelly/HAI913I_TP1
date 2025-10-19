package com.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.config.ParseConfiguration;
import com.core.exceptions.ExplorationError;
import com.exceptions.ASTError;
import com.parser.exceptions.ParsingFileError;

public class ASTParserFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTParserFacade.class);
	
	private ParseConfiguration config;
	private ASTParser parser;
	
	public ASTParserFacade() {
		this(ParseConfiguration.defaultConfig());
		logger.trace("{} -> ASTParserFacade()");
	}
	
	public ASTParserFacade(ParseConfiguration config) {
		logger.trace("{} -> ASTParserFacade(ParseConfiguration)");
		this.config = config;
		this.parser = createParser();
	}
	
	public CompilationUnit parseString(String javaString, String unitName) {
		logger.trace("{} -> parseString(String,String)");
		if (javaString == null || javaString.isBlank()) {
			IllegalArgumentException error = new IllegalArgumentException("Java code cannot be empty or null");
			logger.error(error.getLocalizedMessage());
			throw error;
		}
		unitName = unitName!=null ? unitName : this.config.getDefaultUnitName();
		this.parser.setSource(javaString.toCharArray());
		this.parser.setUnitName(unitName);
		
		CompilationUnit compilationUnit = (CompilationUnit) this.parser.createAST(null);
		if (compilationUnit!=null)
			logger.debug("Successfully created the CompilationUnit '{}'",unitName);
		return compilationUnit;
	}
	
	public CompilationUnit parseString(String javaString) {
		logger.trace("{} -> parseString(String)");
		return parseString(javaString, null);
	}
	
	public Map.Entry<String,CompilationUnit> parseFile(File javaFile) {
		logger.trace("{} -> parseFile(File)");
		if (javaFile == null || !javaFile.exists()) {
			IllegalArgumentException error = new IllegalArgumentException("Java file "+javaFile+" does not exist");
			logger.error(error.getLocalizedMessage());
			throw error;
		}
		if (!javaFile.getName().endsWith(".java")) {
			IllegalArgumentException error = new IllegalArgumentException(javaFile+" is not a Java file");
			logger.error(error.getLocalizedMessage());
			throw error;
		}
		try {
			String content = Files.readString(javaFile.toPath());
			logger.debug("Successfully read the file: {}",javaFile.getName());
			CompilationUnit result = parseString(content, javaFile.getName());
			String packageName = result.getPackage() != null 
	                ? result.getPackage().getName().getFullyQualifiedName() 
	                : "";
			String fileName = javaFile.getName();
			return Map.entry(
					"%s.%s".formatted(
						packageName,
						fileName.substring(0, fileName.length()-5)
					),
					result);
		} catch (IOException e) {
			RuntimeException error = new RuntimeException("Error reading file "+javaFile.getName(), e);
			logger.error(error.getLocalizedMessage());
			throw error;
		}
	}
	
	public Map<String,CompilationUnit> parseFiles(List<ASTError> errorsCollector, List<File> files) {
		logger.trace("{} -> parseFiles(List<ASTError>,List<File>)");
		Map<String,CompilationUnit> result = new HashMap<>();
		for (File file : files) {
			try {
				Map.Entry<String,CompilationUnit> parsedFile = parseFile(file);
				result.put(parsedFile.getKey(),parsedFile.getValue());
			} catch (IllegalArgumentException e) {
				logger.error(e.getLocalizedMessage());
				if (errorsCollector != null)
					errorsCollector.add(new ExplorationError(file.toPath(), e.getMessage(), e));
			} catch (RuntimeException e) {
				logger.error(e.getLocalizedMessage());
				if (errorsCollector != null)
					errorsCollector.add(new ParsingFileError(file, e.getMessage(), e));
			}
		}
		return result;
		
	}
	
	public Map<String,CompilationUnit> parseFiles(List<File> files) {
		logger.trace("{} -> parseFiles(List<File>)");
		return parseFiles(null, files);
	}
	
	private ASTParser createParser() {
		logger.trace("{} -> createParser()");
		ASTParser parser = ASTParser.newParser(this.config.getCurrentJLS());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(false);
		return parser;
	}
	
	public ParseConfiguration getConfig() {
		logger.trace("{} -> getConfig()");
		return this.config;
	}
	
	public ASTParser getParser() {
		logger.trace("{} -> getParser()");
		return this.parser;
	}
	
	public static ASTParserFacade createDefault() {
		logger.trace("{} -> createDefault()");
		return new ASTParserFacade();
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)");
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ASTParserFacade that = (ASTParserFacade) obj;
		return 
			Objects.equals(this.config, that.config) &&
			Objects.equals(this.parser, that.parser)
		;
	}
	
	@Override
	public int hashCode() {
		logger.trace("{} -> hashCode()");
		return Objects.hash(config, parser);
	}
	
	@Override
	public String toString() {
		logger.trace("{} -> toString()");
		return (this.getClass().getSimpleName()+"{"
				+ "config=%s, "
				+ "parser=%s}")
				.formatted(config, parser);
	}

}
