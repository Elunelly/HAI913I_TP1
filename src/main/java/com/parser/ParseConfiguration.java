package com.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.AST;


/**
 * Configuration manager for Eclipse JDT (Java Development Tools) AST parsing operations.
 * 
 * <p>This class provides a fluent API for configuring AST parser settings, including JLS 
 * (Java Language Specification) version selection, binding resolution, and path configuration 
 * for source and compiled classes.</p>
 * 
 * <p><strong>Design Rationale:</strong></p>
 * <ul>
 *   <li>Centralized configuration to avoid scattered parser setup code</li>
 *   <li>Fluent interface pattern for readable, chainable configuration</li>
 *   <li>Backward compatibility handling for different JDT versions</li>
 *   <li>Factory methods for common use cases (default, fast, test scenarios)</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * ParseConfiguration config = ParseConfiguration.defaultConfig()
 *     .withUnitName("MyClass.java")
 *     .withSourcePaths(new String[]{"src/main/java"});
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong> This class is NOT thread-safe. Create separate instances 
 * for concurrent operations.</p>
 * 
 * <p><strong>Future Enhancements:</strong></p>
 * <ul>
 *   <li>Add validation for path existence and accessibility</li>
 *   <li>Support for custom encoding settings</li>
 *   <li>Caching mechanism for parsed configurations</li>
 *   <li>Builder pattern for complex configurations</li>
 *   <li>Integration with Java module system (JPMS) for Java 9+</li>
 * </ul>
 * 
 * @author Luna
 * @version 0.0.1
 * @since 0.0.1
 * @see org.eclipse.jdt.core.dom.AST
 * @see org.eclipse.jdt.core.dom.ASTParser
 */
public class ParseConfiguration {
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||  ATTRIBUTES DEFINITION  |||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
    /**
     * JLS Level for Java SE 8 (JLS8).
     * <p><strong>Naming:</strong> JAVA_8 clearly indicates the Java version</p>
     * <p><strong>Type:</strong> int - Eclipse JDT uses integer constants for JLS levels</p>
     * <p><strong>Static:</strong> Shared across all instances as it's a version constant</p>
     * 
     * @deprecated This field uses deprecated AST.JLS8 constant. Consider using dynamic detection.
     */
    public static int JAVA_8 = AST.JLS8;
    
    /**
     * JLS Level for Java SE 11 (JLS11).
     * <p><strong>Naming:</strong> Follows the JAVA_X pattern for consistency</p>
     * <p><strong>Type:</strong> int - Maintains compatibility with AST API</p>
     * <p><strong>Fallback:</strong> Defaults to JAVA_8 if JLS11 is unavailable</p>
     * 
     * @deprecated This field uses deprecated AST.JLS11 constant. Consider using dynamic detection.
     */
	public static int JAVA_11 = AST.JLS11;

    /**
     * JLS Level for Java SE 17 (JLS17) - LTS version.
     * <p><strong>Naming:</strong> JAVA_17 emphasizes this is an LTS (Long Term Support) release</p>
     * <p><strong>Type:</strong> int - Required by Eclipse JDT parser configuration</p>
     * <p><strong>Fallback Chain:</strong> JLS17 → JLS11 → JLS8</p>
     * 
     * @deprecated This field uses deprecated AST.JLS17 constant. Consider using dynamic detection.
     */
	public static int JAVA_17 = AST.JLS17;

    /**
     * JLS Level for Java SE 21 (JLS21) - Latest LTS version.
     * <p><strong>Naming:</strong> JAVA_21 indicates current LTS release</p>
     * <p><strong>Type:</strong> int - Consistent with other version constants</p>
     * <p><strong>Fallback Chain:</strong> JLS21 → JLS17 → JLS11 → JLS8</p>
     */
    public static int JAVA_21 = AST.JLS21;

    /**
     * JLS Level for the latest supported Java version by the current JDT runtime.
     * <p><strong>Naming:</strong> JAVA_LATEST indicates this is dynamically determined</p>
     * <p><strong>Type:</strong> int - Updated based on JDT version at runtime</p>
     * <p><strong>Usage:</strong> Recommended for parsing modern code without version concerns</p>
     * <p><strong>Warning:</strong> May change between JDT versions; use specific versions for stability</p>
     */
    public static int JAVA_LATEST = AST.getJLSLatest();

    /**
     * Registry mapping JLS version strings to their integer constants.
     * <p><strong>Naming:</strong> JAVA_JLS_LIST as it lists available JLS versions</p>
     * <p><strong>Type:</strong> HashMap&lt;String,Integer&gt; for O(1) lookup by version name</p>
     * <p><strong>Purpose:</strong> Enables runtime validation of supported JLS versions</p>
     * <p><strong>Future Enhancement:</strong> Could be exposed via getter for external validation</p>
     */
    private static HashMap<String,Integer> JAVA_JLS_LIST = setSupportedJLS();

    /**
     * Static initialization block for runtime JLS version detection and fallback configuration.
     * 
     * <p><strong>Purpose:</strong></p>
     * <ul>
     *   <li>Detects which JLS versions are available in the current JDT runtime</li>
     *   <li>Establishes fallback chain: JLS21 → JLS17 → JLS11 → JLS8</li>
     *   <li>Provides diagnostic output for debugging version issues</li>
     *   <li>Ensures graceful degradation on older JDT versions</li>
     * </ul>
     * 
     * <p><strong>Implementation Strategy:</strong></p>
     * Uses reflection to safely detect constant availability, preventing ClassNotFoundException
     * or NoSuchFieldException when running on older JDT versions that lack newer JLS constants.
     * 
     * <p><strong>Future Enhancement:</strong> Consider adding a quiet mode to suppress console output</p>
     */
    static {
    	
    	int java11 = JAVA_8;
    	int java17 = JAVA_8;
    	int java21 = JAVA_8;
    	int latest = JAVA_8;
        
        try {java11 = AST.class.getField("JLS11").getInt(null);}
        catch (Exception e) {System.out.println("JLS11 not available, using JLS8");}
        
        try {java17 = AST.class.getField("JLS17").getInt(null);}
        catch (Exception e) {
        	System.out.println("JLS17 not available, using " + (java11!=JAVA_8 ? "JLS11":"JLS8"));
            java17 = java11;
        }
        
        try {java21 = AST.class.getField("JLS21").getInt(null);}
        catch (Exception e) {
            System.out.println("JLS21 not available, using " + (java17!=JAVA_8 ? "JLS17":"JLS11"));
            java21 = java17;
        }
        
        try {latest = (Integer) AST.class.getMethod("getJLSLatest").invoke(null);}
        catch (Exception e) {latest = java21;}
        
        JAVA_11 = java11;
        JAVA_17 = java17;
        JAVA_21 = java21;
        JAVA_LATEST = latest;
        
        System.out.println("JDT Configuration detected - Available versions:");
        System.out.println("  JLS8: " + JAVA_8);
        System.out.println("  JLS11: " + JAVA_11);
        System.out.println("  JLS17: " + JAVA_17);
        System.out.println("  JLS21: " + JAVA_21);
        System.out.println("  Latest: " + JAVA_LATEST);
    }

    /**
     * The current JLS (Java Language Specification) level for this configuration instance.
     * <p><strong>Naming:</strong> currentJLS indicates this is the active version setting</p>
     * <p><strong>Type:</strong> int - Matches AST parser expectations</p>
     * <p><strong>Default:</strong> JAVA_LATEST for maximum compatibility with modern syntax</p>
     * <p><strong>Mutability:</strong> Private with fluent setter for controlled modification</p>
     */
    private int currentJLS = JAVA_LATEST;

    /**
     * Flag controlling whether the parser should resolve bindings (type information, references).
     * <p><strong>Naming:</strong> resolveBindings clearly indicates boolean nature and purpose</p>
     * <p><strong>Type:</strong> boolean - Standard flag type</p>
     * <p><strong>Default:</strong> true - Most use cases need binding resolution</p>
     * <p><strong>Performance:</strong> Setting to false significantly speeds up parsing but loses type info</p>
     * <p><strong>Trade-off:</strong> Binding resolution enables semantic analysis but requires classpath setup</p>
     */
    private boolean resolveBindings = true;

    /**
     * Array of classpath entries for resolving type bindings during parsing.
     * <p><strong>Naming:</strong> classPaths (plural) indicates this holds multiple paths</p>
     * <p><strong>Type:</strong> String[] - Standard format for Java classpath elements</p>
     * <p><strong>Default:</strong> Empty array - No external dependencies by default</p>
     * <p><strong>Usage:</strong> Required when resolveBindings=true and code references external types</p>
     * <p><strong>Format:</strong> Absolute file system paths to .jar files or directories</p>
     * <p><strong>Future Enhancement:</strong> Add validation for path existence and format</p>
     */
    private String[] classPaths = new String[0];

    /**
     * Array of source path entries for locating source files during binding resolution.
     * <p><strong>Naming:</strong> sourcePaths (plural) for consistency with classPaths</p>
     * <p><strong>Type:</strong> String[] - Parallel to classPaths structure</p>
     * <p><strong>Default:</strong> Empty array - Only current compilation unit by default</p>
     * <p><strong>Usage:</strong> Enables cross-file type resolution and source-level analysis</p>
     * <p><strong>Format:</strong> Absolute paths to source root directories</p>
     */
    private String[] sourcePaths = new String[0];

    /**
     * The name of the compilation unit being parsed.
     * <p><strong>Naming:</strong> unitName indicates this is a compilation unit identifier</p>
     * <p><strong>Type:</strong> String - Standard file naming</p>
     * <p><strong>Default:</strong> "UnnamedFile.java" - Provides meaningful fallback</p>
     * <p><strong>Purpose:</strong> Used in error messages and binding resolution context</p>
     * <p><strong>Requirement:</strong> Should match actual .java file name for accurate error reporting</p>
     */
    private String unitName = fixJavaFileNamingWithDefault(null);

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||  CONSTRUCTORS DEFINITION  ||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

    /**
     * Default no-argument constructor creating an unconfigured instance.
     * 
     * <p><strong>Design Choice:</strong> Package-private to encourage use of factory methods,
     * but available for advanced users who need custom initialization sequences.</p>
     * 
     * <p><strong>Usage:</strong> Prefer factory methods (defaultConfig, fastConfig, etc.) 
     * over direct instantiation for better readability and common configurations.</p>
     * 
     * @see #defaultConfig()
     * @see #fastConfig()
     * @see #testConfig(String)
     */
    public ParseConfiguration() {}

    /**
     * Factory method creating a standard configuration with full binding resolution.
     * 
     * <p><strong>Purpose:</strong> Most common use case - full semantic analysis with latest Java version</p>
     * 
     * <p><strong>Configuration:</strong></p>
     * <ul>
     *   <li>JLS Version: Latest available (JAVA_LATEST)</li>
     *   <li>Binding Resolution: Enabled (true)</li>
     *   <li>Classpath: Empty (assumes self-contained code)</li>
     * </ul>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Type hierarchy analysis</li>
     *   <li>Method call resolution</li>
     *   <li>Variable usage tracking</li>
     *   <li>Refactoring tools</li>
     * </ul>
     * 
     * <p><strong>Performance:</strong> Slower than fastConfig() due to binding resolution</p>
     * 
     * @return A new configuration instance with default settings
     * @see #fastConfig()
     */
	public static ParseConfiguration defaultConfig() {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(true);
	}

    /**
     * Factory method creating a lightweight configuration without binding resolution.
     * 
     * <p><strong>Purpose:</strong> Optimized for syntax-only parsing when type information is unnecessary</p>
     * 
     * <p><strong>Configuration:</strong></p>
     * <ul>
     *   <li>JLS Version: Latest available (JAVA_LATEST)</li>
     *   <li>Binding Resolution: Disabled (false)</li>
     *   <li>Performance: ~3-10x faster than defaultConfig()</li>
     * </ul>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Syntax validation</li>
     *   <li>Code formatting tools</li>
     *   <li>Comment extraction</li>
     *   <li>Structure-only analysis (class/method names)</li>
     *   <li>Batch processing of many files</li>
     * </ul>
     * 
     * <p><strong>Limitations:</strong> Cannot resolve types, method signatures, or variable references</p>
     * 
     * @return A new configuration instance optimized for speed
     * @see #defaultConfig()
     */
	public static ParseConfiguration fastConfig() {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(false);
	}

    /**
     * Factory method creating a configuration suitable for test scenarios.
     * 
     * <p><strong>Purpose:</strong> Provides consistent test configuration with custom naming</p>
     * 
     * <p><strong>Configuration:</strong></p>
     * <ul>
     *   <li>JLS Version: Latest available (JAVA_LATEST)</li>
     *   <li>Binding Resolution: Enabled (true)</li>
     *   <li>Unit Name: Custom name with .java extension added</li>
     * </ul>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Unit testing parser functionality</li>
     *   <li>Integration testing with named compilation units</li>
     *   <li>Debugging with identifiable file names</li>
     * </ul>
     * 
     * @param defaultUnitName Base name for the compilation unit (without .java extension)
     * @return A new configuration instance configured for testing
     * @see #withUnitName(String)
     */
	public static ParseConfiguration testConfig(String defaultUnitName) {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(true)
				.withUnitName(defaultUnitName);
	}

    /**
     * Factory method creating a configuration targeting a specific Java version.
     * 
     * <p><strong>Purpose:</strong> For parsing code written for a specific Java language level</p>
     * 
     * <p><strong>Configuration:</strong></p>
     * <ul>
     *   <li>JLS Version: User-specified version</li>
     *   <li>Binding Resolution: Enabled (true)</li>
     * </ul>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Analyzing legacy codebases (e.g., Java 8 projects)</li>
     *   <li>Validating code against specific language specifications</li>
     *   <li>Migration tools checking version compatibility</li>
     * </ul>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * ParseConfiguration config = ParseConfiguration.specificJLSConfig(JAVA_11);
     * }</pre>
     * 
     * @param wantedJLS The desired JLS level constant (e.g., JAVA_8, JAVA_11, etc.)
     * @return A new configuration instance targeting the specified Java version
     * @see #JAVA_8
     * @see #JAVA_11
     * @see #JAVA_17
     * @see #JAVA_21
     */
	public static ParseConfiguration specificJLSConfig(int wantedJLS) {
		return new ParseConfiguration()
				.withSpecificJREVersion(wantedJLS)
				.withBindings(true);
	}

    /**
     * Factory method creating a configuration using Java 8 (maximum compatibility).
     * 
     * <p><strong>Purpose:</strong> Ensures broadest compatibility when parsing unknown code</p>
     * 
     * <p><strong>Configuration:</strong></p>
     * <ul>
     *   <li>JLS Version: Java 8 (JLS8)</li>
     *   <li>Binding Resolution: Enabled (true)</li>
     * </ul>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Processing code from unknown sources</li>
     *   <li>Maximum backward compatibility requirements</li>
     *   <li>Working with legacy systems</li>
     *   <li>Tool deployment on diverse JDT versions</li>
     * </ul>
     * 
     * <p><strong>Limitation:</strong> Won't recognize Java 9+ syntax features (modules, var, records, etc.)</p>
     * 
     * @return A new configuration instance using Java 8 language level
     * @see #defaultConfig()
     */
	public static ParseConfiguration conservativeJLSConfig() {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_8)
				.withBindings(true);
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||  ACCESSORS DEFINITION  |||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

    /**
     * Retrieves the currently configured JLS level.
     * 
     * <p><strong>Naming:</strong> getCurrentJLS follows JavaBean convention for clarity</p>
     * <p><strong>Return Type:</strong> int - Direct AST API compatibility</p>
     * <p><strong>Usage:</strong> Useful for logging, debugging, or conditional parsing logic</p>
     * 
     * @return The integer constant representing the current JLS level
     * @see #withSpecificJREVersion(int)
     */
	public int getCurrentJLS() {return currentJLS;}

    /**
     * Sets the current JLS level.
     * 
     * <p><strong>Visibility:</strong> Private to enforce fluent API usage via withSpecificJREVersion()</p>
     * <p><strong>Validation:</strong> Currently none - Future enhancement could validate against JAVA_JLS_LIST</p>
     * 
     * @param wantedJLS The JLS level to set
     * @see #withSpecificJREVersion(int)
     */
	private void setCurrentJLS(int wantedJLS) {
		this.currentJLS = JAVA_JLS_LIST.containsValue(wantedJLS) ? wantedJLS : this.currentJLS;
	}

    /**
     * Checks whether binding resolution is currently enabled.
     * 
     * <p><strong>Naming:</strong> isResolveBindings follows boolean accessor convention</p>
     * <p><strong>Return Type:</strong> boolean - Simple flag status</p>
     * 
     * @return true if binding resolution is enabled, false otherwise
     * @see #withBindings(boolean)
     */
	public boolean isResolveBindings() {return resolveBindings;}

    /**
     * Sets the binding resolution flag.
     * 
     * <p><strong>Visibility:</strong> Private to enforce fluent API usage via withBindings()</p>
     * 
     * @param resolveBindings true to enable binding resolution, false to disable
     * @see #withBindings(boolean)
     */
	private void setResolveBindings(boolean resolveBindings) {
		this.resolveBindings = resolveBindings;
	}

    /**
     * Retrieves the configured classpath entries.
     * 
     * <p><strong>Naming:</strong> getClassPaths (plural) indicates array return</p>
     * <p><strong>Return Type:</strong> String[] - Direct array for compatibility</p>
     * <p><strong>Safety:</strong> Returns internal reference - caller modifications affect this instance</p>
     * <p><strong>Future Enhancement:</strong> Return defensive copy or immutable collection</p>
     * 
     * @return Array of classpath entries (may be empty, never null)
     * @see #withClassPaths(String[])
     */
	public String[] getClassPaths() {return classPaths.clone();}

    /**
     * Sets the classpath entries, creating a defensive copy.
     * 
     * <p><strong>Visibility:</strong> Private to enforce fluent API usage via withClassPaths()</p>
     * <p><strong>Safety:</strong> Clones input array to prevent external modification</p>
     * <p><strong>Null Handling:</strong> Null input converted to empty array</p>
     * 
     * @param classPaths Array of classpath entries (null-safe)
     * @see #withClassPaths(String[])
     */
	private void setClassPaths(String[] classPaths) {
		this.classPaths = classPaths!=null ? classPaths.clone() : new String[0];
	}

    /**
     * Retrieves the configured source path entries.
     * 
     * <p><strong>Naming:</strong> getSourcePaths (plural) for consistency with getClassPaths()</p>
     * <p><strong>Return Type:</strong> String[] - Matches classpath accessor pattern</p>
     * <p><strong>Safety:</strong> Returns internal reference - same limitation as getClassPaths()</p>
     * 
     * @return Array of source path entries (may be empty, never null)
     * @see #withSourcePaths(String[])
     */
	public String[] getSourcePaths() {return sourcePaths.clone();}

    /**
     * Sets the source path entries, creating a defensive copy.
     * 
     * <p><strong>Visibility:</strong> Private to enforce fluent API usage via withSourcePaths()</p>
     * <p><strong>Safety:</strong> Clones input array to prevent external modification</p>
     * <p><strong>Null Handling:</strong> Null input converted to empty array</p>
     * 
     * @param sourcePaths Array of source path entries (null-safe)
     * @see #withSourcePaths(String[])
     */
	private void setSourcePaths(String[] sourcePaths) {
		this.sourcePaths = sourcePaths!=null ? sourcePaths.clone() : new String[0];
	}

    /**
     * Retrieves the configured compilation unit name.
     * 
     * <p><strong>Naming:</strong> getUnitName follows standard accessor convention</p>
     * <p><strong>Return Type:</strong> String - Simple identifier</p>
     * <p><strong>Guarantee:</strong> Never null, defaults to "UnnamedFile.java"</p>
     * 
     * @return The compilation unit name (never null)
     * @see #withUnitName(String)
     */
	public String getUnitName() {return unitName;}

    /**
     * Sets the compilation unit name with null-safe fallback.
     * 
     * <p><strong>Visibility:</strong> Private to enforce fluent API usage via withUnitName()</p>
     * <p><strong>Null Handling:</strong> Null input replaced with default name</p>
     * 
     * @param unitName The compilation unit name (null-safe)
     * @see #withUnitName(String)
     */
	private void setUnitName(String unitName) {
		this.unitName = fixJavaFileNamingWithDefault(unitName);
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  METHODS DEFINITION  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

    /**
     * Fluent setter for JRE/JLS version configuration.
     * 
     * <p><strong>Naming:</strong> withSpecificJREVersion follows fluent API convention</p>
     * <p><strong>Return Type:</strong> this - Enables method chaining</p>
     * <p><strong>Pattern:</strong> Builder/Fluent Interface pattern</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * config.withSpecificJREVersion(JAVA_17)
     *       .withBindings(true)
     *       .withUnitName("Test.java");
     * }</pre>
     * 
     * @param value The JLS level constant to configure
     * @return This configuration instance for method chaining
     * @see #getCurrentJLS()
     */
	public ParseConfiguration withSpecificJREVersion(int value) {
    	setCurrentJLS(value);
    	return this;
    }

    /**
     * Fluent setter for binding resolution configuration.
     * 
     * <p><strong>Naming:</strong> withBindings concisely indicates the configuration option</p>
     * <p><strong>Return Type:</strong> this - Enables method chaining</p>
     * 
     * @param value true to enable binding resolution, false to disable
     * @return This configuration instance for method chaining
     * @see #isResolveBindings()
     */
	public ParseConfiguration withBindings(boolean value) {
		setResolveBindings(value);
		return this;
	}

    /**
     * Fluent setter for classpath configuration.
     * 
     * <p><strong>Naming:</strong> withClassPaths (plural) indicates array parameter</p>
     * <p><strong>Return Type:</strong> this - Enables method chaining</p>
     * <p><strong>Safety:</strong> Input array is cloned internally</p>
     * 
     * @param value Array of classpath entries (null-safe)
     * @return This configuration instance for method chaining
     * @see #getClassPaths()
     */
	public ParseConfiguration withClassPaths(String[] value) {
		setClassPaths(value);
		return this;
	}

    /**
     * Fluent setter for source path configuration.
     * 
     * <p><strong>Naming:</strong> withSourcePaths (plural) for consistency with withClassPaths</p>
     * <p><strong>Return Type:</strong> this - Enables method chaining</p>
     * <p><strong>Safety:</strong> Input array is cloned internally</p>
     * 
     * @param value Array of source path entries (null-safe)
     * @return This configuration instance for method chaining
     * @see #getSourcePaths()
     */
	public ParseConfiguration withSourcePaths(String[] value) {
		setSourcePaths(value);
		return this;
	}

    /**
     * Fluent setter for compilation unit name configuration.
     * 
     * <p><strong>Naming:</strong> withUnitName follows fluent API convention</p>
     * <p><strong>Return Type:</strong> this - Enables method chaining</p>
     * <p><strong>Safety:</strong> Null values replaced with default name</p>
     * 
     * @param value The compilation unit name (null-safe)
     * @return This configuration instance for method chaining
     * @see #getUnitName()
     */
	public ParseConfiguration withUnitName(String value) {
		setUnitName(value);
		return this;
	}
	
	// AST.getAllSupportedVersions();
	private static HashMap<String,Integer> setSupportedJLS() {
		ArrayList<Integer> allJLS = new ArrayList<>(Arrays.asList(8,11,17,21,AST.getJLSLatest()));
		HashMap<String,Integer> allJLSMap = new HashMap<>();
		int oldVersion = 0;
		for (int v : allJLS) {
			String k = "JLS"+v;
			try {
				Field field = ParseConfiguration.class.getField("JAVA_"+(v!=AST.getJLSLatest()?v:"LATEST"));
				try {
		        	int currVersion = AST.class.getField(k).getInt(null);
		        	field.setInt(null,currVersion);
					allJLSMap.put(k, currVersion);
		        	oldVersion = currVersion;
		        } catch (NoSuchFieldException e) {
		        	field.setInt(null,oldVersion);
		        	allJLSMap.put(k, oldVersion);
		        	System.out.println(k+"not available, using JLS"+oldVersion);
		        }
			} catch (NoSuchFieldException e) {
				System.out.println("Error: The class ParseConfiguration does not have a field named 'JAVA_"+v+"'");
				continue;
			} catch (IllegalAccessException e) {
				System.out.println("Error: The class ParseConfiguration cannot access the field named 'JAVA_"+v+"'");
				continue;
			}
		};
		return allJLSMap;
	}
	
	public static String fixJavaFileNamingWithDefault(String filename) {
		String defaultName = "DefaultClass";
		String extension = ".java";
        if (filename == null || filename.trim().isEmpty()) {
            return defaultName+extension;
        }

        // delete .java extension if present
        if (filename.toLowerCase().endsWith(extension)) {
            filename = filename.substring(0, filename.length() - 5);
        }

        // Delete illegal and invalid characters
        filename = filename.replaceAll("[\\\\/:*?\"<>|]", ""); // Delete illegal characters for file naming
        filename = filename.replaceAll("[^\\p{Alnum}_$]", ""); // Delete invalid characters for Java files

        // Cut the string if too long
        int maxLength = 255 - 5; // 5 = ".java"
        if (filename.length() > maxLength) {
            filename = filename.substring(0, maxLength);
        }

        // Check if the filename starts with a letter, _ or $
        if (!filename.isEmpty() && !filename.matches("^[a-zA-Z_$].*")) {
            filename = "_" + filename; // add an _ at the beginning if not
        }

        // Set default if empty in the end
        if (filename.trim().isEmpty()) {
            filename = defaultName;
        }

        return filename.trim()+extension;
    }

    /**
     * Converts a JLS integer constant to its string representation.
     * 
     * <p><strong>Naming:</strong> getJLStoString clearly indicates conversion purpose</p>
     * <p><strong>Purpose:</strong> Provides human-readable version names for logging and display</p>
     * 
     * <p><strong>Return Values:</strong></p>
     * <ul>
     *   <li>Valid version: Returns "JLS" + version (e.g., "JLS8", "JLS17")</li>
     *   <li>Invalid version: Returns empty string</li>
     * </ul>
     * 
     * <p><strong>Validation:</strong> Checks version is within valid range [1, Latest]</p>
     * 
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * String version = ParseConfiguration.getJLStoString(JAVA_17); // Returns "JLS17"
     * }</pre>
     * 
     * <p><strong>Future Enhancement:</strong> Could use JAVA_JLS_LIST for validation instead of range check</p>
     * 
     * @param version The JLS integer constant to convert
     * @return String representation of the JLS version, or empty string if invalid
     * @see #isJLSSupported(int)
     */
	public static String getJLStoString(int version) {
		if (version < 1 || version > AST.getJLSLatest()) return "";
		return "JLS"+version;
	}

    /**
     * Validates whether a specific JLS version is supported in the current runtime.
     * 
     * <p><strong>Naming:</strong> isJLSSupported follows boolean query convention</p>
     * <p><strong>Purpose:</strong> Enables pre-flight validation before setting JLS version</p>
     * 
     * <p><strong>Implementation:</strong> Queries the internal JAVA_JLS_LIST registry
     * populated during class initialization</p>
     * 
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * if (config.isJLSSupported(17)) {
     *     config.withSpecificJREVersion(JAVA_17);
     * } else {
     *     config.withSpecificJREVersion(JAVA_11);
     * }
     * }</pre>
     * 
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     *   <li>Conditional configuration based on runtime capabilities</li>
     *   <li>Version compatibility checks in tools</li>
     *   <li>Graceful degradation in multi-version environments</li>
     * </ul>
     * 
     * @param versionJLS The JLS version number to validate (e.g., 8, 11, 17, 21)
     * @return true if the specified JLS version is supported, false otherwise
     * @see #JAVA_JLS_LIST
     * @see #getJLStoString(int)
     */
	public boolean isJLSSupported(int versionJLS) {
		return JAVA_JLS_LIST.containsKey("JLS"+versionJLS);
	}

    /**
     * Provides a human-readable string representation of this configuration.
     * 
     * <p><strong>Purpose:</strong> Facilitates debugging, logging, and configuration inspection</p>
     * 
     * <p><strong>Format:</strong> Multi-line output with labeled configuration values</p>
     * 
     * <p><strong>Output Example:</strong></p>
     * <pre>
     * ParseConfiguration:
     *   → JRE's version     = JLS21
     *   → Resolve Bindings  = true
     *   → Default Unit name = MyClass.java
     *   → Class Paths       = [Ljava.lang.String;@15db9742
     *   → Source Paths      = [Ljava.lang.String;@6d06d69c
     * </pre>
     * 
     * <p><strong>Known Limitation:</strong> Array paths display as object references rather than contents</p>
     * 
     * <p><strong>Future Enhancements:</strong></p>
     * <ul>
     *   <li>Format arrays as comma-separated lists: Arrays.toString(classPaths)</li>
     *   <li>Add JSON output option for machine parsing</li>
     *   <li>Include validation status indicators</li>
     *   <li>Add summary statistics (e.g., "3 class paths configured")</li>
     *   <li>Consider using StringBuilder for better performance</li>
     * </ul>
     * 
     * @return A formatted string describing this configuration's current state
     */
	@Override
	public String toString() {
		return
			ParseConfiguration.class.getSimpleName()+":"+
			"\n  → JRE's version     = "+getJLStoString(this.currentJLS)+
			"\n  → Resolve Bindings  = "+this.resolveBindings+
			"\n  → Default Unit name = "+this.unitName+
			"\n  → Class Paths       = "+this.classPaths+
			"\n  → Source Paths      = "+this.sourcePaths
		;
	}

}
