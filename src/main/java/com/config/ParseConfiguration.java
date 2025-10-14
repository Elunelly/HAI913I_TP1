package com.config;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.ASTProcessor;

public class ParseConfiguration {
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||  ATTRIBUTES DEFINITION  |||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
	private static final Logger logger = LoggerFactory.getLogger(ASTProcessor.class);
	
    @SuppressWarnings("deprecation")
	public static int JAVA_8 = AST.JLS8;
	@SuppressWarnings("deprecation")
	public static int JAVA_11 = AST.JLS11;
	@SuppressWarnings("deprecation")
	public static int JAVA_17 = AST.JLS17;
    public static int JAVA_21 = AST.JLS21;
    public static int JAVA_LATEST = AST.getJLSLatest();
    private static Map<String,Integer> JAVA_JLS_LIST = setSupportedJLS();
    
    private int currentJLS = JAVA_LATEST;
    private boolean resolveBindings = true;
    private final List<Path> classPaths = new ArrayList<>();
    private final List<Path> sourcePaths = new ArrayList<>();
    private String defaultUnitName = fixJavaFileNamingWithDefault(null);

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||  CONSTRUCTORS DEFINITION  ||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

    public ParseConfiguration() {}

	public static ParseConfiguration defaultConfig() {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(true);
	}

	public static ParseConfiguration fastConfig() {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(false);
	}

	public static ParseConfiguration testConfig(String defaultUnitName) {
		return new ParseConfiguration()
				.withSpecificJREVersion(JAVA_LATEST)
				.withBindings(true)
				.withDefaultUnitName(defaultUnitName);
	}

	public static ParseConfiguration specificJLSConfig(int wantedJLS) {
		return new ParseConfiguration()
				.withSpecificJREVersion(wantedJLS)
				.withBindings(true);
	}

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

	public int getCurrentJLS() {return currentJLS;}

	private void setCurrentJLS(int wantedJLS) {
		int old = getCurrentJLS();
		this.currentJLS = JAVA_JLS_LIST.containsValue(wantedJLS) ? wantedJLS : this.currentJLS;
		logger.debug("Change value of 'currentJLS': %s -> %s".formatted(old,this.currentJLS));
	}

	public boolean isResolveBindings() {return resolveBindings;}

	private void setResolveBindings(boolean resolveBindings) {
		boolean old = isResolveBindings();
		this.resolveBindings = resolveBindings;
		logger.debug("Change value of 'resolveBindings': %s -> %s".formatted(old,this.resolveBindings));
	}

	public List<Path> getClassPaths() {return Collections.unmodifiableList(classPaths);}
	
	public List<Path> copyClassPaths() {return new ArrayList<>(classPaths);}

	public void addClassPath(Path classPath) {
		if (classPath != null && !classPaths.contains(classPath)) {
			classPaths.add(classPath);
			logger.debug("Class Path added: %s".formatted(classPath));
		}
	}
	
	public void addAllClassPaths(Path... paths) {
		for (Path path : paths) {
			addClassPath(path);
		}
	}

	public List<Path> getSourcePaths() {return Collections.unmodifiableList(sourcePaths);}
	
	public List<Path> copySourcePaths() {return new ArrayList<>(sourcePaths);}
	
	public void addSourcePath(Path sourcePath) {
		if (sourcePath != null && !sourcePaths.contains(sourcePath)) {
			sourcePaths.add(sourcePath);
			logger.debug("Source Path added: %s".formatted(sourcePath));
		}
	}
	
	public void addAllSourcePaths(Path... paths) {
		for (Path path : paths) {
			addSourcePath(path);
		}
	}

	public String getDefaultUnitName() {return defaultUnitName;}

	private void setDefaultUnitName(String defaultUnitName) {
		String old = getDefaultUnitName();
		this.defaultUnitName = fixJavaFileNamingWithDefault(defaultUnitName);
		logger.debug("Change value of 'defaultUnitName': %s -> %s".formatted(old,this.defaultUnitName));
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  METHODS DEFINITION  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

	public ParseConfiguration withSpecificJREVersion(int value) {
    	setCurrentJLS(value);
    	return this;
    }

	public ParseConfiguration withBindings(boolean value) {
		setResolveBindings(value);
		return this;
	}

	public ParseConfiguration withClassPaths(Path... paths) {
		addAllClassPaths(paths);
		return this;
	}

	public ParseConfiguration withClassPaths(String... strPaths) {
		List<Path> paths = new ArrayList<>();
		for (String str : strPaths) {
			paths.add(Path.of(str));
		}
		addAllClassPaths((Path[]) paths.toArray());
		return this;
	}

	public ParseConfiguration withSourcePaths(Path... paths) {
		addAllSourcePaths(paths);
		return this;
	}

	public ParseConfiguration withSourcePaths(String... strPaths) {
		List<Path> paths = new ArrayList<>();
		for (String str : strPaths) {
			paths.add(Path.of(str));
		}
		addAllSourcePaths((Path[]) paths.toArray());
		return this;
	}

	public ParseConfiguration withDefaultUnitName(String value) {
		setDefaultUnitName(value);
		return this;
	}
	
	private static Map<String,Integer> setSupportedJLS() {
		List<Integer> allJLS = AST.getAllSupportedVersions();
		Map<String,Integer> allJLSMap = new HashMap<>();
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
		    		logger.warn(k+"not available, using JLS"+oldVersion);
		        }
			} catch (NoSuchFieldException e) {
				//logger.warn("Error: The class ParseConfiguration does not have a field named 'JAVA_"+v+"'");
				continue;
			} catch (IllegalAccessException e) {
				logger.error("Error: The class ParseConfiguration cannot access the field named 'JAVA_"+v+"'");
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

	public static String getJLStoString(int version) {
		if (version < 1 || version > AST.getJLSLatest()) return "";
		return "JLS"+version;
	}
	
	public String getJLStoString() {
		return getJLStoString(currentJLS);
	}

	public boolean isJLSSupported(int versionJLS) {
		return JAVA_JLS_LIST.containsKey("JLS"+versionJLS);
	}

	public String listConfigSettings() {
		return
			ParseConfiguration.class.getSimpleName()+":"+
			"\n  → JRE's version     = "+getJLStoString(this.currentJLS)+
			"\n  → Resolve Bindings  = "+this.resolveBindings+
			"\n  → Default Unit name = "+this.defaultUnitName+
			"\n  → Class Paths       = "+this.classPaths+
			"\n  → Source Paths      = "+this.sourcePaths
		;
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "JREVersion=%s, "
				+ "resolveBindings=%s, "
				+ "defaultUnitName=%s, "
				+ "classPaths=%d, "
				+ "sourcePaths=%d}")
				.formatted(getJLStoString(), String.valueOf(resolveBindings), defaultUnitName, classPaths.size(), sourcePaths.size());
	}

}
