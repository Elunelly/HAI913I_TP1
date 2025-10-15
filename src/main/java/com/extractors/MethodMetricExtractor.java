package com.extractors;

import java.util.List;

import com.model.structural.MethodInfo;
import com.model.structural.NodeModifiers;

public interface MethodMetricExtractor {
	
	Object extract(MethodInfo source);
	
	default List<String> getParameters(MethodInfo source) {
		return source.getParameters();
	}
	
	default List<NodeModifiers> getModifiers(MethodInfo source) {
		return source.getModifiers();
	}

}
