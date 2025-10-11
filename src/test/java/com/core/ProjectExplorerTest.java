package com.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.model.project.JavaProject;

/**
 * Simple tests for ProjectExplorer.
 */
class ProjectExplorerTest {
    
    private ProjectExplorer explorer;
    
    @BeforeEach
    void setUp() {
        explorer = new ProjectExplorer();
    }
    
    
    // ========== BASIC DISCOVERY TESTS ==========
    
    @Test
    @DisplayName("Should discover Java files in directory")
    void testDiscoverJavaFiles(@TempDir Path tempDir) throws IOException {
        // Given: Create test files
        createJavaFile(tempDir, "ClassA.java");
        createJavaFile(tempDir, "ClassB.java");
        createTextFile(tempDir, "readme.txt");
        
        // When: Explore directory
        List<File> javaFiles = explorer.exploreDirectory(tempDir);
        
        // Then: Should find only Java files
        assertEquals(2, javaFiles.size());
    }
    
    @Test
    @DisplayName("Should discover Java files recursively")
    void testDiscoverJavaFilesRecursively(@TempDir Path tempDir) throws IOException {
        // Given: Create nested structure
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);
        
        createJavaFile(tempDir, "Root.java");
        createJavaFile(subDir, "Sub.java");
        
        // When: Explore directory
        List<File> javaFiles = explorer.exploreDirectory(tempDir);
        
        // Then: Should find both files
        assertEquals(2, javaFiles.size());
    }
    
    @Test
    @DisplayName("Should exclude target directory")
    void testExcludeTargetDirectory(@TempDir Path tempDir) throws IOException {
        // Given: Create files in target directory
        Path targetDir = tempDir.resolve("target");
        Files.createDirectories(targetDir);
        
        createJavaFile(tempDir, "Source.java");
        createJavaFile(targetDir, "Compiled.java");
        
        // When: Explore directory
        List<File> javaFiles = explorer.exploreDirectory(tempDir);
        
        // Then: Should only find Source.java
        assertEquals(1, javaFiles.size());
        assertTrue(javaFiles.get(0).getName().equals("Source.java"));
    }
    
    
    // ========== PROJECT STRUCTURE TESTS ==========
    
    @Test
    @DisplayName("Should build JavaProject structure")
    void testBuildProjectStructure(@TempDir Path tempDir) throws IOException {
        // Given: Create simple project
        Path comDir = tempDir.resolve("com");
        Path exampleDir = comDir.resolve("example");
        Files.createDirectories(exampleDir);
        
        createJavaFile(exampleDir, "Main.java");
        createJavaFile(exampleDir, "Helper.java");
        
        // When: Build project structure
        JavaProject project = explorer.buildJavaProject("TestProject", tempDir);
        
        // Then: Should create project with packages
        assertNotNull(project);
        assertEquals("TestProject", project.getName());
        assertTrue(project.hasPackage("com.example"));
    }
    
    
    // ========== ERROR HANDLING TESTS ==========
    
    @Test
    @DisplayName("Should throw exception for non-existent path")
    void testNonExistentPath() {
        // Given: Non-existent path
        Path nonExistent = Path.of("/does/not/exist");
        
        // When/Then: Should throw exception
        assertThrows(IOException.class, () -> {
            explorer.exploreDirectory(nonExistent);
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null path")
    void testNullPath() {
        // When/Then: Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            explorer.exploreDirectory(null);
        });
    }
    
    
    // ========== CONFIGURATION TESTS ==========
    
    @Test
    @DisplayName("Should respect max depth setting")
    void testMaxDepth(@TempDir Path tempDir) throws IOException {
        // Given: Deep nesting
        Path level1 = tempDir.resolve("level1");
        Path level2 = level1.resolve("level2");
        Path level3 = level2.resolve("level3");
        Files.createDirectories(level3);
        
        createJavaFile(tempDir, "Root.java");
        createJavaFile(level1, "L1.java");
        createJavaFile(level2, "L2.java");
        createJavaFile(level3, "L3.java");
        
        explorer.setMaxDepth(2);
        
        // When: Explore with max depth
        List<File> files = explorer.exploreDirectory(tempDir);
        
        // Then: Should only find files within depth
        assertEquals(3, files.size()); // Root, L1, L2 (not L3)
    }
    
    @Test
    @DisplayName("Should allow custom exclude patterns")
    void testCustomExcludePattern(@TempDir Path tempDir) throws IOException {
        // Given: Files with different names
        createJavaFile(tempDir, "Main.java");
        createJavaFile(tempDir, "MainTest.java");
        createJavaFile(tempDir, "Helper.java");
        
        // When: Explore (default excludes test files)
        List<File> files = explorer.exploreDirectory(tempDir);
        
        // Then: Should exclude test files
        assertEquals(2, files.size());
        assertTrue(files.stream().noneMatch(f -> f.getName().contains("Test")));
    }
    
    @Test
    @DisplayName("Should clear exclude patterns")
    void testClearExcludePatterns(@TempDir Path tempDir) throws IOException {
        // Given: Test files
        createJavaFile(tempDir, "Main.java");
        createJavaFile(tempDir, "MainTest.java");
        
        explorer.clearExcludedPatterns();
        
        // When: Explore without exclusions
        List<File> files = explorer.exploreDirectory(tempDir);
        
        // Then: Should find all files
        assertEquals(2, files.size());
    }
    
    
    // ========== HELPER METHODS ==========
    
    private void createJavaFile(Path directory, String filename) throws IOException {
        Path filePath = directory.resolve(filename);
        Files.writeString(filePath, "public class " + filename.replace(".java", "") + " {}");
    }
    
    private void createTextFile(Path directory, String filename) throws IOException {
        Path filePath = directory.resolve(filename);
        Files.writeString(filePath, "This is a text file");
    }
}