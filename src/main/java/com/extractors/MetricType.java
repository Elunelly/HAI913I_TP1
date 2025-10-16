package com.extractors;

import java.util.List;
import java.util.Map;

public enum MetricType {
    
	// Total count of elements
    COUNT("Count", Integer.class),
    // Average value across elements
    AVERAGE("Average", Double.class),
    // Proportion or percentage
    RATIO("Ratio", Double.class),
    // Filtered list of elements
    FILTERED("Filtered", List.class),
    // Ordered list of elements
    RANKING("Ranking", List.class),
    // Statistical distribution
    DISTRIBUTION("Distribution", Map.class),
    // Descriptive statistics
    STATISTICS("Statistics", Object.class),
    // Elements exceeding threshold
    THRESHOLD("Threshold", List.class),
    // Relational structure
    GRAPH("Graph", Object.class),
    // Boolean indicator
    FLAG("Flag", Boolean.class);
    
    
    private final String displayName;
    private final Class<?> expectedReturnType;
    
    MetricType(String displayName, Class<?> expectedReturnType) {
        this.displayName = displayName;
        this.expectedReturnType = expectedReturnType;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Class<?> getExpectedReturnType() {
        return expectedReturnType;
    }
    
    public <T> T cast(Object result, Class<T> returnType) {
    	if(!returnType.equals(expectedReturnType)) {
    		throw new IllegalStateException("Expected type: "+expectedReturnType+", not "+returnType);
    	}
    	return (T) returnType.cast(result);
    }
    
    public boolean isSingleValue() {
        return this == COUNT || this == AVERAGE || this == RATIO || this == FLAG;
    }
    
    public boolean isCollection() {
        return this == FILTERED || this == RANKING || this == DISTRIBUTION || this == THRESHOLD;
    }
    
    public boolean isComplex() {
        return this == STATISTICS || this == GRAPH;
    }
    
    public boolean isValidResult(Object result) {
        if (result == null) return false;
        return expectedReturnType.isAssignableFrom(result.getClass());
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}