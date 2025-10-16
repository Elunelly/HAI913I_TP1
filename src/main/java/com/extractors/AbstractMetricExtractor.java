package com.extractors;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMetricExtractor<T> implements MetricExtractor<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractMetricExtractor.class);
	
	protected final String metricName;
	protected final MetricType type;
	
	protected AbstractMetricExtractor(String metricName, MetricType type) {
		this.metricName = Objects.requireNonNull(metricName, "Metric name cannot be null");
		this.type = Objects.requireNonNull(type, "Metric type is required");
	}
	
	@Override
	public String getMetricName() {return metricName;}
	
	@Override
	public MetricType getMetricType() {return type;}
	
	public final Object extractWithLogging(T source) {
		Objects.requireNonNull(source, "Source cannot be null for: "+metricName);
		try {
			long timeStart = System.currentTimeMillis();
			Object result = extract(source);
			long duration = System.currentTimeMillis() - timeStart;
			logger.debug("Extracted metric '{}': {} ({}ms)", metricName, result, duration);
			return result;
		} catch(Exception e) {
			logger.error("Failed extracting metric '{}': {}", metricName, e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		AbstractMetricExtractor<?> that = (AbstractMetricExtractor<?>) obj;
		return 
			Objects.equals(this.metricName, that.metricName) &&
			Objects.equals(this.type, that.type)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(metricName, type);
	}

}
