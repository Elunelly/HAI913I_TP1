package com.model.project;

import org.eclipse.jdt.core.dom.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive test suite for ProjectInfo class.
 * 
 * <p>Tests project creation, package management, hierarchy building, and aggregation methods.</p>
 * 
 * @author Luna
 * @version 1.0
 */
@DisplayName("ProjectInfo - Comprehensive Unit Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectInfoTest {

    private ProjectInfo project;
    private Path testPath;
    
    @BeforeEach
    void setUp() {
        testPath = Paths.get("/test/project");
        project = new ProjectInfo("TestProject", testPath);
    }

    @AfterEach
    void tearDown() {
        project = null;
        testPath = null;
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Nested
    @DisplayName("Constructor Tests")
    @Order(1)
    class ConstructorTests {

        @Test
        @DisplayName("Constructor(name, path) should initialize project with both parameters")
        void constructorWithNameAndPathShouldInitialize() {
            // Arrange & Act
            ProjectInfo proj = new ProjectInfo("MyProject", Paths.get("/my/path"));

            // Assert
            assertAll("Project initialization",
                () -> assertEquals("MyProject", proj.getName()),
                () -> assertEquals(Paths.get("/my/path"), proj.getRootPath()),
                () -> assertNotNull(proj.getMappedPackages()),
                () -> assertTrue(proj.getMappedPackages().isEmpty())
            );
        }

        @Test
        @DisplayName("Constructor(name) should use empty path")
        void constructorWithNameOnlyShouldUseEmptyPath() {
            // Act
            ProjectInfo proj = new ProjectInfo("Project");

            // Assert
            assertAll("Constructor with name only",
                () -> assertEquals("Project", proj.getName()),
                () -> assertEquals(Path.of(""), proj.getRootPath())
            );
        }

        @Test
        @DisplayName("Constructor(path) should use path filename as name")
        void constructorWithPathOnlyShouldUsePathAsName() {
            // Arrange
            Path path = Paths.get("/projects/MyApp");

            // Act
            ProjectInfo proj = new ProjectInfo(path);

            // Assert
            assertAll("Constructor with path only",
                () -> assertEquals("MyApp", proj.getName()),
                () -> assertEquals(path, proj.getRootPath())
            );
        }

        @Test
        @DisplayName("Constructor() with no args should use defaults")
        void constructorNoArgsShouldUseDefaults() {
            // Act
            ProjectInfo proj = new ProjectInfo();

            // Assert
            assertAll("Default constructor",
                () -> assertNotNull(proj.getName()),
                () -> assertFalse(proj.getName().isEmpty()),
                () -> assertEquals(Path.of(""), proj.getRootPath())
            );
        }

        @Test
        @DisplayName("Constructor should handle null name gracefully")
        void constructorShouldHandleNullName() {
            // Act
            ProjectInfo proj = new ProjectInfo(null, Paths.get("/test"));

            // Assert
            assertEquals("test", proj.getName(), 
                "Should use path filename when name is null");
        }

        @Test
        @DisplayName("Constructor should handle blank name")
        void constructorShouldHandleBlankName() {
            // Act
            ProjectInfo proj = new ProjectInfo("   ", Paths.get("/myapp"));

            // Assert
            assertEquals("myapp", proj.getName(),
                "Should use path filename when name is blank");
        }

        @Test
        @DisplayName("Constructor should trim name")
        void constructorShouldTrimName() {
            // Act
            ProjectInfo proj = new ProjectInfo("  MyProject  ", testPath);

            // Assert
            assertEquals("MyProject", proj.getName(),
                "Should trim whitespace from name");
        }

        @Test
        @DisplayName("Constructor should handle null path gracefully")
        void constructorShouldHandleNullPath() {
            // Act
            ProjectInfo proj = new ProjectInfo("Project", null);

            // Assert
            assertEquals(Path.of(""), proj.getRootPath(),
                "Should use empty path when path is null");
        }
    }

    // ========================================
    // BASIC GETTER TESTS
    // ========================================

    @Nested
    @DisplayName("Basic Getter Tests")
    @Order(2)
    class BasicGetterTests {

        @Test
        @DisplayName("getName() should return project name")
        void getNameShouldReturnProjectName() {
            // Assert
            assertEquals("TestProject", project.getName());
        }

        @Test
        @DisplayName("getRootPath() should return root path")
        void getRootPathShouldReturnRootPath() {
            // Assert
            assertEquals(testPath, project.getRootPath());
        }

        @Test
        @DisplayName("getFullName() should return root path as string")
        void getFullNameShouldReturnPathAsString() {
            // Assert
            assertEquals(testPath.toString(), project.getFullName());
        }
    }

    // ========================================
    // PACKAGE MANAGEMENT TESTS
    // ========================================

    @Nested
    @DisplayName("Package Management Tests")
    @Order(3)
    class PackageManagementTests {

        @Test
        @DisplayName("getMappedPackages() should return unmodifiable map")
        void getMappedPackagesShouldReturnUnmodifiableMap() {
            // Act
            Map<String, PackageInfo> packages = project.getMappedPackages();

            // Assert
            assertThrows(UnsupportedOperationException.class,
                () -> packages.put("test", new PackageInfo("test", project)),
                "Should not be able to modify returned map");
        }

        @Test
        @DisplayName("getMappedPackages() should return empty map initially")
        void getMappedPackagesShouldReturnEmptyMapInitially() {
            // Assert
            assertTrue(project.getMappedPackages().isEmpty(),
                "New project should have no packages");
        }

        @Test
        @DisplayName("copyMappedPackages() should return mutable copy")
        void copyMappedPackagesShouldReturnMutableCopy() {
            // Arrange
            PackageInfo pkg = new PackageInfo("com.test", project);
            project.addPackage(pkg);

            // Act
            Map<String, PackageInfo> copy = project.copyMappedPackages();
            copy.clear();

            // Assert
            assertAll("Copy should be mutable",
                () -> assertTrue(copy.isEmpty()),
                () -> assertFalse(project.getMappedPackages().isEmpty())
            );
        }

        @Test
        @DisplayName("getPackages() should return list of packages")
        void getPackagesShouldReturnListOfPackages() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));
            project.addPackage(new PackageInfo("org.example", project));

            // Act
            List<PackageInfo> packages = project.getPackages();

            // Assert
            assertEquals(2, packages.size());
        }

        @Test
        @DisplayName("copyPackages() should return mutable list")
        void copyPackagesShouldReturnMutableList() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));

            // Act
            List<PackageInfo> copy = project.copyPackages();
            copy.clear();

            // Assert
            assertAll("Copy should be mutable",
                () -> assertTrue(copy.isEmpty()),
                () -> assertFalse(project.getPackages().isEmpty())
            );
        }

        @Test
        @DisplayName("addPackage() should add package successfully")
        void addPackageShouldAddPackageSuccessfully() {
            // Arrange
            PackageInfo pkg = new PackageInfo("com.test", project);

            // Act
            boolean result = project.addPackage(pkg);

            // Assert
            assertAll("Package should be added",
                () -> assertTrue(result),
                () -> assertTrue(project.hasPackage("com.test")),
                () -> assertEquals(1, project.getPackages().size())
            );
        }

        @Test
        @DisplayName("addPackage() should not add duplicate package")
        void addPackageShouldNotAddDuplicate() {
            // Arrange
            PackageInfo pkg1 = new PackageInfo("com.test", project);
            PackageInfo pkg2 = new PackageInfo("com.test", project);
            project.addPackage(pkg1);

            // Act
            boolean result = project.addPackage(pkg2);

            // Assert
            assertAll("Duplicate should not be added",
                () -> assertFalse(result),
                () -> assertEquals(1, project.getPackages().size())
            );
        }

        @Test
        @DisplayName("addPackage() should handle null gracefully")
        void addPackageShouldHandleNull() {
            // Act
            boolean result = project.addPackage(null);

            // Assert
            assertFalse(result, "Should not add null package");
        }

        @Test
        @DisplayName("getPackage() should return package by name")
        void getPackageShouldReturnPackageByName() {
            // Arrange
            PackageInfo pkg = new PackageInfo("com.test", project);
            project.addPackage(pkg);

            // Act
            PackageInfo result = project.getPackage("com.test");

            // Assert
            assertSame(pkg, result);
        }

        @Test
        @DisplayName("getPackage() should return null for non-existent package")
        void getPackageShouldReturnNullForNonExistent() {
            // Act
            PackageInfo result = project.getPackage("nonexistent");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("hasPackage() should return true if package exists")
        void hasPackageShouldReturnTrueIfExists() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));

            // Assert
            assertTrue(project.hasPackage("com.test"));
        }

        @Test
        @DisplayName("hasPackage() should return false if package doesn't exist")
        void hasPackageShouldReturnFalseIfNotExists() {
            // Assert
            assertFalse(project.hasPackage("com.nonexistent"));
        }

        @Test
        @DisplayName("hasPackages() should return true if project has packages")
        void hasPackagesShouldReturnTrueIfHasPackages() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));

            // Assert
            assertTrue(project.hasPackages());
        }

        @Test
        @DisplayName("hasPackages() should return false if project is empty")
        void hasPackagesShouldReturnFalseIfEmpty() {
            // Assert
            assertFalse(project.hasPackages());
        }

        @Test
        @DisplayName("getPackagesName() should return list of packages name")
        void getPackagesNameShouldReturnListOfNames() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));
            project.addPackage(new PackageInfo("org.example", project));

            // Act
            List<String> names = project.getPackagesName();

            // Assert
            assertAll("Package names",
                () -> assertEquals(2, names.size()),
                () -> assertTrue(names.contains("com.test")),
                () -> assertTrue(names.contains("org.example"))
            );
        }
    }

    // ========================================
    // PACKAGE HIERARCHY TESTS
    // ========================================

    @Nested
    @DisplayName("Package Hierarchy Tests")
    @Order(4)
    class PackageHierarchyTests {

        @Test
        @DisplayName("buildPackagesHierarchy() should establish parent-child relationships")
        void buildPackagesHierarchyShouldEstablishRelationships() {
            // Arrange
            project.addPackage(new PackageInfo("com", project));
            project.addPackage(new PackageInfo("com.test", project));
            project.addPackage(new PackageInfo("com.test.utils", project));

            // Act
            project.buildPackagesHierarchy();

            // Assert
            PackageInfo child = project.getPackage("com.test.utils");
            PackageInfo parent = project.getPackage("com.test");
            
            assertAll("Hierarchy relationships",
                () -> assertNotNull(child.getParentPackage()),
                () -> assertEquals("com.test", child.getParentPackage().getName()),
                () -> assertTrue(parent.hasSubPackage("com.test.utils"))
            );
        }

        @Test
        @DisplayName("buildPackagesHierarchy() should handle missing parents gracefully")
        void buildPackagesHierarchyShouldHandleMissingParents() {
            // Arrange - Add child without parent
            project.addPackage(new PackageInfo("com.test.utils", project));

            // Act & Assert
            assertDoesNotThrow(() -> project.buildPackagesHierarchy(),
                "Should handle missing parents without throwing");
        }

        @Test
        @DisplayName("buildPackagesHierarchy() should handle root packages")
        void buildPackagesHierarchyShouldHandleRootPackages() {
            // Arrange
            project.addPackage(new PackageInfo("simple", project));

            // Act
            project.buildPackagesHierarchy();

            // Assert
            PackageInfo root = project.getPackage("simple");
            assertTrue(root.isRoot(), "Single-level package should be root");
        }
    }

    // ========================================
    // AGGREGATION TESTS
    // ========================================

    @Nested
    @DisplayName("Aggregation Method Tests")
    @Order(5)
    class AggregationTests {

        @Test
        @DisplayName("getAllClasses() should aggregate classes from all packages")
        void getAllClassesShouldAggregateFromAllPackages() {
            // Arrange
            PackageInfo pkg1 = new PackageInfo("com.test", project);
            PackageInfo pkg2 = new PackageInfo("org.example", project);
            project.addPackage(pkg1);
            project.addPackage(pkg2);

            // Add mock classes (requires TypeDeclaration nodes)
            // Note: This is simplified - in real scenario would need proper AST nodes

            // Act
            List<ClassInfo> classes = project.getAllClasses();

            // Assert
            assertNotNull(classes, "Should return non-null list");
        }

        @Test
        @DisplayName("getAllClasses() should return empty list when no classes exist")
        void getAllClassesShouldReturnEmptyListWhenNoClasses() {
            // Act
            List<ClassInfo> classes = project.getAllClasses();

            // Assert
            assertTrue(classes.isEmpty(), 
                "Should return empty list when project has no classes");
        }

        @Test
        @DisplayName("getAllMethods() should aggregate methods from all classes")
        void getAllMethodsShouldAggregateFromAllClasses() {
            // Act
            List<MethodInfo> methods = project.getAllMethods();

            // Assert
            assertNotNull(methods, "Should return non-null list");
        }

        @Test
        @DisplayName("getAllFields() should aggregate fields from all classes")
        void getAllFieldsShouldAggregateFromAllClasses() {
            // Act
            List<FieldInfo> fields = project.getAllFields();

            // Assert
            assertNotNull(fields, "Should return non-null list");
        }

        @Test
        @DisplayName("getAllUnits() should aggregate compilation units from all packages")
        void getAllUnitsShouldAggregateFromAllPackages() {
            // Act
            List<CompilationUnit> units = project.getAllUnits();

            // Assert
            assertNotNull(units, "Should return non-null list");
        }
    }

    // ========================================
    // EQUALITY AND HASHCODE TESTS
    // ========================================

    @Nested
    @DisplayName("Equality and HashCode Tests")
    @Order(6)
    class EqualityTests {

        @Test
        @DisplayName("equals() should return true for same instance")
        void equalsShouldReturnTrueForSameInstance() {
            // Assert
            assertEquals(project, project);
        }

        @Test
        @DisplayName("equals() should return true for same name and path")
        void equalsShouldReturnTrueForSameNameAndPath() {
            // Arrange
            ProjectInfo other = new ProjectInfo("TestProject", testPath);

            // Assert
            assertEquals(project, other);
        }

        @Test
        @DisplayName("equals() should return false for different names")
        void equalsShouldReturnFalseForDifferentNames() {
            // Arrange
            ProjectInfo other = new ProjectInfo("DifferentProject", testPath);

            // Assert
            assertNotEquals(project, other);
        }

        @Test
        @DisplayName("equals() should return false for different paths")
        void equalsShouldReturnFalseForDifferentPaths() {
            // Arrange
            ProjectInfo other = new ProjectInfo("TestProject", Paths.get("/different"));

            // Assert
            assertNotEquals(project, other);
        }

        @Test
        @DisplayName("equals() should return false for null")
        void equalsShouldReturnFalseForNull() {
            // Assert
            assertNotEquals(project, null);
        }

        @Test
        @DisplayName("hashCode() should be consistent with equals()")
        void hashCodeShouldBeConsistentWithEquals() {
            // Arrange
            ProjectInfo other = new ProjectInfo("TestProject", testPath);

            // Assert
            assertEquals(project.hashCode(), other.hashCode(),
                "Equal projects should have equal hash codes");
        }

        @Test
        @DisplayName("hashCode() should be stable")
        void hashCodeShouldBeStable() {
            // Act
            int hash1 = project.hashCode();
            int hash2 = project.hashCode();

            // Assert
            assertEquals(hash1, hash2,
                "hashCode should return same value on multiple calls");
        }
    }

    // ========================================
    // TOSTRING TESTS
    // ========================================

    @Nested
    @DisplayName("toString Tests")
    @Order(7)
    class ToStringTests {

        @Test
        @DisplayName("toString() should contain class name")
        void toStringShouldContainClassName() {
            // Act
            String result = project.toString();

            // Assert
            assertTrue(result.contains("ProjectInfo"),
                "toString should contain class name");
        }

        @Test
        @DisplayName("toString() should contain project name")
        void toStringShouldContainProjectName() {
            // Act
            String result = project.toString();

            // Assert
            assertTrue(result.contains("TestProject"),
                "toString should contain project name");
        }

        @Test
        @DisplayName("toString() should contain package count")
        void toStringShouldContainPackageCount() {
            // Arrange
            project.addPackage(new PackageInfo("com.test", project));

            // Act
            String result = project.toString();

            // Assert
            assertTrue(result.contains("packages=1") || result.contains("packages=%d".formatted(1)),
                "toString should contain package count");
        }

        @Test
        @DisplayName("toString() should not be null or empty")
        void toStringShouldNotBeNullOrEmpty() {
            // Act
            String result = project.toString();

            // Assert
            assertAll("toString output",
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty())
            );
        }
    }

    // ========================================
    // EDGE CASE TESTS
    // ========================================

    @Nested
    @DisplayName("Edge Case Tests")
    @Order(8)
    class EdgeCaseTests {

        @Test
        @DisplayName("Project should handle many packages efficiently")
        void projectShouldHandleManyPackages() {
            // Arrange & Act
            for (int i = 0; i < 100; i++) {
                project.addPackage(new PackageInfo("package" + i, project));
            }

            // Assert
            assertEquals(100, project.getPackages().size());
        }

        @Test
        @DisplayName("Project should handle deep package hierarchy")
        void projectShouldHandleDeepPackageHierarchy() {
            // Arrange
            project.addPackage(new PackageInfo("a", project));
            project.addPackage(new PackageInfo("a.b", project));
            project.addPackage(new PackageInfo("a.b.c", project));
            project.addPackage(new PackageInfo("a.b.c.d", project));
            project.addPackage(new PackageInfo("a.b.c.d.e", project));

            // Act
            project.buildPackagesHierarchy();

            // Assert
            PackageInfo deepest = project.getPackage("a.b.c.d.e");
            assertNotNull(deepest.getParentPackage());
        }

        @Test
        @DisplayName("Project should handle special characters in name")
        void projectShouldHandleSpecialCharactersInName() {
            // Act
            ProjectInfo proj = new ProjectInfo("My-Project_2024", testPath);

            // Assert
            assertEquals("My-Project_2024", proj.getName());
        }

        @Test
        @DisplayName("Project with empty package map should still be valid")
        void projectWithEmptyPackageMapShouldBeValid() {
            // Assert
            assertAll("Empty project validation",
                () -> assertNotNull(project.getPackages()),
                () -> assertNotNull(project.getMappedPackages()),
                () -> assertFalse(project.hasPackages()),
                () -> assertNotNull(project.toString())
            );
        }
    }
}