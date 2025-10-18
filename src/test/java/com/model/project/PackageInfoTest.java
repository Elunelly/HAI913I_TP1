package com.model.project;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.model.structural.ClassInfo;

/**
 * Comprehensive test suite for PackageInfo class.
 * 
 * <p>Tests package hierarchy, class management, compilation units, and aggregation methods.</p>
 * 
 * @author Luna
 * @version 1.0
 */
@DisplayName("PackageInfo - Comprehensive Unit Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PackageInfoTest {

    private ProjectInfo project;
    private PackageInfo packageInfo;
    
    private CompilationUnit createMockCompilationUnit() {
        AST ast = AST.newAST(AST.JLS21, false);
        return ast.newCompilationUnit();
    }

    private TypeDeclaration createMockTypeDeclaration(String name) {
        CompilationUnit cu = createMockCompilationUnit();
        AST ast = cu.getAST();
        
        TypeDeclaration typeDecl = ast.newTypeDeclaration();
        typeDecl.setName(ast.newSimpleName(name));
        cu.types().add(typeDecl);
        
        return typeDecl;
    }

    @BeforeEach
    void setUp() {
        project = new ProjectInfo("TestProject", Paths.get("/test"));
        packageInfo = new PackageInfo("com.test", project);
    }

    @AfterEach
    void tearDown() {
        project = null;
        packageInfo = null;
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Nested
    @DisplayName("Constructor Tests")
    @Order(1)
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize package with name and project")
        void constructorShouldInitialize() {
            // Arrange & Act
            PackageInfo pkg = new PackageInfo("org.example", project);

            // Assert
            assertAll("Package initialization",
                () -> assertEquals("org.example", pkg.getName()),
                () -> assertSame(project, pkg.getProject()),
                () -> assertNotNull(pkg.getSubPackages()),
                () -> assertNotNull(pkg.getClasses())
            );
        }

        @Test
        @DisplayName("Constructor should throw NPE for null project")
        void constructorShouldRejectNullProject() {
            // Act & Assert
            assertThrows(NullPointerException.class,
                () -> new PackageInfo("com.test", null),
                "Should reject null project");
        }

        @Test
        @DisplayName("Constructor should accept empty package name")
        void constructorShouldAcceptEmptyName() {
            // Act
            PackageInfo pkg = new PackageInfo("", project);

            // Assert
            assertEquals("", pkg.getName(),
                "Should accept empty name for default package");
        }
    }

    // ========================================
    // NAME TESTS
    // ========================================

    @Nested
    @DisplayName("Name Method Tests")
    @Order(2)
    class NameTests {

        @Test
        @DisplayName("getName() should return full package name")
        void getNameShouldReturnFullName() {
            // Assert
            assertEquals("com.test", packageInfo.getName());
        }

        @Test
        @DisplayName("getLastName() should return last component")
        void getLastNameShouldReturnLastComponent() {
            // Arrange
            PackageInfo pkg = new PackageInfo("com.example.utils", project);

            // Act & Assert
            assertEquals("utils", pkg.getLastName());
        }

        @Test
        @DisplayName("getLastName() should return full name for single component")
        void getLastNameShouldReturnFullNameForSingleComponent() {
            // Arrange
            PackageInfo pkg = new PackageInfo("myapp", project);

            // Act & Assert
            assertEquals("myapp", pkg.getLastName());
        }

        @Test
        @DisplayName("hasName() should return true for non-empty name")
        void hasNameShouldReturnTrueForNonEmpty() {
            // Assert
            assertTrue(packageInfo.hasName());
        }

        @Test
        @DisplayName("hasName() should return false for empty name")
        void hasNameShouldReturnFalseForEmpty() {
            // Arrange
            PackageInfo pkg = new PackageInfo("", project);

            // Assert
            assertFalse(pkg.hasName());
        }

        @Test
        @DisplayName("splitPackageName() should split into components")
        void splitPackageNameShouldSplitIntoComponents() {
            // Act
            List<String> components = packageInfo.splitPackageName();

            // Assert
            assertAll("Split components",
                () -> assertEquals(2, components.size()),
                () -> assertEquals("com", components.get(0)),
                () -> assertEquals("test", components.get(1))
            );
        }

        @Test
        @DisplayName("splitPackageName() should return empty list for empty name")
        void splitPackageNameShouldReturnEmptyListForEmptyName() {
            // Arrange
            PackageInfo pkg = new PackageInfo("", project);

            // Act
            List<String> components = pkg.splitPackageName();

            // Assert
            assertTrue(components.isEmpty());
        }

        @Test
        @DisplayName("getDepth() should return number of components")
        void getDepthShouldReturnNumberOfComponents() {
            // Arrange & Act & Assert
            assertAll("Package depths",
                () -> assertEquals(2, new PackageInfo("com.test", project).getDepth()),
                () -> assertEquals(3, new PackageInfo("com.test.utils", project).getDepth()),
                () -> assertEquals(1, new PackageInfo("simple", project).getDepth()),
                () -> assertEquals(0, new PackageInfo("", project).getDepth())
            );
        }
    }

    // ========================================
    // PROJECT TESTS
    // ========================================

    @Nested
    @DisplayName("Project Reference Tests")
    @Order(3)
    class ProjectTests {

        @Test
        @DisplayName("getProject() should return parent project")
        void getProjectShouldReturnParentProject() {
            // Assert
            assertSame(project, packageInfo.getProject());
        }
    }

    // ========================================
    // PARENT PACKAGE TESTS
    // ========================================

    @Nested
    @DisplayName("Parent Package Tests")
    @Order(4)
    class ParentPackageTests {

        @Test
        @DisplayName("getParentPackage() should return null initially")
        void getParentPackageShouldReturnNullInitially() {
            // Assert
            assertNull(packageInfo.getParentPackage());
        }

        @Test
        @DisplayName("isRoot() should return true when no parent")
        void isRootShouldReturnTrueWhenNoParent() {
            // Assert
            assertTrue(packageInfo.isRoot());
        }

        @Test
        @DisplayName("isRoot() should return false when has parent")
        void isRootShouldReturnFalseWhenHasParent() {
            // Arrange
            PackageInfo parent = new PackageInfo("com", project);
            PackageInfo child = new PackageInfo("com.test", project);
            child.setParentPackage(parent);

            // Assert
            assertFalse(child.isRoot());
        }

        @Test
        @DisplayName("getParentPackageName() should return '(default)' for root")
        void getParentPackageNameShouldReturnDefaultForRoot() {
            // Assert
            assertEquals("(default)", packageInfo.getParentPackageName());
        }

        @Test
        @DisplayName("getParentPackageName() should return parent name when has parent")
        void getParentPackageNameShouldReturnParentName() {
            // Arrange
            PackageInfo parent = new PackageInfo("com", project);
            packageInfo.setParentPackage(parent);

            // Assert
            assertEquals("com", packageInfo.getParentPackageName());
        }

        @Test
        @DisplayName("isChildOf() should return true for ancestor package")
        void isChildOfShouldReturnTrueForAncestor() {
            // Arrange
            PackageInfo parent = new PackageInfo("com", project);
            PackageInfo child = new PackageInfo("com.test.utils", project);

            // Assert
            assertTrue(child.isChildOf(parent));
        }

        @Test
        @DisplayName("isChildOf() should return false for unrelated package")
        void isChildOfShouldReturnFalseForUnrelated() {
            // Arrange
            PackageInfo other = new PackageInfo("org.example", project);

            // Assert
            assertFalse(packageInfo.isChildOf(other));
        }

        @Test
        @DisplayName("isParentOf() should return true for descendant package")
        void isParentOfShouldReturnTrueForDescendant() {
            // Arrange
            PackageInfo parent = new PackageInfo("com", project);
            PackageInfo child = new PackageInfo("com.test.utils", project);

            // Assert
            assertTrue(parent.isParentOf(child));
        }

        @Test
        @DisplayName("getAllAncestors() should return list of ancestors")
        void getAllAncestorsShouldReturnListOfAncestors() {
            // Arrange
            PackageInfo root = new PackageInfo("com", project);
            PackageInfo middle = new PackageInfo("com.test", project);
            PackageInfo leaf = new PackageInfo("com.test.utils", project);
            
            middle.setParentPackage(root);
            leaf.setParentPackage(middle);

            // Act
            List<PackageInfo> ancestors = leaf.getAllAncestors();

            // Assert
            assertAll("Ancestors",
                () -> assertEquals(2, ancestors.size()),
                () -> assertTrue(ancestors.contains(middle)),
                () -> assertTrue(ancestors.contains(root))
            );
        }

        @Test
        @DisplayName("getAllAncestors() should return empty list for root")
        void getAllAncestorsShouldReturnEmptyListForRoot() {
            // Act
            List<PackageInfo> ancestors = packageInfo.getAllAncestors();

            // Assert
            assertTrue(ancestors.isEmpty());
        }
    }

    // ========================================
    // SUB-PACKAGE TESTS
    // ========================================

    @Nested
    @DisplayName("Sub-Package Management Tests")
    @Order(5)
    class SubPackageTests {

        @Test
        @DisplayName("getSubPackages() should return unmodifiable list")
        void getSubPackagesShouldReturnUnmodifiableList() {
            // Act
            List<PackageInfo> subPackages = packageInfo.getSubPackages();

            // Assert
            assertThrows(UnsupportedOperationException.class,
                () -> subPackages.add(new PackageInfo("test", project)),
                "Should not be able to modify returned list");
        }

        @Test
        @DisplayName("copySubPackages() should return mutable copy")
        void copySubPackagesShouldReturnMutableCopy() {
            // Arrange
            PackageInfo sub = new PackageInfo("com.test.utils", project);
            packageInfo.addSubPackage(sub);

            // Act
            List<PackageInfo> copy = packageInfo.copySubPackages();
            copy.clear();

            // Assert
            assertAll("Copy should be mutable",
                () -> assertTrue(copy.isEmpty()),
                () -> assertFalse(packageInfo.getSubPackages().isEmpty())
            );
        }

        @Test
        @DisplayName("addSubPackage() should add sub-package successfully")
        void addSubPackageShouldAddSubPackageSuccessfully() {
            // Arrange
            PackageInfo sub = new PackageInfo("com.test.utils", project);

            // Act
            boolean result = packageInfo.addSubPackage(sub);

            // Assert
            assertAll("Sub-package should be added",
                () -> assertTrue(result),
                () -> assertTrue(packageInfo.hasSubPackage("com.test.utils")),
                () -> assertEquals(1, packageInfo.getSubPackages().size())
            );
        }

        @Test
        @DisplayName("addSubPackage() should not add duplicate")
        void addSubPackageShouldNotAddDuplicate() {
            // Arrange
            PackageInfo sub1 = new PackageInfo("com.test.utils", project);
            PackageInfo sub2 = new PackageInfo("com.test.utils", project);
            packageInfo.addSubPackage(sub1);

            // Act
            boolean result = packageInfo.addSubPackage(sub2);

            // Assert
            assertAll("Duplicate should not be added",
                () -> assertFalse(result),
                () -> assertEquals(1, packageInfo.getSubPackages().size())
            );
        }

        @Test
        @DisplayName("addSubPackage() should handle null gracefully")
        void addSubPackageShouldHandleNull() {
            // Act
            boolean result = packageInfo.addSubPackage(null);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("removeSubPackage() should remove sub-package")
        void removeSubPackageShouldRemoveSubPackage() {
            // Arrange
            PackageInfo sub = new PackageInfo("com.test.utils", project);
            packageInfo.addSubPackage(sub);

            // Act
            boolean result = packageInfo.removeSubPackage(sub);

            // Assert
            assertAll("Sub-package should be removed",
                () -> assertTrue(result),
                () -> assertFalse(packageInfo.hasSubPackage("com.test.utils")),
                () -> assertTrue(packageInfo.getSubPackages().isEmpty())
            );
        }

        @Test
        @DisplayName("getSubPackage() should return sub-package by name")
        void getSubPackageShouldReturnSubPackageByName() {
            // Arrange
            PackageInfo sub = new PackageInfo("com.test.utils", project);
            packageInfo.addSubPackage(sub);

            // Act
            PackageInfo result = packageInfo.getSubPackage("com.test.utils");

            // Assert
            assertSame(sub, result);
        }

        @Test
        @DisplayName("getSubPackage() should return null for non-existent")
        void getSubPackageShouldReturnNullForNonExistent() {
            // Act
            PackageInfo result = packageInfo.getSubPackage("nonexistent");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("hasSubPackages() should return true when has sub-packages")
        void hasSubPackagesShouldReturnTrueWhenHasSubPackages() {
            // Arrange
            packageInfo.addSubPackage(new PackageInfo("com.test.utils", project));

            // Assert
            assertTrue(packageInfo.hasSubPackages());
        }

        @Test
        @DisplayName("hasSubPackages() should return false when empty")
        void hasSubPackagesShouldReturnFalseWhenEmpty() {
            // Assert
            assertFalse(packageInfo.hasSubPackages());
        }
    }

    // ========================================
    // CLASS MANAGEMENT TESTS
    // ========================================

    @Nested
    @DisplayName("Class Management Tests")
    @Order(6)
    class ClassManagementTests {

        @Test
        @DisplayName("getClasses() should return unmodifiable list")
        void getClassesShouldReturnUnmodifiableList() {
            // Act
            List<ClassInfo> classes = packageInfo.getClasses();

            // Assert - Should throw when trying to modify
            assertThrows(UnsupportedOperationException.class,
                () -> classes.add(null));
        }

        @Test
        @DisplayName("addClass() should add class successfully")
        void addClassShouldAddClassSuccessfully() {
            // Arrange
            TypeDeclaration node = createMockTypeDeclaration("TestClass");
            ClassInfo classInfo = new ClassInfo(node, packageInfo);

            // Act
            boolean result = packageInfo.addClass(classInfo);

            // Assert
            assertAll("Class should be added",
                () -> assertTrue(result),
                () -> assertEquals(1, packageInfo.getClasses().size()),
                () -> assertTrue(packageInfo.hasClass("TestClass"))
            );
        }

        @Test
        @DisplayName("addClass() should not add duplicate class")
        void addClassShouldNotAddDuplicate() {
            // Arrange
            TypeDeclaration node = createMockTypeDeclaration("TestClass");
            ClassInfo class1 = new ClassInfo(node, packageInfo);
            ClassInfo class2 = new ClassInfo(node, packageInfo);
            packageInfo.addClass(class1);

            // Act
            boolean result = packageInfo.addClass(class2);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("removeClass() should remove class")
        void removeClassShouldRemoveClass() {
            // Arrange
            TypeDeclaration node = createMockTypeDeclaration("TestClass");
            ClassInfo classInfo = new ClassInfo(node, packageInfo);
            packageInfo.addClass(classInfo);

            // Act
            boolean result = packageInfo.removeClass(classInfo);

            // Assert
            assertAll("Class should be removed",
                () -> assertTrue(result),
                () -> assertTrue(packageInfo.getClasses().isEmpty()),
                () -> assertFalse(packageInfo.hasClass("TestClass"))
            );
        }

        @Test
        @DisplayName("getClass() should return class by name")
        void getClassShouldReturnClassByName() {
            // Arrange
            TypeDeclaration node = createMockTypeDeclaration("TestClass");
            ClassInfo classInfo = new ClassInfo(node, packageInfo);
            packageInfo.addClass(classInfo);

            // Act
            ClassInfo result = packageInfo.getClass("TestClass");

            // Assert
            assertSame(classInfo, result);
        }

        @Test
        @DisplayName("getClass() should return null for non-existent class")
        void getClassShouldReturnNullForNonExistent() {
            // Act
            ClassInfo result = packageInfo.getClass("NonExistent");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("hasClasses() should return true when has classes")
        void hasClassesShouldReturnTrueWhenHasClasses() {
            // Arrange
            TypeDeclaration node = createMockTypeDeclaration("TestClass");
            packageInfo.addClass(new ClassInfo(node, packageInfo));

            // Assert
            assertTrue(packageInfo.hasClasses());
        }

        @Test
        @DisplayName("hasClasses() should return false when empty")
        void hasClassesShouldReturnFalseWhenEmpty() {
            // Assert
            assertFalse(packageInfo.hasClasses());
        }

        @Test
        @DisplayName("getAllClasses() should aggregate classes from hierarchy")
        void getAllClassesShouldAggregateFromHierarchy() {
            // Arrange
            TypeDeclaration node1 = createMockTypeDeclaration("Class1");
            packageInfo.addClass(new ClassInfo(node1, packageInfo));

            PackageInfo sub = new PackageInfo("com.test.utils", project);
            TypeDeclaration node2 = createMockTypeDeclaration("Class2");
            sub.addClass(new ClassInfo(node2, sub));
            packageInfo.addSubPackage(sub);

            // Act
            List<ClassInfo> allClasses = packageInfo.getAllClasses();

            // Assert
            assertEquals(2, allClasses.size());
        }
    }

    // ========================================
    // COMPILATION UNIT TESTS
    // ========================================

    @Nested
    @DisplayName("Compilation Unit Tests")
    @Order(7)
    class CompilationUnitTests {

        @Test
        @DisplayName("addUnit() should add compilation unit")
        void addUnitShouldAddCompilationUnit() {
            // Arrange
            CompilationUnit cu = createMockCompilationUnit();

            // Act
            boolean result = packageInfo.addUnit("Test.java", cu);

            // Assert
            assertAll("Unit should be added",
                () -> assertTrue(result),
                () -> assertTrue(packageInfo.hasUnit("Test.java")),
                () -> assertEquals(1, packageInfo.getUnits().size())
            );
        }

        @Test
        @DisplayName("addUnit() should reject null or blank name")
        void addUnitShouldRejectInvalidName() {
            // Arrange
            CompilationUnit cu = createMockCompilationUnit();

            // Act & Assert
            assertAll("Invalid names should be rejected",
                () -> assertFalse(packageInfo.addUnit(null, cu)),
                () -> assertFalse(packageInfo.addUnit("", cu)),
                () -> assertFalse(packageInfo.addUnit("   ", cu))
            );
        }

        @Test
        @DisplayName("removeUnit() should remove compilation unit")
        void removeUnitShouldRemoveCompilationUnit() {
            // Arrange
            CompilationUnit cu = createMockCompilationUnit();
            packageInfo.addUnit("Test.java", cu);

            // Act
            boolean result = packageInfo.removeUnit("Test.java");

            // Assert
            assertAll("Unit should be removed",
                () -> assertTrue(result),
                () -> assertFalse(packageInfo.hasUnit("Test.java"))
            );
        }

        @Test
        @DisplayName("getUnit() should return unit by name")
        void getUnitShouldReturnUnitByName() {
            // Arrange
            CompilationUnit cu = createMockCompilationUnit();
            packageInfo.addUnit("Test.java", cu);

            // Act
            CompilationUnit result = packageInfo.getUnit("Test.java");

            // Assert
            assertSame(cu, result);
        }

        @Test
        @DisplayName("getAllUnits() should aggregate units from hierarchy")
        void getAllUnitsShouldAggregateFromHierarchy() {
            // Arrange
            packageInfo.addUnit("Test1.java", createMockCompilationUnit());

            PackageInfo sub = new PackageInfo("com.test.utils", project);
            sub.addUnit("Test2.java", createMockCompilationUnit());
            packageInfo.addSubPackage(sub);

            // Act
            List<CompilationUnit> allUnits = packageInfo.getAllUnits();

            // Assert
            assertEquals(2, allUnits.size());
        }

        @Test
        @DisplayName("hasUnits() should return true only when has valid units")
        void hasUnitsShouldReturnTrueOnlyWhenHasValidUnits() {
            // Assert - Initially false
            assertFalse(packageInfo.hasUnits());

            // Arrange - Add null unit
            packageInfo.addUnit("null.java", null);
            assertFalse(packageInfo.hasUnits(), 
                "Should return false when only null units");

            // Arrange - Add valid unit
            packageInfo.addUnit("valid.java", createMockCompilationUnit());
            assertTrue(packageInfo.hasUnits(),
                "Should return true when has valid unit");
        }
    }

    // ========================================
    // EQUALITY AND HASHCODE TESTS
    // ========================================

    @Nested
    @DisplayName("Equality and HashCode Tests")
    @Order(8)
    class EqualityTests {

        @Test
        @DisplayName("equals() should return true for same instance")
        void equalsShouldReturnTrueForSameInstance() {
            // Assert
            assertEquals(packageInfo, packageInfo);
        }

        @Test
        @DisplayName("equals() should return true for same name and structure")
        void equalsShouldReturnTrueForSameNameAndStructure() {
            // Arrange
            PackageInfo other = new PackageInfo("com.test", project);

            // Assert
            assertEquals(packageInfo, other);
        }

        @Test
        @DisplayName("equals() should return false for different names")
        void equalsShouldReturnFalseForDifferentNames() {
            // Arrange
            PackageInfo other = new PackageInfo("org.example", project);

            // Assert
            assertNotEquals(packageInfo, other);
        }

        @Test
        @DisplayName("hashCode() should be consistent with equals()")
        void hashCodeShouldBeConsistentWithEquals() {
            // Arrange
            PackageInfo other = new PackageInfo("com.test", project);

            // Assert
            assertEquals(packageInfo.hashCode(), other.hashCode());
        }
    }

    // ========================================
    // TOSTRING TESTS
    // ========================================

    @Nested
    @DisplayName("toString Tests")
    @Order(9)
    class ToStringTests {

        @Test
        @DisplayName("toString() should contain package information")
        void toStringShouldContainPackageInformation() {
            // Act
            String result = packageInfo.toString();

            // Assert
            assertAll("toString content",
                () -> assertTrue(result.contains("PackageInfo")),
                () -> assertTrue(result.contains("com.test")),
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty())
            );
        }
    }
}