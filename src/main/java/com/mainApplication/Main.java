package com.mainApplication;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
