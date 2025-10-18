package com.extractors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.core.AnalysisResult;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;

public final class ModelNavigator {
	
	public static List<PackageInfo> getPackages(Object source) {
		return switch(source) {
			case null -> Collections.emptyList();
			case AnalysisResult r -> r.getProject().getPackages();
			case ProjectInfo p -> p.getPackages();
			default -> Collections.emptyList();
		};
	}
	
	public static List<PackageInfo> getAllPackages(Collection<?> sources) {
		return sources.stream().flatMap(s -> getPackages(s).stream()).distinct().toList();
	}
	
	public static List<ClassInfo> getClasses(Object source) {
		return switch(source) {
			case null -> Collections.emptyList();
			case AnalysisResult r -> r.getClasses();
			case ProjectInfo p -> p.getAllClasses();
			case PackageInfo p -> p.getAllClasses();
			case ClassInfo c-> Collections.unmodifiableList(List.of(c));
			case Collection<?> sources -> sources.stream().flatMap(s -> getClasses(s).stream()).distinct().toList();
			default -> Collections.emptyList();
		};
	}
	
	public static List<ClassInfo> getDirectClasses(PackageInfo source) {
		return source==null ? Collections.emptyList() : source.getClasses();
	}
	
	public static List<MethodInfo> getMethods(Object source) {
		return switch(source) {
			case null -> Collections.emptyList();
			case ClassInfo c -> c.getMethods();
			case MethodInfo m -> Collections.unmodifiableList(List.of(m));
			case Collection<?> sources -> sources.stream().flatMap(s -> getMethods(s).stream()).distinct().toList();
			default -> getClasses(source).stream().flatMap(c -> c.getMethods().stream()).toList();
		};
	}
	
	public static List<FieldInfo> getFields(Object source) {
		return switch(source) {
			case null -> Collections.emptyList();
			case ClassInfo c -> c.getFields();
			case FieldInfo m -> Collections.unmodifiableList(List.of(m));
			case Collection<?> sources -> sources.stream().flatMap(s -> getFields(s).stream()).distinct().toList();
			default -> getClasses(source).stream().flatMap(c -> c.getFields().stream()).toList();
		};
	}
	
	public static int count(Object source) {
		return switch(source) {
			case null -> 0;
			case Collection<?> c -> c.size();
			case Object[] a -> a.length;
			default -> 0;
		};
	}

}
