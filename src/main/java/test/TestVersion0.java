package test;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import parser.ASTParserFacade;

public class TestVersion0 {
	
	public static void testParseConfigurationClass() {
		
	}
	
	public static void testASTParserFacadeClass_SimpleParsing() {
        System.out.println("--- Test 1: Simple Parsing ---");
        
        // Simple Java class as String
        String javaCode = """
                public class HelloWorld {
                    private String message;
                    
                    public HelloWorld(String message) {
                        this.message = message;
                    }
                    
                    public void printMessage() {
                        System.out.println(message);
                    }
                }
                """;
        
        try {
            // Create parser
            ASTParserFacade parser = ASTParserFacade.createDefault();
            
            // Parse the code
            CompilationUnit cu = parser.parseSource(javaCode);
            
            // Check if parsing succeeded
            if (cu != null) {
                System.out.println("Parsing successful!");
                System.out.println("   Types found: " + cu.types().size());
                
                // Get the first type (class)
                if (!cu.types().isEmpty()) {
                    TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
                    System.out.println("   Class name: " + type.getName().getIdentifier());
                    System.out.println("   Methods: " + type.getMethods().length);
                    System.out.println("   Fields: " + type.getFields().length);
                }
            } else {
                System.out.println("Parsing failed - CompilationUnit is null");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
	}
	
	public static void testASTParserFacadeClass_ASTExploration() {
        System.out.println("--- Test 2: AST Exploration ---");
        
        String javaCode = """
                package com.example;
                
                import java.util.List;
                
                public class User {
                    private String name;
                    private int age;
                    
                    public User(String name, int age) {
                        this.name = name;
                        this.age = age;
                    }
                    
                    public String getName() {
                        return name;
                    }
                    
                    public void setName(String name) {
                        this.name = name;
                    }
                    
                    public int getAge() {
                        return age;
                    }
                }
                """;
        
        try {
            ASTParserFacade parser = new ASTParserFacade();
            CompilationUnit cu = parser.parseSource(javaCode);
            
            // Display package
            if (cu.getPackage() != null) {
                System.out.println("Package: " + cu.getPackage().getName());
            }
            
            // Display imports
            System.out.println("Imports: " + cu.imports().size());
            
            // Explore types (classes)
            for (Object typeObj : cu.types()) {
                if (typeObj instanceof TypeDeclaration) {
                    TypeDeclaration type = (TypeDeclaration) typeObj;
                    exploreClass(type);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println();
	}
	
    private static void exploreClass(TypeDeclaration type) {
        System.out.println("\n🏛  Class: " + type.getName().getIdentifier());
        
        // Explore fields
        FieldDeclaration[] fields = type.getFields();
        System.out.println("     Fields: " + fields.length);
        for (FieldDeclaration field : fields) {
            exploreField(field);
        }
        
        // Explore methods
        MethodDeclaration[] methods = type.getMethods();
        System.out.println("   ⚙  Methods: " + methods.length);
        for (MethodDeclaration method : methods) {
            exploreMethod(method);
        }
    }
    private static void exploreField(FieldDeclaration field) {
        String visibility = getVisibility(field.getModifiers());
        String type = field.getType().toString();
        
        for (Object fragObj : field.fragments()) {
            if (fragObj instanceof VariableDeclarationFragment) {
                VariableDeclarationFragment frag = (VariableDeclarationFragment) fragObj;
                String name = frag.getName().getIdentifier();
                System.out.println("        " + visibility + " " + type + " " + name);
            }
        }
    }
    private static void exploreMethod(MethodDeclaration method) {
        String visibility = getVisibility(method.getModifiers());
        String name = method.getName().getIdentifier();
        int paramCount = method.parameters().size();
        boolean isConstructor = method.isConstructor();
        
        String methodType = isConstructor ? "Constructor" : "Method";
        System.out.println("      🔹 " + visibility + " " + methodType + " " + name + "(" + paramCount + " params)");
    }
    private static String getVisibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) return "public";
        if (Modifier.isPrivate(modifiers)) return "private";
        if (Modifier.isProtected(modifiers)) return "protected";
        return "package";
    }

}
