package com.model.project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
		logger.debug("Setting project name: "+this.name);
		this.rootPath = checkRootPath(rootPath);
		logger.debug("Setting project root: "+this.rootPath);
		this.metrics = new ProjectMetrics(getQualifiedName());
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
	
	private static String checkName(String name, Path rootPath) {
		rootPath = checkRootPath(rootPath);
		if (name==null || name.isBlank())
			name = rootPath.getFileName().toString();
		return name.trim().isEmpty() ? "MyDefaultProject" : name.trim();
	}
	
	private static Path checkRootPath(Path rootPath) {
		if (rootPath==null)
			rootPath = Path.of("");
		return rootPath;
	}
	
	@Override
	public String getQualifiedName() {
		if (getFullName().isBlank()) return getName();
		else return "%s (%s)".formatted(getName(),getFullName());
	}
	
	// ROOT PATH
	
	public String getFullName() {return this.rootPath.toString();}
	
	public Path getRootPath() {return this.rootPath;}
	
	// PACKAGES
	
	public Map<String,PackageInfo> getMappedPackages() {return Collections.unmodifiableMap(packages);}
	
	public Map<String,PackageInfo> copyMappedPackages() {return new HashMap<>(packages);}
	
	public List<PackageInfo> getPackages() {return Collections.unmodifiableList(copyPackages());}
	
	public List<PackageInfo> copyPackages() {return new ArrayList<>(packages.values());}
	
	public boolean addPackage(PackageInfo packageInfo) {
		if (packageInfo!=null && packages.putIfAbsent(packageInfo.getName(), packageInfo)==null) {
			logger.debug("Package added: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllPackages(List<PackageInfo> packageInfos) {
		return packageInfos.stream().filter(p -> addPackage(p)).count() > 0;
	}
	
	public boolean removePackage(PackageInfo packageInfo) {
		if (packageInfo!=null && packages.remove(packageInfo.getName())!=null) {
			logger.debug("Package removed: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllPackages(List<PackageInfo> packageInfos) {
		return packageInfos.stream().filter(p -> removePackage(p)).count() > 0;
	}
	
	public PackageInfo getPackage(String name) {
		return packages.get(name);
	}
	
	public boolean hasPackage(String name) {
		return getPackage(name) != null;
	}
	
	public boolean hasPackages() {
		return !this.packages.isEmpty();
	}
	
	public void clearPackages() {
		packages.clear();
		logger.debug("All Packages removed from: "+getName());
	}
	
	public List<String> getPackagesName() {return Collections.unmodifiableList(copyPackagesName());}
	
	public List<String> copyPackagesName() {return new ArrayList<>(packages.keySet());}
	
	// METRICS
	
	public ProjectMetrics getMetrics() {return metrics;}
	
	// UTILITIES
    
    public void buildPackagesHierarchy() {
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
    	logger.debug("\n"+packages.keySet().stream().collect(Collectors.joining("\n")));
    	for (Map.Entry<String,CompilationUnit> entry : units.entrySet()) {
    		String fullName = entry.getKey();
    		String packageName = fullName.substring(0, fullName.lastIndexOf("."));
    		logger.debug("fullName={}, packageName={} ({})",fullName,packageName,packages.containsKey(packageName));
    		PackageInfo packageInfo = packages.get(packageName);
    		if (packageInfo != null)
    			packageInfo.addUnit(fullName,entry.getValue());
    	}
    }
    
    public List<CompilationUnit> getAllUnits() {
    	return getPackages().stream().flatMap(p -> p.getUnits().stream()).toList();
    }
    
    public List<ClassInfo> getAllClasses() {
    	return getPackages().stream().flatMap(p -> p.getClasses().stream()).toList();
    }
    
    public List<MethodInfo> getAllMethods() {
    	return getPackages().stream().flatMap(p -> p.getAllMethods().stream()).toList();
    }
    
    public List<FieldInfo> getAllFields() {
    	return getPackages().stream().flatMap(p -> p.getAllFields().stream()).toList();
    }
	
	@Override
	public boolean equals(Object obj) {
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
		return Objects.hash(name, getFullName(), packages.size());
	}
	
	@Override
    public String toString() {
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
