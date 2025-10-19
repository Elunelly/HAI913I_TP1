package com.model.project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.metrics.ProjectMetrics;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.utils.table.TableUI;

public class ProjectInfo extends ProjectNode {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectInfo.class);
	
	private final Path rootPath;
	private final Map<String,PackageInfo> packages = new HashMap<>();
	
	private final ProjectMetrics metrics;
	
	public ProjectInfo(String name, Path rootPath) {
		super(checkName(name,rootPath));
		logger.trace("{} -> ProjectInfo(String,Path)",this.name);
		logger.debug("Setting project name: "+this.name);
		this.rootPath = checkRootPath(rootPath);
		logger.debug("Setting project root: "+this.rootPath);
		this.metrics = new ProjectMetrics(getQualifiedName());
	}
	
	public ProjectInfo(String name) {
		this(name, null);
		logger.trace("{} -> ProjectInfo(String)",this.name);
	}
	
	public ProjectInfo(Path rootPath) {
		this(null, rootPath);
		logger.trace("{} -> ProjectInfo(Path)",this.name);
	}
	
	public ProjectInfo() {
		this(null, null);
		logger.trace("{} -> ProjectInfo()",this.name);
	}
	
	private static String checkName(String name, Path rootPath) {
		logger.trace("{} -> CheckName(String,Path)",name);
		rootPath = checkRootPath(rootPath);
		if (name==null || name.isBlank())
			name = rootPath.getFileName().toString();
		return name.trim().isEmpty() ? "MyDefaultProject" : name.trim();
	}
	
	private static Path checkRootPath(Path rootPath) {
		logger.trace("{} -> checkRootPath(Path)");
		if (rootPath==null)
			rootPath = Path.of("");
		return rootPath;
	}
	
	@Override
	public String getQualifiedName() {
		logger.trace("{} -> getQualifiedName()",name);
		if (getFullName().isBlank()) return getName();
		else return "%s (%s)".formatted(getName(),getFullName());
	}
	
	// ROOT PATH
	
	public String getFullName() {
		logger.trace("{} -> getFullName()",name);
		return this.rootPath.toString();
	}
	
	public Path getRootPath() {
		logger.trace("{} -> getRootPath()",name);
		return this.rootPath;
	}
	
	// PACKAGES
	
	public Map<String,PackageInfo> getMappedPackages() {
		logger.trace("{} -> getMappedPackages()",name);
		return Collections.unmodifiableMap(packages);
	}
	
	public Map<String,PackageInfo> copyMappedPackages() {
		logger.trace("{} -> copyMappedPackages()",name);
		return new HashMap<>(packages);
	}
	
	public List<PackageInfo> getPackages() {
		logger.trace("{} -> getPackages()",name);
		return Collections.unmodifiableList(copyPackages());
	}
	
	public List<PackageInfo> copyPackages() {
		logger.trace("{} -> copyPackages()",name);
		return new ArrayList<>(packages.values());
	}
	
	public boolean addPackage(PackageInfo packageInfo) {
		logger.trace("{} -> addPackage(PackageInfo)",name);
		if (packageInfo!=null && packages.putIfAbsent(packageInfo.getName(), packageInfo)==null) {
			logger.debug("Package added: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllPackages(List<PackageInfo> packageInfos) {
		logger.trace("{} -> addAllPackages(List<PackageInfo>)",name);
		return packageInfos.stream().filter(p -> addPackage(p)).count() > 0;
	}
	
	public boolean removePackage(PackageInfo packageInfo) {
		logger.trace("{} -> removePackage(PackageInfo)",name);
		if (packageInfo!=null && packages.remove(packageInfo.getName())!=null) {
			logger.debug("Package removed: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllPackages(List<PackageInfo> packageInfos) {
		logger.trace("{} -> removeAllPackages(List<PackageInfo>)",name);
		return packageInfos.stream().filter(p -> removePackage(p)).count() > 0;
	}
	
	public PackageInfo getPackage(String name) {
		logger.trace("{} -> getPackage(String)",name);
		return packages.get(name);
	}
	
	public boolean hasPackage(String name) {
		logger.trace("{} -> hasPackage(String)",name);
		return getPackage(name) != null;
	}
	
	public boolean hasPackages() {
		logger.trace("{} -> hasPackages()",name);
		return !this.packages.isEmpty();
	}
	
	public void clearPackages() {
		logger.trace("{} -> clearPackages()",name);
		packages.clear();
		logger.debug("All Packages removed from: "+getName());
	}
	
	public List<String> getPackagesName() {
		logger.trace("{} -> getPackagesName()",name);
		return Collections.unmodifiableList(copyPackagesName());
	}
	
	public List<String> copyPackagesName() {
		logger.trace("{} -> copyPackagesName()",name);
		return new ArrayList<>(packages.keySet());
	}
	
	// METRICS
	
	public ProjectMetrics getMetrics() {
		logger.trace("{} -> getMetrics()",name);
		return metrics;
	}
	
	// UTILITIES
    
    public void buildPackagesHierarchy() {
		logger.trace("{} -> buildPackageHierarchy()",name);
        List<PackageInfo> sortedPackageNames = copyPackages();
        sortedPackageNames.sort(Comparator.comparingInt(p -> p.getDepth()));
        
        for (PackageInfo child : sortedPackageNames) {
        	String packageName = child.getName();
            if (packageName.isEmpty()) continue;
            
            int lastDot = packageName.lastIndexOf('.');
            if (lastDot > 0) {
                String parentName = packageName.substring(0, lastDot);
                PackageInfo parent = this.packages.get(parentName);
                child.setParentPackage(parent);
                if (parent != null && child != null) {
                    parent.addSubPackage(child);
				}
			}
		}
	}
    
    public void buildCompilationUnitsAssociation(Map<String,CompilationUnit> units) {
		logger.trace("{} -> buildCompilationUnitsAssocation(Map<String,CompilationUnit>)",name);
    	for (Map.Entry<String,CompilationUnit> entry : units.entrySet()) {
    		String fullName = entry.getKey();
    		String packageName = fullName.substring(0, fullName.lastIndexOf("."));
    		PackageInfo packageInfo = packages.get(packageName);
    		if (packageInfo != null)
    			packageInfo.addUnit(fullName,entry.getValue());
    	}
    }
    
    public List<CompilationUnit> getAllUnits() {
		logger.trace("{} -> getAllUnits()",name);
    	return getPackages().stream().flatMap(p -> p.getUnits().stream()).toList();
    }
    
    public List<ClassInfo> getAllClasses() {
		logger.trace("{} -> getAllClasses()",name);
    	return getPackages().stream().flatMap(p -> p.getClasses().stream()).toList();
    }
    
    public List<MethodInfo> getAllMethods() {
		logger.trace("{} -> getAllMethods()",name);
    	return getPackages().stream().flatMap(p -> p.getAllMethods().stream()).toList();
    }
    
    public List<FieldInfo> getAllFields() {
		logger.trace("{} -> getAllFields()",name);
    	return getPackages().stream().flatMap(p -> p.getAllFields().stream()).toList();
    }
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ProjectInfo that = (ProjectInfo) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.getFullName(), that.getFullName()) &&
			Objects.equals(this.packages.size(), that.packages.size())
		;
	}
	
	@Override
	public int hashCode() {
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(name, getFullName(), packages.size());
	}
	
	@Override
    public String toString() {
		logger.trace("{} -> toString()",name);
        return (this.getClass().getSimpleName()+"{"
        		+ "name=%s, "
        		+ "root=%s, "
        		+ "packages=%d, "
        		+ "classes=%d, "
        		+ "compilationUnits=%d}")
        		.formatted(
        			name,
        			getFullName(),
        			packages.size(),
        			getAllClasses().size(),
        			getAllUnits().size()
        		);
    }
	
	public String toStringTable() {
		logger.trace("{} -> toStringTable()",name);
		return TableUI.titledTable(
			this.getClass().getSimpleName(),
			List.of(
				new Object[] {"Name", name},
				new Object[] {"Root", getFullName()},
				new Object[] {"Packages", packages.size()},
				new Object[] {"Units", getAllUnits().size()},
				new Object[] {"Classes", getAllClasses().size()},
				new Object[] {"Methods", getAllMethods().size()}
			),
			"="
		);
	}

}
