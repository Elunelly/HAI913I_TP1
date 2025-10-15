package com.extractors;

import java.util.List;

import com.model.structural.FieldInfo;
import com.model.structural.NodeModifiers;

public interface FieldMetricExtractor {
	
	Object extract(FieldInfo source);
	
	default List<NodeModifiers> getModifiers(FieldInfo source) {
		return source.getModifiers();
	}

}
