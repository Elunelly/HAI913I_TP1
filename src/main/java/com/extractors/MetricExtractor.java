package com.extractors;

import com.core.AnalysisResult;

public interface MetricExtractor {
	
	String getMetricName();
	
	MetricType getMetricType();
	
	Object extract(AnalysisResult source);
	
	abstract Object wrapper(Object result);
	
	default boolean isApplicable(AnalysisResult result) {
        return result != null && result.hasClasses();
	}

}
