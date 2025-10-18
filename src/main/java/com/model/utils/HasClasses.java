package com.model.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;

public interface HasClasses {
	
	static final Logger logger = LoggerFactory.getLogger(HasClasses.class);
	
	List<ClassInfo> classes();
	
	default List<ClassInfo> getClasses() {return Collections.unmodifiableList(classes());}
	
	default List<ClassInfo> copyClasses() {return new ArrayList<>(classes());}
	
	default boolean addClass(ClassInfo classInfo) {
		if (classInfo!=null && !classes().contains(classInfo) && classes().add(classInfo)) {
			logger.debug("Class added: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	default boolean addAllClasses(List<ClassInfo> classes) {
		return classes.stream().filter(c -> addClass(c)).count() > 0;
	}
	
	default boolean removeClass(ClassInfo classInfo) {
		if (classInfo!=null && classes().remove(classInfo)) {
			logger.debug("Class removed: %s".formatted(classInfo));
			return true;
		}
		return false;
	}
	
	default boolean removeAllClasses(List<ClassInfo> classes) {
		return classes.stream().filter(c -> removeClass(c)).count() > 0;
	}
	
	default ClassInfo getClass(String name) {
		for(ClassInfo classInfo : classes()) {
			if (name.equalsIgnoreCase(classInfo.getName())) return classInfo;
		}
		return null;
	}
	
	default boolean hasClass(String name) {
		return getClass(name) != null;
	}
	
	default boolean hasClasses() {
		return !classes().isEmpty();
	}

}
