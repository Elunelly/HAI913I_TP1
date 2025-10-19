package com.mainApplication;

import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;
import com.core.AnalysisResult;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.utils.table.TableUI;
import com.visitors.metrics.LOCVisitor;
import com.visitors.structural.ClassStructureVisitor;

public class MainTemp {
    
    private static final Logger logger = LoggerFactory.getLogger(MainTemp.class);
	static {
		if (System.getProperty("log.mode") == null)
			System.setProperty("log.mode", "DETAILED");
		if (System.getProperty("log.level") == null)
			System.setProperty("log.level", "info");
	}
    
    public static void main(String[] args) {
        printHeader();
        
        String projectPath = determineProjectPath(args);
        System.out.println("Target project: " + projectPath);
        System.out.println();
        
        try {
            AnalysisResult result = analyzeProject(projectPath);
            
            displayResults(result);
            
            displayDetailedClassInfo(result);
            
        } catch (Exception e) {
            logger.error("Analysis failed", e);
            System.err.println("\nERROR: " + e.getMessage());
            System.err.println("Please check the logs for more details.");
        }
    }
    
    private static void printHeader() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║        Java AST Static Analysis Tool - Demo            ║");
        System.out.println("║              Structural Analysis Module                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private static String determineProjectPath(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        
        // Default: analyze current project's source code
//        Path currentPath = Path.of("/home/luna/Documents/java/luna.java.utils");
//        if (currentPath.toFile().exists()) {
//            return currentPath.toString();
//        }
        
        return ".";
    }
    
    private static AnalysisResult analyzeProject(String projectPath) {
        System.out.println("Starting analysis pipeline...\n");
        
        ASTProcessor processor = new ASTProcessor();
        
        processor.addVisitor(new ClassStructureVisitor());
        processor.addVisitor(new LOCVisitor());
        
        AnalysisResult result = processor.processProject(projectPath);
        
        System.out.println("\nAnalysis completed successfully!\n");
        
        return result;
    }
    
    private static void displayResults(AnalysisResult result) {
        ProjectInfo project = result.getProject();
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("                   ANALYSIS SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        Object[][] projectData = {
            {"Project Name", project.getName()},
            {"Root Path", project.getRootPath().toString()},
            {"Total Packages", String.valueOf(result.getTotalPackagesCount())},
            {"Total Classes", String.valueOf(result.getTotalClassesCount())},
            {"Total Methods", String.valueOf(result.getTotalMethodsCount())},
            {"Total Fields", String.valueOf(result.getTotalFieldsCount())}
        };
        
        System.out.println(TableUI.titledTable("Project Overview", List.of(projectData), ":"));
        System.out.println();
        
        displayPackageBreakdown(project);
    }
    
    private static void displayPackageBreakdown(ProjectInfo project) {
        if (!project.hasPackages()) {
            System.out.println("No packages found.");
            return;
        }
        
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("                 PACKAGE BREAKDOWN");
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println();
        
        List<PackageInfo> packages = project.copyPackages();
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
        
        int displayCount = Math.min(15, classes.size());
        
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
    
    private static void displaySingleClass(ClassInfo classInfo, int index) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println(String.format(" Class #%d: %s", index, classInfo.getQualifiedName()));
        System.out.println("─────────────────────────────────────────────────────");
        
        System.out.println("  Type       : " + (classInfo.isInterface() ? "Interface" : "Class"));
        System.out.println("  Visibility : " + classInfo.getVisibility().name().toLowerCase());
        System.out.println("  Package    : " + (classInfo.getPackageName().isEmpty() ? "(default)" : classInfo.getPackageName()));
        
        if (classInfo.getSuperClass() != null) {
            System.out.println("  Extends    : " + classInfo.getSuperClass());
        }
        
        if (!classInfo.getInterfaces().isEmpty()) {
            System.out.println("  Implements : " + String.join(", ", classInfo.getInterfaces()));
        }
        
        if (classInfo.getMetrics().hasData()) {
	        System.out.println("Available Metrics:");
	        for (Entry<Enum<?>,Object> entry : classInfo.getMetrics().copyData().entrySet()) {
	        	System.out.println("    -> %-15s: %s".formatted(entry.getKey(),entry.getValue()));
	        }
        }
        
        List<MethodInfo> methods = classInfo.getMethods();
        System.out.println();
        System.out.println("  📋 Methods (" + methods.size() + "):");
        
        if (methods.isEmpty()) {
            System.out.println("     (no methods)");
        } else {
            for (MethodInfo method : methods) {
                System.out.println(method.getFullSignature()+"  [%d]".formatted(method.getMetrics().getLOC()));
            }
        }
        
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