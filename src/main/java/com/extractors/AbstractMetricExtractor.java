package com.extractors;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.model.interfaces.HasClasses;
import com.model.interfaces.HasFields;
import com.model.interfaces.HasMethods;
import com.model.interfaces.HasPackages;
import com.model.project.PackageInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;

public abstract class AbstractMetricExtractor implements MetricExtractor {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractMetricExtractor.class);
	
	protected final String metricName;
	protected final MetricType type;
	
	protected AbstractMetricExtractor(String metricName, MetricType type) {
		this.metricName = metricName;
		this.type = type;
	}
	
	@Override
	public String getMetricName() {return metricName;}
	
	@Override
	public MetricType getMetricType() {return type;}
	
	protected List<PackageInfo> getPackages(AnalysisResult source) {
		return source.getProject().getPackages();
	}
	
	protected <T extends HasPackages> List<PackageInfo> getPackages(T source) {
		return source.getPackages();
	}
	
	protected <T extends HasPackages> List<PackageInfo> getAllPackages(Collection<T> source) {
		return source.stream().flatMap(s -> getPackages(s).stream()).toList();
	}
	
	protected List<ClassInfo> getClasses(AnalysisResult source) {
		return source.getClasses();
	}
	
	protected <T extends HasClasses> List<ClassInfo> getClasses(T source) {
		return source.getClasses();
	}
	
	protected <T extends HasClasses> List<ClassInfo> getAllClasses(Collection<T> source) {
		return source.stream().flatMap(s -> getClasses(s).stream()).toList();
	}
	
	protected List<MethodInfo> getMethods(AnalysisResult source) {
		return source.getClasses().stream().flatMap(c -> c.getMethods().stream()).toList();
	}
	
	protected <T extends HasMethods> List<MethodInfo> getMethods(T source) {
		return source.getMethods();
	}
	
	protected <T extends HasMethods> List<MethodInfo> getAllMethods(Collection<T> source) {
		return source.stream().flatMap(s -> getMethods(s).stream()).toList();
	}
	
	protected List<FieldInfo> getFields(AnalysisResult source) {
		return source.getClasses().stream().flatMap(c -> c.getFields().stream()).toList();
	}
	
	protected <T extends HasFields> List<FieldInfo> getFields(T source) {
		return source.getFields();
	}
	
	protected <T extends HasFields> List<FieldInfo> getAllFields(Collection<T> source) {
		return source.stream().flatMap(s -> getFields(s).stream()).toList();
	}
	
	@Override
	public Object wrapper(Object result) {
        logger.debug("Extracted metric '{}': {}", getMetricName(), result);
        return result;
	}

}
