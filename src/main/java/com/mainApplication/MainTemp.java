package com.mainApplication;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;
import com.core.AnalysisResult;
import com.model.project.ProjectInfo;
import com.model.project.PackageInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.utils.table.TableUI;

/**
 * Simple main class for testing the AST Analysis Tool.
 * 
 * <p>This class demonstrates basic usage of the analysis pipeline:
 * <ol>
 *   <li>Project exploration and file discovery</li>
 *   <li>AST parsing of Java files</li>
 *   <li>Structural information extraction (classes, methods, fields)</li>
 *   <li>Results presentation</li>
 * </ol>
 * 
 * <p><strong>Usage:</strong>
 * <pre>
 * // Analyze current project
 * java -cp target/classes com.Main
 * 
 * // Analyze specific project
 * java -cp target/classes com.Main /path/to/project
 * </pre>
 * 
 * @author Luna
 * @version 1.0
 */
public class MainTemp {
    
    private static final Logger logger = LoggerFactory.getLogger(MainTemp.class);
	static {
		if (System.getProperty("log.mode") == null)
			System.setProperty("log.mode", "DETAILED");
		if (System.getProperty("log.level") == null)
			System.setProperty("log.level", "info");
	}
    
    /**
     * Entry point for the AST Analysis demonstration.
     * 
     * @param args optional: path to project to analyze (defaults to current project)
     */
    public static void main(String[] args) {
        printHeader();
        
        // Determine project path
        String projectPath = determineProjectPath(args);
        System.out.println("Target project: " + projectPath);
        System.out.println();
        
        try {
            // Execute analysis
            AnalysisResult result = analyzeProject(projectPath);
            
            // Display results
            displayResults(result);
            
            // Display detailed class information
            displayDetailedClassInfo(result);
            
        } catch (Exception e) {
            logger.error("Analysis failed", e);
            System.err.println("\n❌ ERROR: " + e.getMessage());
            System.err.println("Please check the logs for more details.");
        }
    }
    
    /**
     * Prints a welcome header with tool information.
     */
    private static void printHeader() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║        Java AST Static Analysis Tool - Demo            ║");
        System.out.println("║              Structural Analysis Module                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Determines which project path to analyze.
     * 
     * @param args command-line arguments
     * @return the project path to analyze
     */
    private static String determineProjectPath(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        
        // Default: analyze current project's source code
        Path currentPath = Path.of("/home/luna/Documents/java");
        if (currentPath.toFile().exists()) {
            return currentPath.toString();
        }
        
        // Fallback: current directory
        return ".";
    }
    
    /**
     * Executes the full analysis pipeline on the specified project.
     * 
     * @param projectPath path to the Java project
     * @return AnalysisResult containing all extracted information
     */
    private static AnalysisResult analyzeProject(String projectPath) {
        System.out.println("🔍 Starting analysis pipeline...\n");
        
        // Create processor with default configuration
        ASTProcessor processor = new ASTProcessor();
        
        // Process project (exploration + parsing + visitor execution)
        AnalysisResult result = processor.processProject(projectPath);
        
        System.out.println("\n✅ Analysis completed successfully!\n");
        
        return result;
    }
    
    /**
     * Displays a summary of analysis results.
     * 
     * @param result the analysis result to display
     */
    private static void displayResults(AnalysisResult result) {
        ProjectInfo project = result.getProject();
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("                   ANALYSIS SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // Project overview
        String[][] projectData = {
            {"Project Name", project.getName()},
            {"Root Path", project.getRootPath().toString()},
            {"Total Packages", String.valueOf(result.getTotalPackagesCount())},
            {"Total Classes", String.valueOf(result.getTotalClassesCount())},
            {"Total Methods", String.valueOf(result.getTotalMethodsCount())},
            {"Total Fields", String.valueOf(result.getTotalFieldsCount())}
        };
        
        System.out.println(TableUI.titledTable("Project Overview", List.of(projectData), ":"));
        System.out.println();
        
        // Package breakdown
        displayPackageBreakdown(project);
    }
    
    /**
     * Displays a breakdown of classes per package.
     * 
     * @param project the Java project to analyze
     */
    private static void displayPackageBreakdown(ProjectInfo project) {
        if (!project.hasPackages()) {
            System.out.println("No packages found.");
            return;
        }
        
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("                 PACKAGE BREAKDOWN");
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println();
        
        List<PackageInfo> packages = project.getPackages();
        packages.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        
        for (PackageInfo pkg : packages) {
            int classCount = pkg.getClasses().size();
            String packageName = pkg.getName().isEmpty() ? "(default package)" : pkg.getName();
            System.out.printf("  📦 %-40s → %2d class%s%n", 
                packageName, 
                classCount, 
                classCount != 1 ? "es" : "");
        }
        
        System.out.println();
    }
    
    /**
     * Displays detailed information about the first few classes (limited to 5).
     * 
     * @param result the analysis result containing class information
     */
    private static void displayDetailedClassInfo(AnalysisResult result) {
        List<ClassInfo> classes = result.getClasses();
        
        if (classes.isEmpty()) {
            System.out.println("No classes found to display.");
            return;
        }
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("              DETAILED CLASS INFORMATION");
        System.out.println("         (Showing first 5 classes for brevity)");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        int displayCount = Math.min(5, classes.size());
        
        for (int i = 0; i < displayCount; i++) {
            ClassInfo classInfo = classes.get(i);
            displaySingleClass(classInfo, i + 1);
        }
        
        if (classes.size() > displayCount) {
            System.out.println(String.format("... and %d more class%s", 
                classes.size() - displayCount,
                (classes.size() - displayCount) != 1 ? "es" : ""));
        }
    }
    
    /**
     * Displays detailed information about a single class.
     * 
     * @param classInfo the class to display
     * @param index the display index (for numbering)
     */
    private static void displaySingleClass(ClassInfo classInfo, int index) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println(String.format(" Class #%d: %s", index, classInfo.getQualifiedName()));
        System.out.println("─────────────────────────────────────────────────────");
        
        // Basic info
        System.out.println("  Type       : " + (classInfo.isInterface() ? "Interface" : "Class"));
        System.out.println("  Visibility : " + classInfo.getVisibility().name().toLowerCase());
        System.out.println("  Package    : " + (classInfo.getPackageName().isEmpty() ? "(default)" : classInfo.getPackageName()));
        
        // Inheritance
        if (classInfo.getSuperClass() != null) {
            System.out.println("  Extends    : " + classInfo.getSuperClass());
        }
        
        if (!classInfo.getInterfaces().isEmpty()) {
            System.out.println("  Implements : " + String.join(", ", classInfo.getInterfaces()));
        }
        
        // Methods
        List<MethodInfo> methods = classInfo.getMethods();
        System.out.println();
        System.out.println("  📋 Methods (" + methods.size() + "):");
        
        if (methods.isEmpty()) {
            System.out.println("     (no methods)");
        } else {
            for (MethodInfo method : methods) {
                String visibility = method.getVisibility().toString();
                String returnType = method.isConstructor() ? "" : method.getReturnType() + " ";
                String params = method.getParameters().isEmpty() ? 
                    "()" : 
                    "(" + String.join(", ", method.getParameters()) + ")";
                
                System.out.println(String.format("     %s %s%s%s", 
                    visibility, 
                    returnType, 
                    method.getName(), 
                    params));
            }
        }
        
        // Fields
        List<FieldInfo> fields = classInfo.getFields();
        System.out.println();
        System.out.println("  📦 Fields (" + fields.size() + "):");
        
        if (fields.isEmpty()) {
            System.out.println("     (no fields)");
        } else {
            for (FieldInfo field : fields) {
                String visibility = field.getVisibility().toString();
                System.out.println(String.format("     %s %s %s", 
                    visibility, 
                    field.getType(), 
                    field.getName()));
            }
        }
        
        System.out.println();
    }
}