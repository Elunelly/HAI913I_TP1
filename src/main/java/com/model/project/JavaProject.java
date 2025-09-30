package com.model.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.model.structural.ClassInfo;

public class JavaProject {
	
	private final String name;
	private final String rootPath;
	private List<PackageInfo> packages = new ArrayList<>();
	private List<ClassInfo> classes = new ArrayList<>();
	
	public JavaProject(String name, String rootPath) {
		this.name = name;
		this.rootPath = rootPath;
	}
	
	public String getName() {return this.name;}
	
	public String getRootPath() {return this.rootPath;}
	
	public List<PackageInfo> getPackages() {return Collections.unmodifiableList(this.packages);}
	
	public List<PackageInfo> copyPackages() {return new ArrayList<>(this.packages);}
	
	public void addPackage(PackageInfo packageInfo) {
		if (packageInfo!=null && !this.packages.contains(packageInfo)) this.packages.add(packageInfo);
	}
	
	public List<ClassInfo> getClasses() {return Collections.unmodifiableList(this.classes);}
	
	public List<ClassInfo> copyClasses() {return new ArrayList<>(this.classes);}
	
	public void addClass(ClassInfo classInfo) {
		if (classInfo!=null && !this.classes.contains(classInfo)) this.classes.add(classInfo);
	}
	
	public ClassInfo findClass(String name) {
		for (ClassInfo classInfo : this.classes) {
			if (classInfo.getName().equals(name)) return classInfo;
		}
		return null;
	}

}
