package com.model.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.model.structural.ClassInfo;

public class PackageInfo {
	
	private final String name;
	private List<ClassInfo> classes = new ArrayList<>();

	public PackageInfo(String name) {
		this.name = name;
	}
	
	public String getName() {return this.name;}
	
	public List<ClassInfo> getClasses() {return Collections.unmodifiableList(this.classes);}
	
	public List<ClassInfo> copyClasses() {return new ArrayList<>(this.classes);}
	
	public void addClass(ClassInfo classInfo) {
		if (classInfo!=null && !this.classes.contains(classInfo)) this.classes.add(classInfo);
	}
	
	@Override
	public String toString() {
		return "Package{name=%s, methods=%d}"
				.formatted(getName(),getClasses().size());
	}
	
}
