package com.model.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.MethodInfo;

public interface HasMethods {
	
	static final Logger logger = LoggerFactory.getLogger(HasMethods.class);
	
	List<MethodInfo> methods();
	
	default List<MethodInfo> getMethods() {return Collections.unmodifiableList(methods());}
	
	default List<MethodInfo> copyMethods() {return new ArrayList<>(methods());}
	
	default boolean addMethod(MethodInfo methodInfo) {
		if (methodInfo!=null && !methods().contains(methodInfo) && methods().add(methodInfo)) {
			logger.debug("Method added: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	default boolean addAllMethods(List<MethodInfo> methods) {
		return methods.stream().filter(c -> addMethod(c)).count() > 0;
	}
	
	default boolean removeMethod(MethodInfo methodInfo) {
		if (methodInfo!=null && methods().remove(methodInfo)) {
			logger.debug("Method removed: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	default boolean removeAllMethods(List<MethodInfo> methods) {
		return methods.stream().filter(c -> removeMethod(c)).count() > 0;
	}
	
	default MethodInfo getMethod(String name) {
		for(MethodInfo methodInfo : methods()) {
			if (name.equalsIgnoreCase(methodInfo.getName())) return methodInfo;
		}
		return null;
	}
	
	default boolean hasMethod(String name) {
		return getMethod(name) != null;
	}
	
	default boolean hasMethods() {
		return !methods().isEmpty();
	}

}
