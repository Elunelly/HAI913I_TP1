package com.mainApplication;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;
import com.core.ProjectExplorer;
import com.model.project.JavaProject;
import com.parser.ParseConfiguration;

public class Main {
	private static final Logger logger;
	static {
		if (System.getProperty("log.mode") == null)
			System.setProperty("log.mode", "DETAILED");
		if (System.getProperty("log.level") == null)
			System.setProperty("log.level", "trace");
		
		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(String[] args) {
		test5();
	}
	
	public static void test5() {
		ASTProcessor ast = new ASTProcessor();
		logger.debug("Created default ASTProcessor: "+ast);
		logger.debug(ast.processProject("/home/luna/Documents/java").toString());
	}
	
	public static void test4() {
        System.out.println("=== DÉBUT DU PROGRAMME ===");
        
        logger.error("TEST ERROR");
        logger.warn("TEST WARN");
        logger.info("TEST INFO");
        logger.debug("TEST DEBUG");
        logger.trace("TEST TRACE");
        
        System.out.println("=== FIN DU PROGRAMME ===");
    }
	
	public static void test3() {
		int i = 1;
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.info("For your information... I'm dead");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.error("uhh...did someone died??????");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.info("For your information... I'm happy");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.debug("nevermind, dead people can't be happy, problem solved");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.warn("War has been declared!!!!");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.warn("That was a warning btw");
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
		logger.trace("That's my %d trace".formatted(i++));
	}
	
	public static void test2() {
		logger.info("DEFAULT CONFIG:\n{}", ParseConfiguration.defaultConfig());
		System.out.println("\nFAST CONFIG");
		ParseConfiguration configF = ParseConfiguration.fastConfig();
		System.out.println(configF.toString());
		System.out.println("\nCONSERVATIVE CONFIG");
		ParseConfiguration configC = ParseConfiguration.conservativeJLSConfig();
		System.out.println(configC.toString());
		System.out.println("\nSPECIFIC JRE CONFIG");
		ParseConfiguration configS = ParseConfiguration.specificJLSConfig(17);
		System.out.println(configS.toString());
	}
	
	public static void test1() {
		Path dir = Path.of("/home/luna/Documents");
		ProjectExplorer explorer = new ProjectExplorer();
        //explorer.setMaxDepth(4);
		try {
			JavaProject project = explorer.buildJavaProject("myTools", dir);
			System.out.println(explorer.getStatisticsOnCurrentProject());
	        System.out.println(project);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void test0() {
		ASTParser parser = ASTParser.newParser(AST.JLS21);
		String source = "public class Main {\r\n"
				+ "  // Static method\r\n"
				+ "  static void myStaticMethod() {\r\n"
				+ "    System.out.println(\"Static methods can be called without creating objects\");\r\n"
				+ "  }\r\n"
				+ "\r\n"
				+ "  // Public method\r\n"
				+ "  public void myPublicMethod() {\r\n"
				+ "    System.out.println(\"Public methods must be called by creating objects\");\r\n"
				+ "  }\r\n"
				+ "\r\n"
				+ "  // Main method\r\n"
				+ "  public static void main(String[] args) {\r\n"
				+ "    myStaticMethod(); // Call the static method\r\n"
				+ "    // myPublicMethod(); This would compile an error\r\n"
				+ "\r\n"
				+ "    Main myObj = new Main(); // Create an object of Main\r\n"
				+ "    myObj.myPublicMethod(); // Call the public method on the object\r\n"
				+ "  }\r\n"
				+ "}";
		parser.setSource(source.toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration node) {
				System.out.println(
					"("+node.getStartPosition()+") "+
				    "class: "+node.getName()+" "+
					"["+node.getLength()+"]"
				);
				return true;
			}
			
			@Override
			public boolean visit(MethodDeclaration node) {
				System.out.println(
					"  --> "+
					"("+node.getStartPosition()+") "+
				    "method: "+node.getName()+" "+
					"["+node.getLength()+"]"
				);
				return true;
			}
		});
	}

}
