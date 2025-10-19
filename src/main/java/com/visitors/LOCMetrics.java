package com.visitors;

import com.extractors.MetricType;

public enum LOCMetrics {
	
	FIRST_CHAR_POS	("firstCharPos"	),
	LAST_CHAR_POS	("lastCharPos"	),
	CHAR_LENGTH		("charLength"	),
	FIRST_LINE_NUM	("firstLineNum"	),
	LAST_LINE_NUM	("lastLineNum"	),
	LINE_LENGTH		("lineLength"	);
	
	private final String name;
	private static final MetricType type = MetricType.COUNT;
	
	private LOCMetrics(String name) {
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
