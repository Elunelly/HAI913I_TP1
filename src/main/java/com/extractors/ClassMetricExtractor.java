package com.extractors;

import java.util.Collection;
import java.util.List;

import com.model.structural.ClassInfo;
import com.model.structural.NodeModifiers;

public interface ClassMetricExtractor {
	
	Object extract(ClassInfo source);
	
	Object extractAllByClasses(Collection<ClassInfo> source);
	
	default List<NodeModifiers> getModifiers(ClassInfo source) {
		return source.getModifiers();
	}

}
