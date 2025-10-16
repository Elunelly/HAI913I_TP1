package com.extractors;

public interface MetricExtractor<T> {
	
	String getMetricName();
	
	MetricType getMetricType();

	Object extract(T source);

}
