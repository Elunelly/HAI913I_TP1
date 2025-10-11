package com.mainApplication;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.core.ProjectExplorer;
import com.model.project.JavaProject;

public class Main {

	public static void main(String[] args) {
		test1();
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
