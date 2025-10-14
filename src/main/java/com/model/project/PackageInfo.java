package com.model.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.interfaces.HasClasses;
import com.model.interfaces.ModelInfo;
import com.model.structural.ClassInfo;

public class PackageInfo implements ModelInfo, HasClasses {
	
	private static final Logger logger = LoggerFactory.getLogger(PackageInfo.class);
	
	private final String name;
	private PackageInfo parentPackage;
	private final Map<String,PackageInfo> subPackages = new HashMap<>();
	private final List<ClassInfo> classes = new ArrayList<>();

	public PackageInfo(String name) {
		super();
		this.name = name.trim();
	}
	
	public String getName() {return this.name;}
	
	public String getLastName() {return this.name.substring(this.name.lastIndexOf(".")+1);}
	
	public PackageInfo getParentPackage() {return this.parentPackage;}
	
	public void setParentPackage(PackageInfo parentPackage) {
		PackageInfo old = this.parentPackage;
		this.parentPackage = parentPackage;
		logger.debug("Change value of 'parentPackage': %s -> %s".formatted(old,this.parentPackage));
	}
	
	public List<PackageInfo> getSubPackages() {return Collections.unmodifiableList(new ArrayList<>(this.subPackages.values()));}
	
	public PackageInfo getSubPackage(String name) {
		return this.subPackages.get(name);
	}
	
	public void addSubPackage(PackageInfo subpackage) {
		if (subpackage!=null && this.subPackages.putIfAbsent(subpackage.getName(), subpackage)==null)
			logger.debug("Sub-package added: %s".formatted(subpackage));
	}
	
	public boolean hasSubPackage(String name) {
		return this.subPackages.containsKey(name);
	}
	
	public boolean hasSubPackages() {
		return !this.subPackages.isEmpty();
	}

	@Override
	public List<ClassInfo> classes() {return classes;}
	
	public boolean isRoot() {
		return this.parentPackage==null;
	}
	
	public boolean hasName() {
		return !this.name.isEmpty();
	}
	
	public boolean isChildOf(PackageInfo that) {
		return this.name.startsWith(that.name);
	}
	
	public boolean isParentOf(PackageInfo that) {
		return that.isChildOf(this);
	}
	
	public int getDepth() {
		return hasName() ? 
			this.name.split("\\.").length :
			0
		;
	}
	
	public List<String> splitPackageName() {
		if (!hasName()) return Collections.emptyList();
		return Arrays.asList(this.name.split("\\."));
	}
	
	public List<PackageInfo> getAllAncestors() {
		List<PackageInfo> result = new ArrayList<>();
		PackageInfo current = this.parentPackage;
		while (current!=null) {
			result.add(current);
			current = current.parentPackage;
		}
		return result;
	}
	
	public List<PackageInfo> getAllDescendants() {
		List<PackageInfo> result = new ArrayList<>();
		for (PackageInfo descendant : this.subPackages.values()) {
			result.add(descendant);
			result.addAll(descendant.getAllDescendants());
		}
		return result;
	}
	
	public List<ClassInfo> getAllClassesRecursive() {
		List<ClassInfo> result = new ArrayList<>(this.classes);
		for (PackageInfo descendant : this.subPackages.values()) {
			result.addAll(descendant.getAllClassesRecursive());
		}
		return result;
	}
	
	public boolean equals(PackageInfo that) {
		return
			this.name.equals(that.name) &&
			this.parentPackage.equals(that.parentPackage)
		;
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "parent=%s, "
				+ "subPackages=%d, "
				+ "classes=%d}")
				.formatted(name, parentPackage, subPackages.size(), classes.size());
	}
	
}
