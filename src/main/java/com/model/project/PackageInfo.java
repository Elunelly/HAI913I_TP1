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

import com.model.metrics.PackageMetrics;
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
	
	private final PackageMetrics metrics;

	public PackageInfo(String name, ProjectInfo project) {
		super(name);
		logger.trace("{} -> PackageInfo(String,ProjectInfo)",this.name);
		this.project = Objects.requireNonNull(project, "Project cannot be null for '"+name+"' package");
		this.metrics = new PackageMetrics(getQualifiedName());
	}
	
	@Override
	public String getQualifiedName() {
		logger.trace("{} -> getQualifiedName()",name);
		if (project.getName().isBlank()) return getName();
		else return "%s:%s".formatted(project.getName(),getName());
	}
	
	public String getLastName() {
		logger.trace("{} -> getLastName()",name);
		return name.substring(name.lastIndexOf(".")+1);
	}
	
	public boolean hasName() {
		logger.trace("{} -> hasName()",name);
		return !name.isEmpty();
	}
	
	public List<String> splitPackageName() {
		logger.trace("{} -> splitPackageName()",name);
		if (!hasName()) return Collections.emptyList();
		return Arrays.asList(name.split("\\."));
	}
	
	public int getDepth() {
		logger.trace("{} -> getDepth()",name);
		return splitPackageName().size();
	}
	
	// PROJECT
	
	public ProjectInfo getProject() {
		logger.trace("{} -> getProject()",name);
		return project;
	}
	
	// PARENT PACKAGE
	
	public PackageInfo getParentPackage() {
		logger.trace("{} -> getParentPackage()",name);
		return parentPackage;
	}
	
	protected void setParentPackage(PackageInfo value) {
		logger.trace("{} -> setParentPackage(PackageInfo)",name);
		this.parentPackage = value;
	}
	
	public String getParentPackageName() {
		logger.trace("{} -> getParentPackageName()",name);
		return isRoot() ? "(default)" : getParentPackage().name;
	}
	
	public boolean isRoot() {
		logger.trace("{} -> isRoot()",name);
		return this.parentPackage==null;
	}
	
	public boolean isChildOf(PackageInfo that) {
		logger.trace("{} -> isChildOf(PackageInfo)",name);
		return this.name.startsWith(that.name);
	}
	
	public boolean isParentOf(PackageInfo that) {
		logger.trace("{} -> isParentOf(PackageInfo)",name);
		return that.isChildOf(this);
	}
	
	public List<PackageInfo> getAllAncestors() {
		logger.trace("{} -> getAllAncestors()",name);
		List<PackageInfo> result = new ArrayList<>();
		PackageInfo current = parentPackage;
		while (current!=null) {
			result.add(current);
			current = current.parentPackage;
		}
		return result;
	}
	
	// SUB-PACKAGES
	
	public List<PackageInfo> getSubPackages() {
		logger.trace("{} -> getSubPackages()",name);
		return Collections.unmodifiableList(copySubPackages());
	}
	
	public List<PackageInfo> copySubPackages() {
		logger.trace("{} -> copySubPackages()",name);
		return new ArrayList<>(subPackages.values());
	}
	
	public boolean addSubPackage(PackageInfo subPackage) {
		logger.trace("{} -> addSubPackage(PackageInfo)",name);
		if (subPackage!=null && this.subPackages.putIfAbsent(subPackage.getName(), subPackage)==null) {
			logger.debug("Sub-package added: %s".formatted(subPackage));
			return true;
		}
		return false;
	}
	
	public boolean addAllSubPackages(List<PackageInfo> subPackages) {
		logger.trace("{} -> addAllSubPackages(List<PackageInfo>)",name);
		return subPackages.stream().filter(p -> addSubPackage(p)).count() > 0;
	}
	
	public boolean removeSubPackage(PackageInfo subPackage) {
		logger.trace("{} -> removeSubPackage(PackageInfo)",name);
		if (subPackage!=null && subPackages.remove(subPackage.getName())!=null) {
			logger.debug("Sub-package removed: %s".formatted(subPackage));
			return true;
		}
		return false;
	}
	
	public boolean removeAllSubPackages(List<PackageInfo> subPackages) {
		logger.trace("{} -> removeAllSubPackages(List<PackageInfo>)",name);
		return subPackages.stream().filter(p -> removeSubPackage(p)).count() > 0;
	}
	
	public PackageInfo getSubPackage(String name) {
		logger.trace("{} -> getSubPackage(String)",name);
		return subPackages.get(name);
	}
	
	public boolean hasSubPackage(String name) {
		logger.trace("{} -> hasSubPackage(String)",name);
		return getSubPackage(name) != null;
	}
	
	public boolean hasSubPackages() {
		logger.trace("{} -> hasSubPackages()",name);
		return !this.subPackages.isEmpty();
	}
	
	public void clearSubPackages() {
		logger.trace("{} -> clearSubPackages()",name);
		subPackages.clear();
		logger.debug("All Sub-packages removed from: "+getName());
	}
	
	public List<PackageInfo> getAllDescendants() {
		logger.trace("{} -> getAllDescendants()",name);
		return copySubPackages().stream().flatMap(p -> p.getAllDescendants().stream()).toList();
	}
	
	// CLASSES
	
	public List<ClassInfo> getClasses() {
		logger.trace("{} -> getClasses()",name);
		return Collections.unmodifiableList(classes);
	}
	
	public List<ClassInfo> copyClasses() {
		logger.trace("{} -> copyClasses()",name);
		return new ArrayList<>(classes);
	}
	
	public boolean addClass(ClassInfo classInfo) {
		logger.trace("{} -> addClass(ClassInfo)",name);
		if (classInfo!=null && !classes.contains(classInfo) && classes.add(classInfo)) {
			logger.debug("Class added: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllClasses(List<ClassInfo> classInfos) {
		logger.trace("{} -> addAllClasses(List<ClassInfo>)",name);
		return classInfos.stream().filter(c -> addClass(c)).count() > 0;
	}
	
	public boolean removeClass(ClassInfo classInfo) {
		logger.trace("{} -> removeClass(ClassInfo)",name);
		if (classInfo!=null && classes.remove(classInfo)) {
			logger.debug("Class removed: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllClasses(List<ClassInfo> classInfos) {
		logger.trace("{} -> removeAllClasses(List<ClassInfo>)",name);
		return classInfos.stream().filter(c -> removeClass(c)).count() > 0;
	}
	
	public ClassInfo getClass(String name) {
		logger.trace("{} -> getClass(String)",name);
		for(ClassInfo classInfo : classes) {
			if (name.equalsIgnoreCase(classInfo.getName())) return classInfo;
		}
		return null;
	}
	
	public boolean hasClass(String name) {
		logger.trace("{} -> hasClass(String)",name);
		return getClass(name) != null;
	}
	
	public boolean hasClasses() {
		logger.trace("{} -> hasClasses()",name);
		return !classes.isEmpty();
	}
	
	public void clearClasses() {
		logger.trace("{} -> clearClasses()",name);
		classes.clear();
		logger.debug("All Classes removed from: "+getName());
	}
	
	public List<ClassInfo> getAllClasses() {
		logger.trace("{} -> getAllClasses()",name);
		return Stream.concat(
			copyClasses().stream(),
			copySubPackages().stream().flatMap(p -> p.getAllClasses().stream())
		).toList();
	}
	
	// COMPILATION UNITS
	
	public Map<String,CompilationUnit> getMappedUnits() {
		logger.trace("{} -> getMappedUnits()",name);
		return Collections.unmodifiableMap(units);
	}
	
	public List<CompilationUnit> getUnits() {
		logger.trace("{} -> getUnits()",name);
		return Collections.unmodifiableList(copyUnits());
	}
	
	public List<CompilationUnit> copyUnits() {
		logger.trace("{} -> copyUnits()",name);
		return new ArrayList<>(units.values());
	}
	
	public boolean addUnit(String name, CompilationUnit unit) {
		logger.trace("{} -> addUnit(String,CompilationUnit)",name);
		if (name!=null && !name.isBlank() && units.putIfAbsent(name,unit)==null) {
			logger.debug("Unit added: %s (%,d char)".formatted(name, unit==null?0:unit.getLength()));
			return true;
		}
		return false;
	}
	
	public boolean addAllUnits(Map<String,CompilationUnit> units) {
		logger.trace("{} -> addAllUnits(Map<String,CompilationUnit>)",name);
		return units.keySet().stream().filter(k -> addUnit(k,units.get(k))).count() > 0;
	}
	
	public boolean removeUnit(String unitName) {
		logger.trace("{} -> removeUnit(String)",name);
		if (unitName!=null && units.remove(unitName)!=null) {
			logger.debug("Unit removed: %s".formatted(unitName));
			return true;
		}
		return false;
	}
	
	public boolean removeAllUnits(List<String> unitNames) {
		logger.trace("{} -> removeAllUnits(List<String>)",name);
		return unitNames.stream().filter(n -> removeUnit(n)).count() > 0;
	}
	
	public CompilationUnit getUnit(String name) {
		logger.trace("{} -> getUnit(String)",name);
		return units.get(name);
	}
	
	public boolean hasUnit(String name) {
		logger.trace("{} -> hasUnit(String)",name);
		return getUnit(name) != null;
	}
	
	public boolean hasUnits() {
		logger.trace("{} -> hasUnits()",name);
		return !units.values().stream().filter(cu -> cu!=null).toList().isEmpty();
	}
	
	public void clearUnits() {
		logger.trace("{} -> clearUnits()",name);
		units.clear();
		logger.debug("All Units removed from: "+getName());
	}
	
	public List<CompilationUnit> getAllUnits() {
		logger.trace("{} -> getAllUnits()",name);
		return Stream.concat(
			copyUnits().stream(),
			copySubPackages().stream().flatMap(p -> p.getAllUnits().stream())
		).toList();
	}
	
	// METRICS
	
	public PackageMetrics getMetrics() {
		logger.trace("{} -> getMetrics()",name);
		return metrics;
	}
	
	// UTILITIES
	
	public List<MethodInfo> getMethods() {
		logger.trace("{} -> getMethods()",name);
		return classes.stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	public List<MethodInfo> getAllMethods() {
		logger.trace("{} -> getAllMethods()",name);
		return getAllClasses().stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	public List<FieldInfo> getFields() {
		logger.trace("{} -> getFields()",name);
		return classes.stream().flatMap(c -> c.getFields().stream()).toList();
	}
	
	public List<FieldInfo> getAllFields() {
		logger.trace("{} -> getAllFields()",name);
		return getAllClasses().stream().flatMap(c -> c.getFields().stream()).toList();
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
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
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(name, getParentPackageName(), subPackages.size(), classes.size());
	}
	
	@Override
	public String toString() {
		logger.trace("{} -> toString()",name);
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
		logger.trace("{} -> toStringTable()",name);
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
		logger.trace("{} -> withUnits()",name);
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
