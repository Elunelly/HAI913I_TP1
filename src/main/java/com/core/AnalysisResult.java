package com.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.JavaProject;
import com.model.structural.ClassInfo;

public class AnalysisResult {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisResult.class);
	
	private final JavaProject project;
	private final Map<String,Object> metrics = new HashMap<>();
	//private final List<MethodCall> methodCalls = new ArrayList<>();
	
	public AnalysisResult(JavaProject project) {
		this.project = project;
	}
	
	public Map<String,Object> getMetrics() {return Collections.unmodifiableMap(metrics);}
	
	public Map<String,Object> copyMetrics() {return new HashMap<>(metrics);}
	
	public void addMetric(String name, Object value) {
		Object old = metrics.get(name);
		metrics.put(name, value);
		if (old==null)
			logger.debug("Added metric '"+name+"': "+value.toString());
		else
			logger.debug("Change value of '"+name+"': %s -> %s".formatted(old,metrics.get(name)));
	}
	
	public void addAllMetrics(Map<String,Object> metrics) {
		for (Entry<String,Object> metric : metrics.entrySet()) {
			addMetric(metric.getKey(),metric.getValue());
		}
	}
	
	public Object getMetric(String name) {
		return this.metrics.get(name);
	}
	
	public boolean hasMetric(String name) {
		return metrics.containsKey(name);
	}
	
	public boolean hasMetrics() {
		return !metrics.isEmpty();
	}
	
	public JavaProject getProject() {
		return this.project;
	}
	
	public List<ClassInfo> getClasses() {return project.getClasses();}
	
	public int getTotalPackagesCount() {return project.getPackages().size();}
	
	public int getTotalClassesCount() {return getClasses().size();}
	
	public int getTotalMethodsCount() {return project.getMethods().size();}
	
	public int getTotalFieldsCount() {return project.getFields().size();}
	
	@Override
	public String toString() {
		return ("AnalysisResult{"
				+ "projectName=%s, "
				+ "metrics=%d, "
				+ "classes=%d}")
				.formatted(project.getName(),metrics.size(),getTotalClassesCount());
	}

}
