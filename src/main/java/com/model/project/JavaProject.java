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

import com.model.structural.ClassInfo;

public class JavaProject {
	
	private final String name;
	private final Path rootPath;
	private final Map<String,PackageInfo> packages = new HashMap<>();
	private final List<ClassInfo> classes = new ArrayList<>();
	private final Map<String,ClassInfo> classIndex = new HashMap<>();
	private final List<CompilationUnit> compilationUnits = new ArrayList<>();
	
	public JavaProject(String name, Path rootPath) {
		if (name==null || name.isBlank())
			throw new IllegalArgumentException("Project name cannot be null or blank");
		if (rootPath==null)
			throw new IllegalArgumentException("Project path cannot be null");
		this.name = name.trim();
		this.rootPath = rootPath;
	}
	
	public JavaProject(String name) {
		this(name,Path.of(""));
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
		if (packageInfo!=null) this.packages.putIfAbsent(packageInfo.getName(),packageInfo);
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
		if (compilationUnit!=null && !this.compilationUnits.contains(compilationUnit)) this.compilationUnits.add(compilationUnit);
	}
	
	public void addAllCompilationUnits(CompilationUnit...compilationUnits) {
		for (CompilationUnit compilationUnit : compilationUnits) {
			addCompilationUnit(compilationUnit);
		}
	}
	
	
	
	public boolean hasClasses() {
		return !this.classes.isEmpty();
	}
	
	@Override
    public String toString() {
        return String.format("JavaProject{name='%s', packages=%d, classes=%d, path='%s'}",
                this.name, this.packages.size(), this.classes.size(), this.rootPath);
    }
	
	public void clear() {
		this.packages.clear();
		this.classes.clear();
		this.classIndex.clear();
		this.compilationUnits.clear();
	}

}
