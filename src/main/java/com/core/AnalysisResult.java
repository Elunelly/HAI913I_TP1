package com.core;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;

public class AnalysisResult {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisResult.class);
	
	private final ProjectInfo project;
//	private final Map<String,Object> metrics = new HashMap<>();
//	private final List<MethodCall> methodCalls = new ArrayList<>();
	
	public AnalysisResult(ProjectInfo project) {
		this.project = project;
	}
	
//	public Map<String,Object> getMetrics() {return Collections.unmodifiableMap(metrics);}
//	
//	public Map<String,Object> copyMetrics() {return new HashMap<>(metrics);}
//	
//	public void addMetric(String name, Object value) {
//		Object old = metrics.get(name);
//		metrics.put(name, value);
//		if (old==null)
//			logger.debug("Added metric '"+name+"': "+value.toString());
//		else
//			logger.debug("Change value of '"+name+"': %s -> %s".formatted(old,metrics.get(name)));
//	}
//	
//	public void addAllMetrics(Map<String,Object> metrics) {
//		for (Entry<String,Object> metric : metrics.entrySet()) {
//			addMetric(metric.getKey(),metric.getValue());
//		}
//	}
//	
//	public Object getMetric(String name) {
//		return this.metrics.get(name);
//	}
//	
//	public boolean hasMetric(String name) {
//		return metrics.containsKey(name);
//	}
//	
//	public boolean hasMetrics() {
//		return !metrics.isEmpty();
//	}
	
	public ProjectInfo getProject() {
		return this.project;
	}
	
	public List<ClassInfo> getClasses() {return project.getAllClasses();}
	
	public boolean hasClasses() {return getClasses().isEmpty();}
	
	public int getTotalPackagesCount() {return project.getPackages().size();}
	
	public int getTotalClassesCount() {return getClasses().size();}
	
	public int getTotalMethodsCount() {return project.getAllMethods().size();}
	
	public int getTotalFieldsCount() {return project.getAllFields().size();}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		AnalysisResult that = (AnalysisResult) obj;
		return 
			Objects.equals(this.project, that.project)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(project);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "projectName=%s, "
				+ "classes=%d, "
				+ "methods=%d, "
				+ "fields=%d}")
				.formatted(
					project.getName(),
					getTotalClassesCount(),
					getTotalMethodsCount(),
					getTotalFieldsCount()
				);
	}

}
