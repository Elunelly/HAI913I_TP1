package com.core;

import java.util.List;
import java.util.Map;

import com.model.project.JavaProject;
import com.model.structural.ClassInfo;
import com.model.structural.MethodCall;

public class AnalysisResult {
	
	private JavaProject project;
	private Map<String,Object> metrics;
	private List<ClassInfo> classes;
	private List<MethodCall> methodCalls;
	
	public boolean addMetric(String name, Object value) {
		// TODO
		return this.metrics.put(name, value) != null;
	}
	
	public Object getMetric(String name) {
		// TODO
		return this.metrics.get(name);
	}
	
	public JavaProject getProject() {
		// TODO
		return this.project;
	}

}
