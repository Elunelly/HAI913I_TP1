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

import com.model.interfaces.HasClasses;
import com.model.interfaces.ModelInfo;
import com.model.structural.ClassInfo;

public class ProjectInfo implements ModelInfo, HasClasses {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectInfo.class);
	
	private final String name;
	private final Path rootPath;
	private final Map<String,PackageInfo> packages = new HashMap<>();
	private final List<ClassInfo> classes = new ArrayList<>();
	private final Map<String,ClassInfo> classIndex = new HashMap<>();
	private final List<CompilationUnit> compilationUnits = new ArrayList<>();
	
	public ProjectInfo(String name, Path rootPath) {
		if (rootPath==null)
			rootPath = Path.of("");
		if (name==null || name.isBlank())
			name = rootPath.getFileName().toString();
		this.name = name.trim();
		logger.debug("Setting project name:"+this.name);
		this.rootPath = rootPath;
		logger.debug("Setting project root:"+this.rootPath);
	}
	
	public ProjectInfo(String name) {
		this(name, null);
	}
	
	public ProjectInfo(Path rootPath) {
		this(null, rootPath);
	}
	
	public ProjectInfo() {
		this(null, null);
	}
	
	public String getName() {return this.name;}
	
	public String getFullName() {return this.rootPath.toString();}
	
	public Path getRootPath() {return this.rootPath;}
	
	public Map<String,PackageInfo> getMappedPackages() {return Collections.unmodifiableMap(this.packages);}
	
	public Map<String,PackageInfo> copyMappedPackages() {return new HashMap<>(this.packages);}
	
	public List<PackageInfo> getPackages() {
		return new ArrayList<>(this.packages.values());
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
    
    public List<ClassInfo> classes() {return classes;}
	
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
		for (CompilationUnit compilationUnit : compilationUnits) {
			addCompilationUnit(compilationUnit);
		}
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
        return (this.getClass().getSimpleName()+"{"
        		+ "name=%s, "
        		+ "root=%s, "
        		+ "packages=%d, "
        		+ "classes=%d, "
        		+ "compilationUnits=%d}")
        		.formatted(name, rootPath, packages.size(), classes.size(), compilationUnits.size());
    }

}
