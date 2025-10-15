package com.extractors;

import java.util.Collection;
import java.util.List;

import com.model.project.PackageInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;

public interface PackageMetricExtractor {
	
	Object extract(PackageInfo source);
	
	Object extractAllByPackages(Collection<PackageInfo> source);
	
	Object extractByRecursive(PackageInfo source);
	
	default List<ClassInfo> getClassesByRecursive(PackageInfo source) {
		return source.getAllClassesRecursive();
	}
	
	default List<MethodInfo> getMethodsByRecursive(PackageInfo source) {
		return source.getAllClassesRecursive().stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	default List<FieldInfo> getFieldsByRecursive(PackageInfo source) {
		return source.getAllClassesRecursive().stream().flatMap(c -> c.getFields().stream()).toList();
	}

}
