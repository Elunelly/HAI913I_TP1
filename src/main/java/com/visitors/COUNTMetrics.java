package com.visitors;

import com.extractors.MetricType;

public enum COUNTMetrics {
	PACKAGES	("packages"	),
	CLASSES		("classes"	),
	METHODS		("methods"	),
	FIELDS		("fields"	);
	
	private final String name;
	private static final MetricType type = MetricType.COUNT;
	
	private COUNTMetrics(String name) {
		this.name = name;
	}
	
	public String getName() {return name;}
	
	public MetricType getType() {return type;}
	
	public Class<?> getReturnType() {return type.getExpectedReturnType();}
	
	@Override
	public String toString() {
		return name;
	}
}
