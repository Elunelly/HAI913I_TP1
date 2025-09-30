package parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTParserFacade {
	
	private ParseConfiguration config;
	private ASTParser parser;
	
	public ASTParserFacade() {
		this(ParseConfiguration.defaultConfig());
	}
	
	public ASTParserFacade(ParseConfiguration config) {
		this.config = config;
		this.parser = createParser();
	}
	
	public CompilationUnit parseSource(String javaString, String source) {
		if (javaString == null || javaString.isBlank()) throw new IllegalArgumentException("Java code cannot be empty or null");
		this.parser.setSource(javaString.toCharArray());
		this.parser.setUnitName(source!=null ? source : this.config.getUnitName());
		
		CompilationUnit compilationUnit = (CompilationUnit) this.parser.createAST(null);
		return compilationUnit;
	}
	
	public CompilationUnit parseSource(String javaString) {
		return parseSource(javaString, null);
	}
	
	public CompilationUnit parseFile(File javaFile) {
		if (javaFile == null || !javaFile.exists()) throw new IllegalArgumentException("Java file "+javaFile+" does not exist");
		if (!javaFile.getName().endsWith(".java")) throw new IllegalArgumentException(javaFile+" is not a Java file");
		try {
			String content = Files.readString(javaFile.toPath());
			return parseSource(content, javaFile.getName());
		} catch (IOException e) {
			throw new RuntimeException("Error reading file "+javaFile.getName(), e);
		}
	}
	
	private ASTParser createParser() {
		ASTParser parser = ASTParser.newParser(this.config.getCurrentJLS());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(false); // See ParseConfiguration.getResolveBindings() later on!!
		return parser;
	}
	
	public ParseConfiguration getConfig() {return this.config;}
	
	public ASTParser getParser() {return this.parser;}
	
	public static ASTParserFacade createDefault() {
		return new ASTParserFacade();
	}

}
