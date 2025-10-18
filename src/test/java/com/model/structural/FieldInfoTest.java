package com.model.structural;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.utils.NodeVisibility;

/**
 * Comprehensive test suite for FieldInfo class.
 * 
 * <p>Tests field properties, type handling, modifiers, and constant detection.</p>
 * 
 * @author Luna
 * @version 1.0
 */
@DisplayName("FieldInfo - Comprehensive Unit Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FieldInfoTest {

	private ProjectInfo testProject;
    private PackageInfo testPackage;
    private ClassInfo testClass;
    
    /**
     * Creates a minimal CompilationUnit with proper AST structure.
     */
    private CompilationUnit createMockCompilationUnit() {
        AST ast = AST.newAST(AST.JLS21, false);
        CompilationUnit cu = ast.newCompilationUnit();
        return cu;
    }

    /**
     * Creates a TypeDeclaration for ClassInfo.
     */
    private TypeDeclaration createMockTypeDeclaration(String name) {
        CompilationUnit cu = createMockCompilationUnit();
        AST ast = cu.getAST();
        
        TypeDeclaration typeDecl = ast.newTypeDeclaration();
        typeDecl.setName(ast.newSimpleName(name));
        cu.types().add(typeDecl);
        
        return typeDecl;
    }

    /**
     * Creates a VariableDeclarationFragment for FieldInfo.
     */
    private VariableDeclarationFragment createMockFieldFragment(String name) {
        CompilationUnit cu = createMockCompilationUnit();
        AST ast = cu.getAST();
        
        TypeDeclaration typeDecl = createMockTypeDeclaration("TestClass");
        FieldDeclaration fieldDecl = ast.newFieldDeclaration(
            ast.newVariableDeclarationFragment()
        );
        
        VariableDeclarationFragment fragment = 
            (VariableDeclarationFragment) fieldDecl.fragments().get(0);
        fragment.setName(ast.newSimpleName(name));
        
        typeDecl.bodyDeclarations().add(fieldDecl);
        return fragment;
    }

    @BeforeEach
    void setUp() {
    	testProject = new ProjectInfo("Test");
        testPackage = new PackageInfo("com.test",testProject);
        TypeDeclaration typeNode = createMockTypeDeclaration("TestClass");
        testClass = new ClassInfo(typeNode, testPackage);
    }

    @AfterEach
    void tearDown() {
        testPackage = null;
        testClass = null;
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Nested
    @DisplayName("Constructor Tests")
    @Order(1)
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize field with all parameters")
        void constructorShouldInitializeField() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("testField");
            String type = "String";

            // Act
            FieldInfo field = new FieldInfo(node, testClass, type);

            // Assert
            assertAll("Field initialization",
                () -> assertEquals("testField", field.getName()),
                () -> assertEquals("String", field.getType()),
                () -> assertSame(testClass, field.getParentClass()),
                () -> assertNotNull(field.getNode())
            );
        }

        @Test
        @DisplayName("Constructor should throw NPE for null node")
        void constructorShouldRejectNullNode() {
            // Act & Assert
            assertThrows(NullPointerException.class, 
                () -> new FieldInfo(null, testClass, "String"),
                "Should reject null node");
        }

        @Test
        @DisplayName("Constructor should throw NPE for null parent class")
        void constructorShouldRejectNullParentClass() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");

            // Act & Assert
            assertThrows(NullPointerException.class, 
                () -> new FieldInfo(node, null, "String"),
                "Should reject null parent class");
        }

        @Test
        @DisplayName("Constructor should throw NPE for null type")
        void constructorShouldRejectNullType() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");

            // Act & Assert
            assertThrows(NullPointerException.class, 
                () -> new FieldInfo(node, testClass, null),
                "Should reject null type");
        }
    }

    // ========================================
    // GETTER TESTS
    // ========================================

    @Nested
    @DisplayName("Getter Tests")
    @Order(2)
    class GetterTests {

        @Test
        @DisplayName("getName() should return field name")
        void getNameShouldReturnFieldName() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("userName");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act & Assert
            assertEquals("userName", field.getName());
        }

        @Test
        @DisplayName("getType() should return field type")
        void getTypeShouldReturnFieldType() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("count");
            FieldInfo field = new FieldInfo(node, testClass, "int");

            // Act & Assert
            assertEquals("int", field.getType());
        }

        @Test
        @DisplayName("getType() should handle complex types")
        void getTypeShouldHandleComplexTypes() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("items");
            FieldInfo field = new FieldInfo(node, testClass, "List<String>");

            // Act & Assert
            assertEquals("List<String>", field.getType());
        }

        @Test
        @DisplayName("getParentClass() should return parent class")
        void getParentClassShouldReturnParentClass() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "Object");

            // Act & Assert
            assertSame(testClass, field.getParentClass());
        }

        @Test
        @DisplayName("getNode() should return AST node")
        void getNodeShouldReturnASTNode() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act & Assert
            assertSame(node, field.getNode());
        }
    }

    // ========================================
    // MODIFIER TESTS
    // ========================================

    @Nested
    @DisplayName("Modifier Tests")
    @Order(3)
    class ModifierTests {

        @Test
        @DisplayName("isConstant() should return true for static final fields")
        void isConstantShouldReturnTrueForStaticFinal() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("MAX_SIZE");
            FieldInfo field = new FieldInfo(node, testClass, "int");
            
            int modifiers = Modifier.STATIC | Modifier.FINAL;
            field.setModifiers(modifiers);

            // Act & Assert
            assertTrue(field.isConstant(), 
                "Static final field should be constant");
        }

        @Test
        @DisplayName("isConstant() should return false for non-static final fields")
        void isConstantShouldReturnFalseForInstanceFinal() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("id");
            FieldInfo field = new FieldInfo(node, testClass, "String");
            
            int modifiers = Modifier.FINAL;
            field.setModifiers(modifiers);

            // Act & Assert
            assertFalse(field.isConstant(), 
                "Non-static final field should not be constant");
        }

        @Test
        @DisplayName("isConstant() should return false for static non-final fields")
        void isConstantShouldReturnFalseForStaticMutable() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("counter");
            FieldInfo field = new FieldInfo(node, testClass, "int");
            
            int modifiers = Modifier.STATIC;
            field.setModifiers(modifiers);

            // Act & Assert
            assertFalse(field.isConstant(), 
                "Static non-final field should not be constant");
        }

        @Test
        @DisplayName("isStatic() should detect static modifier")
        void isStaticShouldDetectStaticModifier() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("instance");
            FieldInfo field = new FieldInfo(node, testClass, "TestClass");
            
            int modifiers = Modifier.STATIC | Modifier.PRIVATE;
            field.setModifiers(modifiers);

            // Act & Assert
            assertTrue(field.isStatic());
        }

        @Test
        @DisplayName("isFinal() should detect final modifier")
        void isFinalShouldDetectFinalModifier() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("name");
            FieldInfo field = new FieldInfo(node, testClass, "String");
            
            int modifiers = Modifier.PRIVATE | Modifier.FINAL;
            field.setModifiers(modifiers);

            // Act & Assert
            assertTrue(field.isFinal());
        }

        @Test
        @DisplayName("Field should support all standard modifiers")
        void fieldShouldSupportAllStandardModifiers() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("value");
            FieldInfo field = new FieldInfo(node, testClass, "int");

            // Act - Test different modifier combinations
            assertAll("Modifier combinations",
                () -> {
                    field.setModifiers(Modifier.PUBLIC);
                    assertTrue(field.isPublic());
                },
                () -> {
                    field.setModifiers(Modifier.PRIVATE);
                    assertTrue(field.isPrivate());
                },
                () -> {
                    field.setModifiers(Modifier.PROTECTED);
                    assertTrue(field.isProtected());
                },
                () -> {
                    field.setModifiers(0); // Package-private
                    assertTrue(field.isDefault());
                }
            );
        }
    }

    // ========================================
    // SIGNATURE TESTS
    // ========================================

    @Nested
    @DisplayName("Signature Tests")
    @Order(4)
    class SignatureTests {

        @Test
        @DisplayName("getSignature() should return 'type name' format")
        void getSignatureShouldReturnTypeAndName() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("userName");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act
            String signature = field.getSignature();

            // Assert
            assertEquals("String userName", signature);
        }

        @Test
        @DisplayName("getSignature() should handle primitive types")
        void getSignatureShouldHandlePrimitiveTypes() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("count");
            FieldInfo field = new FieldInfo(node, testClass, "int");

            // Act & Assert
            assertEquals("int count", field.getSignature());
        }

        @Test
        @DisplayName("getSignature() should handle generic types")
        void getSignatureShouldHandleGenericTypes() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("items");
            FieldInfo field = new FieldInfo(node, testClass, "List<Item>");

            // Act & Assert
            assertEquals("List<Item> items", field.getSignature());
        }

        @Test
        @DisplayName("getShortSignature() should return only name")
        void getShortSignatureShouldReturnOnlyName() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("data");
            FieldInfo field = new FieldInfo(node, testClass, "byte[]");

            // Act
            String shortSig = field.getShortSignature();

            // Assert
            assertEquals("data", shortSig);
        }

        @Test
        @DisplayName("getFullSignature() should include visibility and modifiers")
        void getFullSignatureShouldIncludeVisibilityAndModifiers() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("CONSTANT");
            FieldInfo field = new FieldInfo(node, testClass, "String");
            
            field.setVisibility(NodeVisibility.PUBLIC);
            field.setModifiers(Modifier.STATIC | Modifier.FINAL);

            // Act
            String fullSig = field.getFullSignature();

            // Assert
            assertAll("Full signature components",
                () -> assertTrue(fullSig.contains("PUBLIC") || fullSig.contains("public")),
                () -> assertTrue(fullSig.contains("String")),
                () -> assertTrue(fullSig.contains("CONSTANT"))
            );
        }
    }

    // ========================================
    // EQUALITY TESTS
    // ========================================

    @Nested
    @DisplayName("Equality and HashCode Tests")
    @Order(5)
    class EqualityTests {

        @Test
        @DisplayName("equals() should return true for same signature")
        void equalsShouldReturnTrueForSameSignature() {
            // Arrange
            VariableDeclarationFragment node1 = createMockFieldFragment("field");
            VariableDeclarationFragment node2 = createMockFieldFragment("field");
            
            FieldInfo field1 = new FieldInfo(node1, testClass, "String");
            FieldInfo field2 = new FieldInfo(node2, testClass, "String");

            // Act & Assert
            assertEquals(field1, field2, 
                "Fields with same signature should be equal");
        }

        @Test
        @DisplayName("equals() should return false for different names")
        void equalsShouldReturnFalseForDifferentNames() {
            // Arrange
            VariableDeclarationFragment node1 = createMockFieldFragment("field1");
            VariableDeclarationFragment node2 = createMockFieldFragment("field2");
            
            FieldInfo field1 = new FieldInfo(node1, testClass, "String");
            FieldInfo field2 = new FieldInfo(node2, testClass, "String");

            // Act & Assert
            assertNotEquals(field1, field2, 
                "Fields with different names should not be equal");
        }

        @Test
        @DisplayName("equals() should return false for different types")
        void equalsShouldReturnFalseForDifferentTypes() {
            // Arrange
            VariableDeclarationFragment node1 = createMockFieldFragment("value");
            VariableDeclarationFragment node2 = createMockFieldFragment("value");
            
            FieldInfo field1 = new FieldInfo(node1, testClass, "int");
            FieldInfo field2 = new FieldInfo(node2, testClass, "String");

            // Act & Assert
            assertNotEquals(field1, field2, 
                "Fields with different types should not be equal");
        }

        @Test
        @DisplayName("equals() should return true for same instance")
        void equalsShouldReturnTrueForSameInstance() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "Object");

            // Act & Assert
            assertEquals(field, field, "Field should equal itself");
        }

        @Test
        @DisplayName("equals() should return false for null")
        void equalsShouldReturnFalseForNull() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act & Assert
            assertNotEquals(field, null, "Field should not equal null");
        }

        @Test
        @DisplayName("hashCode() should be consistent with equals()")
        void hashCodeShouldBeConsistentWithEquals() {
            // Arrange
            VariableDeclarationFragment node1 = createMockFieldFragment("field");
            VariableDeclarationFragment node2 = createMockFieldFragment("field");
            
            FieldInfo field1 = new FieldInfo(node1, testClass, "String");
            FieldInfo field2 = new FieldInfo(node2, testClass, "String");

            // Act & Assert
            assertEquals(field1.hashCode(), field2.hashCode(), 
                "Equal fields should have equal hash codes");
        }

        @Test
        @DisplayName("hashCode() should be stable")
        void hashCodeShouldBeStable() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act
            int hash1 = field.hashCode();
            int hash2 = field.hashCode();

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
    @Order(6)
    class ToStringTests {

        @Test
        @DisplayName("toString() should contain class name")
        void toStringShouldContainClassName() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act
            String result = field.toString();

            // Assert
            assertTrue(result.contains("FieldInfo"), 
                "toString should contain class name");
        }

        @Test
        @DisplayName("toString() should contain field name")
        void toStringShouldContainFieldName() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("userName");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act
            String result = field.toString();

            // Assert
            assertTrue(result.contains("userName"), 
                "toString should contain field name");
        }

        @Test
        @DisplayName("toString() should contain type")
        void toStringShouldContainType() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("count");
            FieldInfo field = new FieldInfo(node, testClass, "int");

            // Act
            String result = field.toString();

            // Assert
            assertTrue(result.contains("int"), 
                "toString should contain type");
        }

        @Test
        @DisplayName("toString() should not be null or empty")
        void toStringShouldNotBeNullOrEmpty() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "Object");

            // Act
            String result = field.toString();

            // Assert
            assertAll("toString output validation",
                () -> assertNotNull(result, "toString should not return null"),
                () -> assertFalse(result.isEmpty(), "toString should not be empty")
            );
        }
    }

    // ========================================
    // EDGE CASE TESTS
    // ========================================

    @Nested
    @DisplayName("Edge Case Tests")
    @Order(7)
    class EdgeCaseTests {

        @Test
        @DisplayName("Field should handle single-character names")
        void fieldShouldHandleSingleCharacterNames() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("x");
            
            // Act & Assert
            assertDoesNotThrow(() -> {
                FieldInfo field = new FieldInfo(node, testClass, "int");
                assertEquals("x", field.getName());
            });
        }

        @Test
        @DisplayName("Field should handle array types")
        void fieldShouldHandleArrayTypes() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("data");
            FieldInfo field = new FieldInfo(node, testClass, "byte[]");

            // Act & Assert
            assertEquals("byte[]", field.getType());
        }

        @Test
        @DisplayName("Field should handle multi-dimensional arrays")
        void fieldShouldHandleMultiDimensionalArrays() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("matrix");
            FieldInfo field = new FieldInfo(node, testClass, "int[][]");

            // Act & Assert
            assertEquals("int[][]", field.getType());
        }

        @Test
        @DisplayName("Field should handle wildcard generic types")
        void fieldShouldHandleWildcardGenerics() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("items");
            FieldInfo field = new FieldInfo(node, testClass, "List<?>");

            // Act & Assert
            assertEquals("List<?>", field.getType());
        }

        @Test
        @DisplayName("Field with no modifiers should have default visibility")
        void fieldWithNoModifiersShouldHaveDefaultVisibility() {
            // Arrange
            VariableDeclarationFragment node = createMockFieldFragment("field");
            FieldInfo field = new FieldInfo(node, testClass, "String");

            // Act & Assert
            assertEquals(NodeVisibility.PACKAGE, field.getVisibility(), 
                "Default visibility should be PACKAGE");
        }
    }
}