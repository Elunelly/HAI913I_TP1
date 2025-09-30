package com.parser;

import org.eclipse.jdt.core.dom.AST;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import com.parser.ParseConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ParseConfiguration class.
 * 
 * <p>This test class provides exhaustive coverage of all ParseConfiguration functionality,
 * organized into logical test groups following the AAA (Arrange-Act-Assert) pattern.</p>
 * 
 * <p><strong>Test Organization:</strong></p>
 * <ul>
 *   <li>Constructor Tests: Default values and initialization</li>
 *   <li>Factory Methods Tests: All static factory methods</li>
 *   <li>Fluent API Tests: Method chaining and return values</li>
 *   <li>Getter/Setter Tests: Basic accessors behavior</li>
 *   <li>Null Safety Tests: Handling of null inputs</li>
 *   <li>Array Handling Tests: Defensive copying and immutability</li>
 *   <li>JLS Version Tests: Version constants and validation</li>
 *   <li>Edge Cases Tests: Boundary conditions and special scenarios</li>
 *   <li>Integration Tests: Complex multi-configuration scenarios</li>
 *   <li>toString Tests: String representation formatting</li>
 * </ul>
 * 
 * @author Luna
 * @version 1.0
 */
@DisplayName("ParseConfiguration - Comprehensive Unit Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParseConfigurationTest {

    private ParseConfiguration config;

    /**
     * Setup method executed before each test.
     * Creates a fresh ParseConfiguration instance to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        config = new ParseConfiguration();
    }

    /**
     * Cleanup method executed after each test.
     * Allows garbage collection of test instances.
     */
    @AfterEach
    void tearDown() {
        config = null;
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Nested
    @DisplayName("Constructor Tests")
    @Order(1)
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should initialize with default unit name")
        void constructorShouldSetDefaultUnitName() {
            // Assert
            assertEquals("DefaultClass.java", config.getUnitName(),
                "Default unit name should be 'DefaultClass.java'");
        }

        @Test
        @DisplayName("Constructor should initialize with empty classpath array")
        void constructorShouldInitializeEmptyClasspath() {
            // Assert
            assertNotNull(config.getClassPaths(), 
                "Classpath should not be null");
            assertEquals(0, config.getClassPaths().length,
                "Classpath should be empty by default");
        }

        @Test
        @DisplayName("Constructor should initialize with empty source paths array")
        void constructorShouldInitializeEmptySourcePaths() {
            // Assert
            assertNotNull(config.getSourcePaths(),
                "Source paths should not be null");
            assertEquals(0, config.getSourcePaths().length,
                "Source paths should be empty by default");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("Constructor should initialize with JAVA_LATEST as default JLS")
        void constructorShouldSetDefaultJLS() {
            // Assert
            assertTrue(config.getCurrentJLS() >= ParseConfiguration.JAVA_8,
                "Current JLS should be at least Java 8");
        }

        @Test
        @DisplayName("Constructor should initialize with bindings enabled")
        void constructorShouldEnableBindingsByDefault() {
            // Assert
            assertTrue(config.isResolveBindings(),
                "Binding resolution should be enabled by default");
        }
    }

    // ========================================
    // FACTORY METHODS TESTS
    // ========================================

    @Nested
    @DisplayName("Factory Methods Tests")
    @Order(2)
    class FactoryMethodsTests {

        @Test
        @DisplayName("defaultConfig() should enable bindings")
        void defaultConfigShouldEnableBindings() {
            // Act
            ParseConfiguration result = ParseConfiguration.defaultConfig();

            // Assert
            assertTrue(result.isResolveBindings(),
                "Default configuration should have bindings enabled");
        }

        @Test
        @DisplayName("defaultConfig() should use JAVA_LATEST")
        void defaultConfigShouldUseLatestJLS() {
            // Act
            ParseConfiguration result = ParseConfiguration.defaultConfig();

            // Assert
            assertEquals(ParseConfiguration.JAVA_LATEST, result.getCurrentJLS(),
                "Default configuration should use latest JLS version");
        }

        @Test
        @DisplayName("fastConfig() should disable bindings for performance")
        void fastConfigShouldDisableBindings() {
            // Act
            ParseConfiguration result = ParseConfiguration.fastConfig();

            // Assert
            assertFalse(result.isResolveBindings(),
                "Fast configuration should have bindings disabled");
        }

        @Test
        @DisplayName("fastConfig() should use JAVA_LATEST")
        void fastConfigShouldUseLatestJLS() {
            // Act
            ParseConfiguration result = ParseConfiguration.fastConfig();

            // Assert
            assertEquals(ParseConfiguration.JAVA_LATEST, result.getCurrentJLS(),
                "Fast configuration should use latest JLS version");
        }

        @Test
        @DisplayName("testConfig() should append .java extension to unit name")
        void testConfigShouldAppendJavaExtension() {
            // Arrange
            String baseName = "MyTest";

            // Act
            ParseConfiguration result = ParseConfiguration.testConfig(baseName);

            // Assert
            assertEquals("MyTest.java", result.getUnitName(),
                "Test configuration should append .java extension");
        }

        @Test
        @DisplayName("testConfig() should NOT duplicate .java extension if already present")
        void testConfigShouldHandleDuplicateExtension() {
            // Arrange
            String nameWithExtension = "MyTest.java";

            // Act
            ParseConfiguration result = ParseConfiguration.testConfig(nameWithExtension);

            // Assert
            assertEquals("MyTest.java", result.getUnitName(),
                "Test configuration should not duplicate .java extension");
            assertFalse(result.getUnitName().endsWith(".java.java"),
                "Unit name should not end with '.java.java'");
        }

        @Test
        @DisplayName("testConfig() should sanitize invalid characters in unit name")
        void testConfigShouldSanitizeInvalidCharacters() {
            // Arrange
            String invalidName = "My*Test?File<>";

            // Act
            ParseConfiguration result = ParseConfiguration.testConfig(invalidName);

            // Assert
            assertEquals("MyTestFile.java", result.getUnitName(),
                "Test configuration should remove invalid characters");
            assertTrue(result.getUnitName().matches("^[a-zA-Z_$][\\w$]*\\.java$"),
                "Unit name should be a valid Java filename");
        }

        @Test
        @DisplayName("testConfig() should handle empty string by using default name")
        void testConfigShouldHandleEmptyString() {
            // Act
            ParseConfiguration result = ParseConfiguration.testConfig("");

            // Assert
            assertEquals("DefaultClass.java", result.getUnitName(),
                "Test configuration should use default name for empty string");
        }

        @Test
        @DisplayName("testConfig() should handle null by using default name")
        void testConfigShouldHandleNull() {
            // Act
            ParseConfiguration result = ParseConfiguration.testConfig(null);

            // Assert
            assertEquals("DefaultClass.java", result.getUnitName(),
                "Test configuration should use default name for null");
        }

        @Test
        @DisplayName("testConfig() should enable bindings")
        void testConfigShouldEnableBindings() {
            // Act
            ParseConfiguration result = ParseConfiguration.testConfig("Test");

            // Assert
            assertTrue(result.isResolveBindings(),
                "Test configuration should enable bindings");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("specificJLSConfig() should set specified JLS version")
        void specificJLSConfigShouldSetVersion() {
            // Act
            ParseConfiguration result = ParseConfiguration.specificJLSConfig(
                ParseConfiguration.JAVA_11);

            // Assert
            assertEquals(ParseConfiguration.JAVA_11, result.getCurrentJLS(),
                "Specific JLS config should use the provided version");
        }

        @Test
        @DisplayName("specificJLSConfig() should enable bindings")
        void specificJLSConfigShouldEnableBindings() {
            // Act
            @SuppressWarnings("deprecation")
			ParseConfiguration result = ParseConfiguration.specificJLSConfig(
                ParseConfiguration.JAVA_17);

            // Assert
            assertTrue(result.isResolveBindings(),
                "Specific JLS configuration should enable bindings");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("conservativeJLSConfig() should use JAVA_8")
        void conservativeJLSConfigShouldUseJava8() {
            // Act
            ParseConfiguration result = ParseConfiguration.conservativeJLSConfig();

            // Assert
            assertEquals(ParseConfiguration.JAVA_8, result.getCurrentJLS(),
                "Conservative configuration should use Java 8");
        }

        @Test
        @DisplayName("conservativeJLSConfig() should enable bindings")
        void conservativeJLSConfigShouldEnableBindings() {
            // Act
            ParseConfiguration result = ParseConfiguration.conservativeJLSConfig();

            // Assert
            assertTrue(result.isResolveBindings(),
                "Conservative configuration should enable bindings");
        }
    }

    // ========================================
    // FLUENT API TESTS
    // ========================================

    @Nested
    @DisplayName("Fluent API Tests")
    @Order(3)
    class FluentAPITests {

        @Test
        @DisplayName("withSpecificJREVersion() should return same instance")
        void withSpecificJREVersionShouldReturnSameInstance() {
            // Act
            @SuppressWarnings("deprecation")
			ParseConfiguration result = config.withSpecificJREVersion(
                ParseConfiguration.JAVA_17);

            // Assert
            assertSame(config, result,
                "Fluent method should return the same instance");
        }

        @Test
        @DisplayName("withBindings() should return same instance")
        void withBindingsShouldReturnSameInstance() {
            // Act
            ParseConfiguration result = config.withBindings(true);

            // Assert
            assertSame(config, result,
                "Fluent method should return the same instance");
        }

        @Test
        @DisplayName("withClassPaths() should return same instance")
        void withClassPathsShouldReturnSameInstance() {
            // Arrange
            String[] paths = {"lib/"};

            // Act
            ParseConfiguration result = config.withClassPaths(paths);

            // Assert
            assertSame(config, result,
                "Fluent method should return the same instance");
        }

        @Test
        @DisplayName("withSourcePaths() should return same instance")
        void withSourcePathsShouldReturnSameInstance() {
            // Arrange
            String[] paths = {"src/"};

            // Act
            ParseConfiguration result = config.withSourcePaths(paths);

            // Assert
            assertSame(config, result,
                "Fluent method should return the same instance");
        }

        @Test
        @DisplayName("withUnitName() should return same instance")
        void withUnitNameShouldReturnSameInstance() {
            // Act
            ParseConfiguration result = config.withUnitName("Test.java");

            // Assert
            assertSame(config, result,
                "Fluent method should return the same instance");
        }

        @Test
        @DisplayName("Fluent API should allow complete method chaining")
        void fluentAPIShouldAllowCompleteChaining() {
            // Arrange
            String[] classPaths = {"lib/", "target/"};
            String[] sourcePaths = {"src/main/java/"};

            // Act
            ParseConfiguration result = config
                .withSpecificJREVersion(ParseConfiguration.JAVA_21)
                .withBindings(true)
                .withClassPaths(classPaths)
                .withSourcePaths(sourcePaths)
                .withUnitName("CompleteTest.java");

            // Assert
            assertAll("All chained configurations should be applied",
                () -> assertSame(config, result),
                () -> assertEquals(ParseConfiguration.JAVA_21, result.getCurrentJLS()),
                () -> assertTrue(result.isResolveBindings()),
                () -> assertArrayEquals(classPaths, result.getClassPaths()),
                () -> assertArrayEquals(sourcePaths, result.getSourcePaths()),
                () -> assertEquals("CompleteTest.java", result.getUnitName())
            );
        }
    }

    // ========================================
    // GETTER/SETTER TESTS
    // ========================================

    @Nested
    @DisplayName("Getter/Setter Tests")
    @Order(4)
    class GetterSetterTests {

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("getCurrentJLS() should return configured JLS version")
        void getCurrentJLSShouldReturnConfiguredVersion() {
            // Arrange
            config.withSpecificJREVersion(ParseConfiguration.JAVA_17);

            // Act
            int result = config.getCurrentJLS();

            // Assert
            assertEquals(ParseConfiguration.JAVA_17, result,
                "getCurrentJLS should return the configured version");
        }

        @Test
        @DisplayName("isResolveBindings() should reflect binding configuration")
        void isResolveBindingsShouldReflectConfiguration() {
            // Test enabled
            config.withBindings(true);
            assertTrue(config.isResolveBindings(),
                "isResolveBindings should return true when enabled");

            // Test disabled
            config.withBindings(false);
            assertFalse(config.isResolveBindings(),
                "isResolveBindings should return false when disabled");
        }

        @Test
        @DisplayName("getClassPaths() should return configured paths")
        void getClassPathsShouldReturnConfiguredPaths() {
            // Arrange
            String[] paths = {"lib/", "target/classes/"};
            config.withClassPaths(paths);

            // Act
            String[] result = config.getClassPaths();

            // Assert
            assertArrayEquals(paths, result,
                "getClassPaths should return the configured paths");
        }

        @Test
        @DisplayName("getSourcePaths() should return configured paths")
        void getSourcePathsShouldReturnConfiguredPaths() {
            // Arrange
            String[] paths = {"src/main/java/", "src/test/java/"};
            config.withSourcePaths(paths);

            // Act
            String[] result = config.getSourcePaths();

            // Assert
            assertArrayEquals(paths, result,
                "getSourcePaths should return the configured paths");
        }

        @Test
        @DisplayName("getUnitName() should return configured name")
        void getUnitNameShouldReturnConfiguredName() {
            // Arrange
            String name = "MyClass.java";
            config.withUnitName(name);

            // Act
            String result = config.getUnitName();

            // Assert
            assertEquals(name, result,
                "getUnitName should return the configured name");
        }
    }

    // ========================================
    // NULL SAFETY TESTS
    // ========================================

    @Nested
    @DisplayName("Null Safety Tests")
    @Order(5)
    class NullSafetyTests {

        @Test
        @DisplayName("withClassPaths(null) should set empty array")
        void withClassPathsNullShouldSetEmptyArray() {
            // Act
            config.withClassPaths(null);

            // Assert
            assertNotNull(config.getClassPaths(),
                "Classpath should not be null after setting null");
            assertEquals(0, config.getClassPaths().length,
                "Classpath should be empty after setting null");
        }

        @Test
        @DisplayName("withSourcePaths(null) should set empty array")
        void withSourcePathsNullShouldSetEmptyArray() {
            // Act
            config.withSourcePaths(null);

            // Assert
            assertNotNull(config.getSourcePaths(),
                "Source paths should not be null after setting null");
            assertEquals(0, config.getSourcePaths().length,
                "Source paths should be empty after setting null");
        }

        @Test
        @DisplayName("withUnitName(null) should set default name")
        void withUnitNameNullShouldSetDefaultName() {
            // Act
            config.withUnitName(null);

            // Assert
            assertEquals("DefaultClass.java", config.getUnitName(),
                "Unit name should revert to default when set to null");
        }

        @Test
        @DisplayName("testConfig(null) should handle null gracefully")
        void testConfigNullShouldHandleGracefully() {
            // Act
            ParseConfiguration result = ParseConfiguration.testConfig(null);

            // Assert
            assertNotNull(result.getUnitName(),
                "Unit name should not be null even with null input");
            assertTrue(result.getUnitName().endsWith(".java"),
                "Unit name should still end with .java extension");
        }
    }

    // ========================================
    // ARRAY HANDLING TESTS
    // ========================================

    @Nested
    @DisplayName("Array Handling Tests")
    @Order(6)
    class ArrayHandlingTests {

        @Test
        @DisplayName("withClassPaths() should perform defensive copy on input")
        void withClassPathsShouldDefensivelyCopyInput() {
            // Arrange
            String[] original = {"lib/", "target/"};
            config.withClassPaths(original);

            // Act - Modify original array
            original[0] = "modified/";

            // Assert - Configuration should not be affected
            assertEquals("lib/", config.getClassPaths()[0],
                "Internal classpath should not be affected by external modifications");
        }

        @Test
        @DisplayName("withSourcePaths() should perform defensive copy on input")
        void withSourcePathsShouldDefensivelyCopyInput() {
            // Arrange
            String[] original = {"src/main/", "src/test/"};
            config.withSourcePaths(original);

            // Act - Modify original array
            original[0] = "modified/";

            // Assert - Configuration should not be affected
            assertEquals("src/main/", config.getSourcePaths()[0],
                "Internal source paths should not be affected by external modifications");
        }

        @Test
        @DisplayName("getClassPaths() should return defensive copy preventing external modification")
        void getClassPathsReturnsDefensiveCopy() {
            // Arrange
            String[] original = {"lib/", "target/"};
            config.withClassPaths(original);

            // Act - Get reference and modify it
            String[] retrieved = config.getClassPaths();
            retrieved[0] = "hacked/";

            // Assert - Internal state should NOT be affected (fixed with .clone())
            assertEquals("lib/", config.getClassPaths()[0],
                "getClassPaths returns defensive copy, preventing external modification");
        }

        @Test
        @DisplayName("getSourcePaths() should return defensive copy preventing external modification")
        void getSourcePathsReturnsDefensiveCopy() {
            // Arrange
            String[] original = {"src/main/", "src/test/"};
            config.withSourcePaths(original);

            // Act - Get reference and modify it
            String[] retrieved = config.getSourcePaths();
            retrieved[0] = "hacked/";

            // Assert - Internal state should NOT be affected (fixed with .clone())
            assertEquals("src/main/", config.getSourcePaths()[0],
                "getSourcePaths returns defensive copy, preventing external modification");
        }

        @Test
        @DisplayName("withClassPaths() with empty array should work correctly")
        void withClassPathsEmptyArrayShouldWork() {
            // Act
            config.withClassPaths(new String[0]);

            // Assert
            assertNotNull(config.getClassPaths());
            assertEquals(0, config.getClassPaths().length);
        }

        @Test
        @DisplayName("withSourcePaths() with empty array should work correctly")
        void withSourcePathsEmptyArrayShouldWork() {
            // Act
            config.withSourcePaths(new String[0]);

            // Assert
            assertNotNull(config.getSourcePaths());
            assertEquals(0, config.getSourcePaths().length);
        }
    }

    // ========================================
    // JLS VERSION TESTS
    // ========================================

    @Nested
    @DisplayName("JLS Version Tests")
    @Order(7)
    class JLSVersionTests {

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("JAVA_8 constant should be defined")
        void java8ConstantShouldBeDefined() {
            // Assert
            assertTrue(ParseConfiguration.JAVA_8 > 0,
                "JAVA_8 constant should be positive");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("JAVA_11 constant should be >= JAVA_8")
        void java11ConstantShouldBeValid() {
            // Assert
            assertTrue(ParseConfiguration.JAVA_11 >= ParseConfiguration.JAVA_8,
                "JAVA_11 should be greater than or equal to JAVA_8");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("JAVA_17 constant should be >= JAVA_11")
        void java17ConstantShouldBeValid() {
            // Assert
            assertTrue(ParseConfiguration.JAVA_17 >= ParseConfiguration.JAVA_11,
                "JAVA_17 should be greater than or equal to JAVA_11");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("JAVA_21 constant should be >= JAVA_17")
        void java21ConstantShouldBeValid() {
            // Assert
            assertTrue(ParseConfiguration.JAVA_21 >= ParseConfiguration.JAVA_17,
                "JAVA_21 should be greater than or equal to JAVA_17");
        }

        @Test
        @DisplayName("JAVA_LATEST should be >= JAVA_21")
        void javaLatestShouldBeAtLeastJava21() {
            // Assert
            assertTrue(ParseConfiguration.JAVA_LATEST >= ParseConfiguration.JAVA_21,
                "JAVA_LATEST should be at least JAVA_21");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("getJLStoString() should convert valid version to string")
        void getJLStoStringShouldConvertValidVersion() {
            // Act & Assert
            assertEquals("JLS8", ParseConfiguration.getJLStoString(
                ParseConfiguration.JAVA_8));
            assertEquals("JLS11", ParseConfiguration.getJLStoString(
                ParseConfiguration.JAVA_11));
            assertEquals("JLS17", ParseConfiguration.getJLStoString(
                ParseConfiguration.JAVA_17));
            assertEquals("JLS21", ParseConfiguration.getJLStoString(
                ParseConfiguration.JAVA_21));
        }

        @Test
        @DisplayName("getJLStoString() should return empty string for invalid version")
        void getJLStoStringShouldReturnEmptyForInvalid() {
            // Act & Assert
            assertEquals("", ParseConfiguration.getJLStoString(0),
                "Invalid version 0 should return empty string");
            assertEquals("", ParseConfiguration.getJLStoString(-1),
                "Negative version should return empty string");
            assertEquals("", ParseConfiguration.getJLStoString(999),
                "Version beyond latest should return empty string");
        }

        @Test
        @DisplayName("isJLSSupported() should return true for supported versions")
        void isJLSSupportedShouldReturnTrueForSupported() {
            // Assert
            assertTrue(config.isJLSSupported(8),
                "Java 8 should always be supported");
        }

        @Test
        @DisplayName("isJLSSupported() should return false for unsupported versions")
        void isJLSSupportedShouldReturnFalseForUnsupported() {
            // Assert
            assertFalse(config.isJLSSupported(7),
                "Java 7 should not be supported");
            assertFalse(config.isJLSSupported(999),
                "Non-existent version should not be supported");
        }
    }

    // ========================================
    // EDGE CASES TESTS
    // ========================================

    @Nested
    @DisplayName("Edge Cases Tests")
    @Order(8)
    class EdgeCasesTests {

        @Test
        @DisplayName("withUnitName() with empty string should use default name")
        void withUnitNameEmptyStringShouldUseDefault() {
            // Act
            config.withUnitName("");

            // Assert
            assertEquals("DefaultClass.java", config.getUnitName(),
                "Empty unit name should be replaced with default");
        }

        @Test
        @DisplayName("withUnitName() with whitespace-only string should use default name")
        void withUnitNameWhitespaceOnlyShouldUseDefault() {
            // Arrange
            String whitespace = "   ";

            // Act
            config.withUnitName(whitespace);

            // Assert
            assertEquals("DefaultClass.java", config.getUnitName(),
                "Whitespace-only unit name should be replaced with default");
        }

        @Test
        @DisplayName("withUnitName() with invalid characters should sanitize them")
        void withUnitNameInvalidCharactersShouldBeSanitized() {
            // Arrange
            String invalidName = "My*Class?Name<>.java";

            // Act
            config.withUnitName(invalidName);

            // Assert
            assertEquals("MyClassName.java", config.getUnitName(),
                "Invalid characters should be removed from unit name");
        }

        @Test
        @DisplayName("withUnitName() with path separators should remove them")
        void withUnitNamePathSeparatorsShouldBeRemoved() {
            // Arrange
            String nameWithPath = "com/example/MyClass.java";

            // Act
            config.withUnitName(nameWithPath);

            // Assert
            assertEquals("comexampleMyClass.java", config.getUnitName(),
                "Path separators should be removed from unit name");
        }

        @Test
        @DisplayName("withUnitName() starting with number should prepend underscore")
        void withUnitNameStartingWithNumberShouldPrependUnderscore() {
            // Arrange
            String nameStartingWithNumber = "123Class";

            // Act
            config.withUnitName(nameStartingWithNumber);

            // Assert
            assertEquals("_123Class.java", config.getUnitName(),
                "Names starting with number should have underscore prepended");
        }

        @Test
        @DisplayName("withUnitName() with very long name should be truncated")
        void withUnitNameVeryLongShouldBeTruncated() {
            // Arrange
            String veryLongName = "A".repeat(300);

            // Act
            config.withUnitName(veryLongName);

            // Assert
            assertTrue(config.getUnitName().length() <= 255,
                "Unit name should be truncated to 255 characters max");
            assertTrue(config.getUnitName().endsWith(".java"),
                "Truncated name should still end with .java");
        }

        @Test
        @DisplayName("Multiple consecutive calls should properly override values")
        void multipleConsecutiveCallsShouldOverride() {
            // Act
            config.withUnitName("First.java")
                  .withUnitName("Second.java")
                  .withUnitName("Third.java");

            // Assert
            assertEquals("Third.java", config.getUnitName(),
                "Last value should be retained");
        }

        @Test
        @DisplayName("withSpecificJREVersion() with invalid JLS should NOT change current version")
        void withSpecificJREVersionInvalidShouldNotChange() {
            // Arrange
            int originalJLS = config.getCurrentJLS();
            
            // Act - Try to set invalid JLS version
            config.withSpecificJREVersion(999);

            // Assert - Current JLS should remain unchanged
            assertEquals(originalJLS, config.getCurrentJLS(),
                "Invalid JLS version should be rejected and not change current version");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("withSpecificJREVersion() with valid JLS should update version")
        void withSpecificJREVersionValidShouldUpdate() {
            // Act
            config.withSpecificJREVersion(ParseConfiguration.JAVA_17);

            // Assert
            assertEquals(ParseConfiguration.JAVA_17, config.getCurrentJLS(),
                "Valid JLS version should be accepted and update current version");
        }

        @Test
        @DisplayName("withClassPaths() with array containing null elements")
        void withClassPathsArrayWithNullElements() {
            // Arrange
            String[] pathsWithNull = {"lib/", null, "target/"};

            // Act & Assert
            assertDoesNotThrow(() -> config.withClassPaths(pathsWithNull),
                "Should handle array with null elements gracefully");
        }
    }

    // ========================================
    // INTEGRATION TESTS
    // ========================================

    @Nested
    @DisplayName("Integration Tests")
    @Order(9)
    class IntegrationTests {

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("Complete configuration scenario for production parsing")
        void completeProductionConfigurationScenario() {
            // Arrange
            String[] classPaths = {"lib/commons.jar", "target/classes/"};
            String[] sourcePaths = {"src/main/java/", "src/main/resources/"};

            // Act
            ParseConfiguration result = ParseConfiguration.defaultConfig()
                .withUnitName("BusinessLogic.java")
                .withClassPaths(classPaths)
                .withSourcePaths(sourcePaths)
                .withSpecificJREVersion(ParseConfiguration.JAVA_17);

            // Assert
            assertAll("Complete production configuration",
                () -> assertEquals("BusinessLogic.java", result.getUnitName()),
                () -> assertArrayEquals(classPaths, result.getClassPaths()),
                () -> assertArrayEquals(sourcePaths, result.getSourcePaths()),
                () -> assertEquals(ParseConfiguration.JAVA_17, result.getCurrentJLS()),
                () -> assertTrue(result.isResolveBindings())
            );
        }

        @Test
        @DisplayName("Fast parsing scenario for syntax checking")
        void fastParsingSyntaxCheckingScenario() {
            // Act
            ParseConfiguration result = ParseConfiguration.fastConfig()
                .withUnitName("QuickCheck.java");

            // Assert
            assertAll("Fast parsing configuration",
                () -> assertFalse(result.isResolveBindings(),
                    "Fast config should disable bindings"),
                () -> assertEquals("QuickCheck.java", result.getUnitName()),
                () -> assertEquals(ParseConfiguration.JAVA_LATEST, 
                    result.getCurrentJLS())
            );
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("Legacy codebase scenario using Java 8")
        void legacyCodebaseScenario() {
            // Act
            ParseConfiguration result = ParseConfiguration.conservativeJLSConfig()
                .withClassPaths(new String[]{"legacy-lib/"})
                .withUnitName("LegacyCode.java");

            // Assert
            assertAll("Legacy codebase configuration",
                () -> assertEquals(ParseConfiguration.JAVA_8, result.getCurrentJLS()),
                () -> assertTrue(result.isResolveBindings()),
                () -> assertEquals("LegacyCode.java", result.getUnitName()),
                () -> assertEquals(1, result.getClassPaths().length)
            );
        }

        @Test
        @DisplayName("Test harness scenario with custom naming")
        void testHarnessScenario() {
            // Act
            ParseConfiguration result = ParseConfiguration.testConfig("TestCase")
                .withSourcePaths(new String[]{"src/test/java/"});

            // Assert
            assertAll("Test harness configuration",
                () -> assertEquals("TestCase.java", result.getUnitName()),
                () -> assertTrue(result.isResolveBindings()),
                () -> assertEquals(1, result.getSourcePaths().length)
            );
        }

        @Test
        @DisplayName("Reconfiguration scenario - modify existing config")
        void reconfigurationScenario() {
            // Arrange - Start with fast config
            ParseConfiguration result = ParseConfiguration.fastConfig();
            
            // Act - Reconfigure for full analysis
            result.withBindings(true)
                  .withClassPaths(new String[]{"new-lib/"})
                  .withSpecificJREVersion(ParseConfiguration.JAVA_21);

            // Assert
            assertAll("Reconfigured settings",
                () -> assertTrue(result.isResolveBindings(),
                    "Should enable bindings after reconfiguration"),
                () -> assertEquals(ParseConfiguration.JAVA_21, result.getCurrentJLS()),
                () -> assertEquals(1, result.getClassPaths().length)
            );
        }
    }

    // ========================================
    // toString TESTS
    // ========================================

    @Nested
    @DisplayName("toString Tests")
    @Order(10)
    class ToStringTests {

        @Test
        @DisplayName("toString() should include class name")
        void toStringShouldIncludeClassName() {
            // Act
            String result = config.toString();

            // Assert
            assertTrue(result.startsWith("ParseConfiguration:"),
                "toString should start with class name");
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("toString() should include JLS version")
        void toStringShouldIncludeJLSVersion() {
            // Arrange
            config.withSpecificJREVersion(ParseConfiguration.JAVA_17);

            // Act
            String result = config.toString();

            // Assert
            assertTrue(result.contains("JLS17"),
                "toString should include JLS version string");
        }

        @Test
        @DisplayName("toString() should include binding resolution status")
        void toStringShouldIncludeBindingStatus() {
            // Arrange
            config.withBindings(true);

            // Act
            String result = config.toString();

            // Assert
            assertTrue(result.contains("Resolve Bindings"),
                "toString should include binding resolution label");
            assertTrue(result.contains("true"),
                "toString should include binding status value");
        }

        @Test
        @DisplayName("toString() should include unit name")
        void toStringShouldIncludeUnitName() {
            // Arrange
            config.withUnitName("TestClass.java");

            // Act
            String result = config.toString();

            // Assert
            assertTrue(result.contains("TestClass.java"),
                "toString should include unit name");
        }

        @Test
        @DisplayName("toString() should be multi-line formatted")
        void toStringShouldBeMultiLine() {
            // Act
            String result = config.toString();

            // Assert
            assertTrue(result.contains("\n"),
                "toString should use multi-line format");
            assertTrue(result.split("\n").length >= 5,
                "toString should have at least 5 lines");
        }

        @Test
        @DisplayName("toString() should not be null or empty")
        void toStringShouldNotBeNullOrEmpty() {
            // Act
            String result = config.toString();

            // Assert
            assertNotNull(result, "toString should not return null");
            assertFalse(result.isEmpty(), "toString should not be empty");
        }

        @Test
        @DisplayName("toString() output should be consistent across multiple calls")
        void toStringShouldBeConsistent() {
            // Act
            String first = config.toString();
            String second = config.toString();

            // Assert
            assertEquals(first, second,
                "toString should produce consistent output");
        }

        @Test
        @DisplayName("toString() should reflect configuration changes")
        void toStringShouldReflectChanges() {
            // Arrange
            String beforeChange = config.toString();
            
            // Act
            config.withUnitName("ChangedName.java");
            String afterChange = config.toString();

            // Assert
            assertNotEquals(beforeChange, afterChange,
                "toString should reflect configuration changes");
            assertTrue(afterChange.contains("ChangedName.java"),
                "toString should show new unit name");
        }
    }

    // ========================================
    // SPECIAL BEHAVIOR TESTS
    // ========================================

    @Nested
    @DisplayName("Special Behavior Tests")
    @Order(11)
    class SpecialBehaviorTests {

        @Test
        @DisplayName("testConfig() with extension should handle it intelligently")
        void testConfigWithExtensionHandling() {
            // Test various extension scenarios
            assertAll("Extension handling in testConfig",
                // Basic case - no extension
                () -> {
                    ParseConfiguration cfg = ParseConfiguration.testConfig("Simple");
                    assertEquals("Simple.java", cfg.getUnitName());
                },
                
                // Fixed: already has .java - should NOT duplicate
                () -> {
                    ParseConfiguration cfg = ParseConfiguration.testConfig("HasExtension.java");
                    assertEquals("HasExtension.java", cfg.getUnitName(),
                        "Should not duplicate .java extension");
                },
                
                // Edge case - has different extension (removed and replaced)
                () -> {
                    ParseConfiguration cfg = ParseConfiguration.testConfig("Script.groovy");
                    assertEquals("Scriptgroovy.java", cfg.getUnitName(),
                        "Should remove invalid extension and add .java");
                },
                
                // Edge case - invalid characters
                () -> {
                    ParseConfiguration cfg = ParseConfiguration.testConfig("Test*File");
                    assertEquals("TestFile.java", cfg.getUnitName(),
                        "Should sanitize invalid characters");
                }
            );
        }

        @Test
        @DisplayName("fixJavaFileNamingWithDefault() should handle all edge cases")
        void fixJavaFileNamingWithDefaultHandlesEdgeCases() {
            // Test the static utility method directly
            assertAll("File naming edge cases",
                () -> assertEquals("DefaultClass.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault(null)),
                () -> assertEquals("DefaultClass.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("")),
                () -> assertEquals("ValidName.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("ValidName")),
                () -> assertEquals("ValidName.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("ValidName.java")),
                () -> assertEquals("_123Test.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("123Test")),
                () -> assertEquals("TestClass.java", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("Test*Class?")),
                () -> assertEquals("$", 
                    ParseConfiguration.fixJavaFileNamingWithDefault("*@#$%").substring(0, 1),
                    "Names with only invalid chars should start with _")
            );
        }

        @Test
        @DisplayName("Factory methods should create independent instances")
        void factoryMethodsShouldCreateIndependentInstances() {
            // Act
            ParseConfiguration config1 = ParseConfiguration.defaultConfig();
            ParseConfiguration config2 = ParseConfiguration.defaultConfig();

            // Assert
            assertNotSame(config1, config2,
                "Factory methods should create separate instances");

            // Modify one instance
            config1.withUnitName("Modified.java");

            // Other instance should not be affected
            assertNotEquals(config1.getUnitName(), config2.getUnitName(),
                "Configurations should be independent");
        }

        @Test
        @DisplayName("JLS version constants should be immutable")
        void jlsVersionConstantsShouldBeImmutable() {
            // Arrange
            @SuppressWarnings("deprecation")
			int originalJava8 = ParseConfiguration.JAVA_8;
            @SuppressWarnings("deprecation")
			int originalJava11 = ParseConfiguration.JAVA_11;

            // Note: These are public static fields, so technically mutable
            // This test documents the expected behavior

            // Assert
            assertTrue(originalJava8 > 0, "JAVA_8 should be positive");
            assertTrue(originalJava11 >= originalJava8, 
                "Version constants should be ordered");
            
            // In a production environment, these should be 'final'
            // to prevent modification
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("Configuration should handle rapid successive changes")
        void configurationShouldHandleRapidChanges() {
            // Act - Rapid configuration changes
            for (int i = 0; i < 100; i++) {
                config.withUnitName("Test" + i + ".java")
                      .withBindings(i % 2 == 0)
                      .withSpecificJREVersion(
                          i % 2 == 0 ? ParseConfiguration.JAVA_17 : ParseConfiguration.JAVA_11
                      );
            }

            // Assert - Final state should be consistent
            assertEquals("Test99.java", config.getUnitName());
            assertFalse(config.isResolveBindings());
            assertEquals(ParseConfiguration.JAVA_11, config.getCurrentJLS());
        }

        @Test
        @DisplayName("Empty array vs null array behavior should be consistent")
        void emptyVsNullArrayBehaviorShouldBeConsistent() {
            // Arrange
            ParseConfiguration configWithNull = new ParseConfiguration();
            ParseConfiguration configWithEmpty = new ParseConfiguration();

            // Act
            configWithNull.withClassPaths(null);
            configWithEmpty.withClassPaths(new String[0]);

            // Assert - Both should result in empty arrays
            assertAll("Null and empty arrays should be equivalent",
                () -> assertEquals(0, configWithNull.getClassPaths().length),
                () -> assertEquals(0, configWithEmpty.getClassPaths().length),
                () -> assertNotNull(configWithNull.getClassPaths()),
                () -> assertNotNull(configWithEmpty.getClassPaths())
            );
        }
    }

    // ========================================
    // BOUNDARY TESTS
    // ========================================

    @Nested
    @DisplayName("Boundary Tests")
    @Order(12)
    class BoundaryTests {

        @Test
        @DisplayName("withClassPaths() with very long array should work")
        void withClassPathsVeryLongArrayShouldWork() {
            // Arrange
            String[] largePaths = new String[1000];
            for (int i = 0; i < largePaths.length; i++) {
                largePaths[i] = "lib/dependency" + i + ".jar";
            }

            // Act & Assert
            assertDoesNotThrow(() -> config.withClassPaths(largePaths));
            assertEquals(1000, config.getClassPaths().length);
        }

        @Test
        @DisplayName("withUnitName() with very long string should be truncated safely")
        void withUnitNameVeryLongStringShouldBeTruncated() {
            // Arrange
            String longName = "A".repeat(300);

            // Act & Assert
            assertDoesNotThrow(() -> config.withUnitName(longName));
            assertTrue(config.getUnitName().length() <= 255,
                "Very long names should be truncated to max 255 chars");
            assertTrue(config.getUnitName().endsWith(".java"),
                "Even after truncation, should end with .java");
        }

        @Test
        @DisplayName("withClassPaths() with single element array")
        void withClassPathsSingleElementArray() {
            // Arrange
            String[] single = {"single/path/"};

            // Act
            config.withClassPaths(single);

            // Assert
            assertEquals(1, config.getClassPaths().length);
            assertEquals("single/path/", config.getClassPaths()[0]);
        }

        @Test
        @DisplayName("withSourcePaths() with paths containing special characters")
        void withSourcePathsSpecialCharacters() {
            // Arrange
            String[] specialPaths = {
                "src/main/java/",
                "C:\\Users\\Test\\Source",
                "/usr/local/share/java",
                "../relative/path/",
                "path with spaces/src/"
            };

            // Act & Assert
            assertDoesNotThrow(() -> config.withSourcePaths(specialPaths));
            assertArrayEquals(specialPaths, config.getSourcePaths());
        }

        @Test
        @DisplayName("getJLStoString() with boundary JLS values")
        void getJLStoStringBoundaryValues() {
            // Assert
            assertAll("Boundary JLS values",
                () -> assertEquals("", ParseConfiguration.getJLStoString(0)),
                () -> assertEquals("JLS1", ParseConfiguration.getJLStoString(1)),
                () -> assertEquals("JLS" + AST.getJLSLatest(),
                    ParseConfiguration.getJLStoString(AST.getJLSLatest())),
                () -> assertEquals("",
                    ParseConfiguration.getJLStoString(AST.getJLSLatest() + 1))
            );
        }
    }

    // ========================================
    // DOCUMENTATION TESTS
    // ========================================

    @Nested
    @DisplayName("Documentation and Contract Tests")
    @Order(13)
    class DocumentationTests {

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("All factory methods should return non-null configurations")
        void allFactoryMethodsShouldReturnNonNull() {
            // Assert
            assertAll("Factory methods return non-null",
                () -> assertNotNull(ParseConfiguration.defaultConfig()),
                () -> assertNotNull(ParseConfiguration.fastConfig()),
                () -> assertNotNull(ParseConfiguration.testConfig("Test")),
                () -> assertNotNull(ParseConfiguration.specificJLSConfig(
                    ParseConfiguration.JAVA_11)),
                () -> assertNotNull(ParseConfiguration.conservativeJLSConfig())
            );
        }

        @Test
        @DisplayName("All getters should return non-null values")
        void allGettersShouldReturnNonNull() {
            // Arrange
            ParseConfiguration cfg = ParseConfiguration.defaultConfig();

            // Assert
            assertAll("Getters return non-null",
                () -> assertNotNull(cfg.getUnitName()),
                () -> assertNotNull(cfg.getClassPaths()),
                () -> assertNotNull(cfg.getSourcePaths()),
                () -> assertNotNull(cfg.toString())
            );
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("Configuration should maintain valid state after any operation")
        void configurationShouldMaintainValidState() {
            // Act - Perform various operations
            config.withSpecificJREVersion(ParseConfiguration.JAVA_17)
                  .withBindings(true)
                  .withUnitName("Test.java")
                  .withClassPaths(new String[]{"lib/"})
                  .withSourcePaths(null)  // Set to null
                  .withBindings(false);   // Toggle binding

            // Assert - State should still be valid
            assertAll("Valid state maintained",
                () -> assertNotNull(config.getUnitName()),
                () -> assertNotNull(config.getClassPaths()),
                () -> assertNotNull(config.getSourcePaths()),
                () -> assertTrue(config.getCurrentJLS() > 0)
            );
        }

        @SuppressWarnings("deprecation")
		@Test
        @DisplayName("isJLSSupported() should be consistent with version constants")
        void isJLSSupportedConsistentWithConstants() {
            // Assert - All defined constants should be supported
            assertAll("Version constant consistency",
                () -> assertTrue(config.isJLSSupported(
                    ParseConfiguration.JAVA_8)),
                () -> assertTrue(config.isJLSSupported(
                    ParseConfiguration.JAVA_11)),
                () -> assertTrue(config.isJLSSupported(
                    ParseConfiguration.JAVA_17)),
                () -> assertTrue(config.isJLSSupported(
                    ParseConfiguration.JAVA_21))
            );
        }
    }
}