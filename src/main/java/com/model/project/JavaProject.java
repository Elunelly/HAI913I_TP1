package com.model.project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;

public class JavaProject {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaProject.class);
	
	private final String name;
	private final Path rootPath;
	private final Map<String,PackageInfo> packages = new HashMap<>();
	private final List<ClassInfo> classes = new ArrayList<>();
	private final Map<String,ClassInfo> classIndex = new HashMap<>();
	private final List<CompilationUnit> compilationUnits = new ArrayList<>();
	
	public JavaProject(String name, Path rootPath) {
		if (rootPath==null)
			rootPath = Path.of("");
		if (name==null || name.isBlank())
			name = rootPath.getFileName().toString();
		this.name = name.trim();
		logger.debug("Setting project name:"+this.name);
		this.rootPath = rootPath;
		logger.debug("Setting project root:"+this.rootPath);
	}
	
	public JavaProject(String name) {
		this(name, null);
	}
	
	public JavaProject(Path rootPath) {
		this(null, rootPath);
	}
	
	public JavaProject() {
		this(null, null);
	}
	
	public String getName() {return this.name;}
	
	public Path getRootPath() {return this.rootPath;}
	
	
	
	public Map<String,PackageInfo> getMappedPackages() {return Collections.unmodifiableMap(this.packages);}
	
	public Map<String,PackageInfo> copyMappedPackages() {return new HashMap<>(this.packages);}
	
	public List<PackageInfo> getPackages() {
		return Collections.unmodifiableList(new ArrayList<>(this.packages.values()));
	}
	
	public PackageInfo getPackage(String name) {
		return this.packages.get(name);
	}
	
	public List<String> getPackageNames() {
		return Collections.unmodifiableList(new ArrayList<>(this.packages.keySet()));
	}
	
	public void addPackage(PackageInfo packageInfo) {
		if (packageInfo!=null) {
			PackageInfo old = this.packages.putIfAbsent(packageInfo.getName(),packageInfo);
			if (old==null)
				logger.debug("Added package '%s': %s".formatted(packageInfo.getName(),packageInfo));
			else
				logger.debug("Change value of '%s': %s -> %s".formatted(packageInfo.getName(),old,packageInfo));
		}
	}
	
	public void addAllPackages(PackageInfo... packageInfos) {
		for (PackageInfo packageInfo : packageInfos) {
			addPackage(packageInfo);
		}
	}
	
	public void addAllPackages(List<PackageInfo> packageInfos) {
		addAllPackages((PackageInfo[]) packageInfos.toArray());
	}
	
	public boolean hasPackages() {
		return !this.packages.isEmpty();
	}
	
	public boolean hasPackage(String name) {
		return name!=null && this.packages.containsKey(name);
	}
    
    public void buildPackagesHierarchy() {
        List<String> sortedPackageNames = new ArrayList<>(this.packages.keySet());
        sortedPackageNames.sort(Comparator.comparingInt(name -> name.split("\\.").length));
        
        for (String packageName : sortedPackageNames) {
            if (packageName.isEmpty()) continue;
            
            int lastDot = packageName.lastIndexOf('.');
            if (lastDot > 0) {
                String parentName = packageName.substring(0, lastDot);
                PackageInfo parent = this.packages.get(parentName);
                PackageInfo child = this.packages.get(packageName);
                child.setParentPackage(parent);                
                if (parent != null && child != null) {
                    parent.addSubPackage(child);
				}
			}
		}
	}
	
	public List<ClassInfo> getClasses() {return Collections.unmodifiableList(this.classes);}
	
	public List<ClassInfo> copyClasses() {return new ArrayList<>(this.classes);}
	
	public void addClass(ClassInfo classInfo) {
		if (classInfo!=null && !this.classes.contains(classInfo)) {
			this.classes.add(classInfo);
			logger.debug("Added classInfo: "+classInfo.getSignature());
			
			this.classIndex.put(classInfo.getName(), classInfo);
			
			String packageName = classInfo.getPackageName();
			if (packageName!=null && this.packages.containsKey(packageName))
				this.packages.get(packageName).addClass(classInfo);
		}
	}
	
	public void addAllClasses(ClassInfo...classInfos) {
		for (ClassInfo classInfo : classInfos) {
			addClass(classInfo);
		}
	}
	
	public void addAllClasses(List<ClassInfo> classInfos) {
		addAllClasses((ClassInfo[]) classInfos.toArray());
	}
	
	public ClassInfo getClass(String name) {
		return this.classIndex.get(name);
	}
	
	public List<ClassInfo> getClassesByPackage(String name) {
		return this.classes.stream()
				.filter(c -> c.getPackageName().equalsIgnoreCase(name))
				.collect(Collectors.toUnmodifiableList());
	}
	
	
	
	public List<CompilationUnit> getCompilationUnits() {return Collections.unmodifiableList(this.compilationUnits);}
	
	public List<CompilationUnit> copyCompilationUnits() {return new ArrayList<>(this.compilationUnits);}
	
	public void addCompilationUnit(CompilationUnit compilationUnit) {
		if (compilationUnit!=null && !this.compilationUnits.contains(compilationUnit)) {
			this.compilationUnits.add(compilationUnit);
			logger.debug("Added CompilationUnit: "+compilationUnit);
		}
	}
	
	public void addAllCompilationUnits(CompilationUnit...compilationUnits) {
		for (CompilationUnit compilationUnit : compilationUnits) {
			addCompilationUnit(compilationUnit);
		}
	}
	
	public void addAllCompilationUnits(List<CompilationUnit> compilationUnits) {
		addAllCompilationUnits((CompilationUnit[]) compilationUnits.toArray());
	}
	
	
	
	public boolean hasClasses() {
		return !this.classes.isEmpty();
	}
	
	public void clear() {
		this.packages.clear();
		this.classes.clear();
		this.classIndex.clear();
		this.compilationUnits.clear();
		logger.debug("Cleared JavaProject (packages, classes, compilationUnits): "+this.name);
	}
	
	@Override
    public String toString() {
        return ("JavaProject{"
        		+ "name=%s, "
        		+ "root=%s, "
        		+ "packages=%d, "
        		+ "classes=%d, "
        		+ "compilationUnits=%d}")
        		.formatted(name, rootPath, packages.size(), classes.size(), compilationUnits.size());
    }

}
