package com.model.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.utils.table.TableUI;

public class PackageInfo extends ProjectNode {
	
	private static final Logger logger = LoggerFactory.getLogger(PackageInfo.class);
	
	private final ProjectInfo project;
	private PackageInfo parentPackage;
	private final Map<String,PackageInfo> subPackages = new HashMap<>();
	private final List<ClassInfo> classes = new ArrayList<>();
	private final Map<String,CompilationUnit> units = new HashMap<>();

	public PackageInfo(String name, ProjectInfo project) {
		super(name);
		this.project = Objects.requireNonNull(project, "Project cannot be null for '"+name+"' package");
	}
	
	public String getName() {return name;}
	
	public String getLastName() {return name.substring(name.lastIndexOf(".")+1);}
	
	public boolean hasName() {
		return !name.isEmpty();
	}
	
	public List<String> splitPackageName() {
		if (!hasName()) return Collections.emptyList();
		return Arrays.asList(name.split("\\."));
	}
	
	public int getDepth() {
		return splitPackageName().size();
	}
	
	// PROJECT
	
	public ProjectInfo getProject() {return project;}
	
	// PARENT PACKAGE
	
	public PackageInfo getParentPackage() {return parentPackage;}
	
	protected void setParentPackage(PackageInfo value) {
		this.parentPackage = value;
	}
	
	public String getParentPackageName() {return isRoot() ? "(default)" : getParentPackage().name;}
	
	public boolean isRoot() {
		return this.parentPackage==null;
	}
	
	public boolean isChildOf(PackageInfo that) {
		return this.name.startsWith(that.name);
	}
	
	public boolean isParentOf(PackageInfo that) {
		return that.isChildOf(this);
	}
	
	public List<PackageInfo> getAllAncestors() {
		List<PackageInfo> result = new ArrayList<>();
		PackageInfo current = parentPackage;
		while (current!=null) {
			result.add(current);
			current = current.parentPackage;
		}
		return result;
	}
	
	// SUB-PACKAGES
	
	public List<PackageInfo> getSubPackages() {return Collections.unmodifiableList(copySubPackages());}
	
	public List<PackageInfo> copySubPackages() {return new ArrayList<>(subPackages.values());}
	
	public boolean addSubPackage(PackageInfo subPackage) {
		if (subPackage!=null && this.subPackages.putIfAbsent(subPackage.getName(), subPackage)==null) {
			logger.debug("Sub-package added: %s".formatted(subPackage));
			return true;
		}
		return false;
	}
	
	public boolean addAllSubPackages(List<PackageInfo> subPackages) {
		return subPackages.stream().filter(p -> addSubPackage(p)).count() > 0;
	}
	
	public boolean removeSubPackage(PackageInfo subPackage) {
		if (subPackage!=null && subPackages.remove(subPackage.getName())!=null) {
			logger.debug("Sub-package removed: %s".formatted(subPackage));
			return true;
		}
		return false;
	}
	
	public boolean removeAllSubPackages(List<PackageInfo> subPackages) {
		return subPackages.stream().filter(p -> removeSubPackage(p)).count() > 0;
	}
	
	public PackageInfo getSubPackage(String name) {
		return subPackages.get(name);
	}
	
	public boolean hasSubPackage(String name) {
		return getSubPackage(name) != null;
	}
	
	public boolean hasSubPackages() {
		return !this.subPackages.isEmpty();
	}
	
	public void clearSubPackages() {
		subPackages.clear();
		logger.debug("All Sub-packages removed from: "+getName());
	}
	
	public List<PackageInfo> getAllDescendants() {
		return copySubPackages().stream().flatMap(p -> p.getAllDescendants().stream()).toList();
	}
	
	// CLASSES
	
	public List<ClassInfo> getClasses() {return Collections.unmodifiableList(classes);}
	
	public List<ClassInfo> copyClasses() {return new ArrayList<>(classes);}
	
	public boolean addClass(ClassInfo classInfo) {
		if (classInfo!=null && !classes.contains(classInfo) && classes.add(classInfo)) {
			logger.debug("Class added: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllClasses(List<ClassInfo> classInfos) {
		return classInfos.stream().filter(c -> addClass(c)).count() > 0;
	}
	
	public boolean removeClass(ClassInfo classInfo) {
		if (classInfo!=null && classes.remove(classInfo)) {
			logger.debug("Class removed: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllClasses(List<ClassInfo> classInfos) {
		return classInfos.stream().filter(c -> removeClass(c)).count() > 0;
	}
	
	public ClassInfo getClass(String name) {
		for(ClassInfo classInfo : classes) {
			if (name.equalsIgnoreCase(classInfo.getName())) return classInfo;
		}
		return null;
	}
	
	public boolean hasClass(String name) {
		return getClass(name) != null;
	}
	
	public boolean hasClasses() {
		return !classes.isEmpty();
	}
	
	public void clearClasses() {
		classes.clear();
		logger.debug("All Classes removed from: "+getName());
	}
	
	public List<ClassInfo> getAllClasses() {
		return Stream.concat(
			copyClasses().stream(),
			copySubPackages().stream().flatMap(p -> p.getAllClasses().stream())
		).toList();
	}
	
	// COMPILATION UNITS
	
	public Map<String,CompilationUnit> getMappedUnits() {return Collections.unmodifiableMap(units);}
	
	public List<CompilationUnit> getUnits() {return Collections.unmodifiableList(copyUnits());}
	
	public List<CompilationUnit> copyUnits() {return new ArrayList<>(units.values());}
	
	public boolean addUnit(String name, CompilationUnit unit) {
		if (name!=null && !name.isBlank() && units.putIfAbsent(name,unit)==null) {
			logger.debug("Unit added: %s (%,d char)".formatted(name, unit==null?0:unit.getLength()));
			return true;
		}
		return false;
	}
	
	public boolean addAllUnits(Map<String,CompilationUnit> units) {
		return units.keySet().stream().filter(k -> addUnit(k,units.get(k))).count() > 0;
	}
	
	public boolean removeUnit(String unitName) {
		if (unitName!=null && units.remove(unitName)!=null) {
			logger.debug("Unit removed: %s".formatted(unitName));
			return true;
		}
		return false;
	}
	
	public boolean removeAllUnits(List<String> unitNames) {
		return unitNames.stream().filter(n -> removeUnit(n)).count() > 0;
	}
	
	public CompilationUnit getUnit(String name) {
		return units.get(name);
	}
	
	public boolean hasUnit(String name) {
		return getUnit(name) != null;
	}
	
	public boolean hasUnits() {
		return !units.values().stream().filter(cu -> cu!=null).toList().isEmpty();
	}
	
	public void clearUnits() {
		units.clear();
		logger.debug("All Units removed from: "+getName());
	}
	
	public List<CompilationUnit> getAllUnits() {
		return Stream.concat(
			copyUnits().stream(),
			copySubPackages().stream().flatMap(p -> p.getAllUnits().stream())
		).toList();
	}
	
	// UTILITIES
	
	public List<MethodInfo> getMethods() {
		return classes.stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	public List<MethodInfo> getAllMethods() {
		return getAllClasses().stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	public List<FieldInfo> getFields() {
		return classes.stream().flatMap(c -> c.getFields().stream()).toList();
	}
	
	public List<FieldInfo> getAllFields() {
		return getAllClasses().stream().flatMap(c -> c.getFields().stream()).toList();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		PackageInfo that = (PackageInfo) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.getParentPackageName(), that.getParentPackageName()) &&
			Objects.equals(this.subPackages.size(), that.subPackages.size()) &&
			Objects.equals(this.classes.size(), that.classes.size())
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, getParentPackageName(), subPackages.size(), classes.size());
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "parent=%s, "
				+ "subPackages=%d, "
				+ "classes=%d}")
				.formatted(
					name,
					getParentPackageName(),
					subPackages.size(),
					classes.size()
				);
	}
	
	public String toStringTable() {
		return TableUI.titledTable(
			this.getClass().getSimpleName(),
			List.of(
				new Object[] {"Name", name},
				new Object[] {"Parent", getParentPackageName()},
				new Object[] {"Sub-packages", subPackages.size()},
				new Object[] {"Units", units.size()},
				new Object[] {"Classes", classes.size()}
			),
			"="
		);
	}
	
	public String withUnits() {
		return units.keySet().stream()
				.map(u -> TableUI.tableDataRows(
						List.of(
							new Object[] {"Name", u},
							new Object[] {"Size", units.get(u).getLength()}
						),
						"->",
						2))
				.collect(Collectors.joining());
	}
	
}
