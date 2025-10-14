package com.model.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.ProjectInfo;
import com.utils.MetricsUtils;

public class ProjectMetrics extends NodeMetrics<ProjectInfo> {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectMetrics.class);
	
	private int totalPackages;
	private int totalClasses;
	private int totalMethods;
	private int totalFields;
	
	private double avgClassesPerPackage;
	private double avgMethodsPerClass;
	private double avgFieldsPerClass;
	
	private double avgLOCPerPackage;
	private double avgLOCPerClass;
	private double avgLOCPerMethod;
	
	public ProjectMetrics(String name) {
		super(name);
	}

	@Override
	protected void doCalculate(ProjectInfo source) {
		this.totalPackages = MetricsUtils.countPackages(source);
		this.totalClasses = MetricsUtils.countClasses(source);
		this.totalMethods = MetricsUtils.countMethods(source);
		this.totalFields = MetricsUtils.countFields(source);
		
		this.avgClassesPerPackage = MetricsUtils.average(totalClasses, totalPackages);
		this.avgMethodsPerClass = MetricsUtils.average(totalMethods, totalClasses);
		this.avgFieldsPerClass = MetricsUtils.average(totalFields, totalClasses);
	}
	
	public int getTotalPackages() {
		return totalPackages;
	}

	public int getTotalClasses() {
		return totalClasses;
	}

	public int getTotalMethods() {
		return totalMethods;
	}

	public int getTotalFields() {
		return totalFields;
	}

	public double getAvgClassesPerPackage() {
		return avgClassesPerPackage;
	}

	public double getAvgMethodsPerClass() {
		return avgMethodsPerClass;
	}

	public double getAvgFieldsPerClass() {
		return avgFieldsPerClass;
	}

	public double getAvgLOCPerPackage() {
		return avgLOCPerPackage;
	}

	public double getAvgLOCPerClass() {
		return avgLOCPerClass;
	}

	public double getAvgLOCPerMethod() {
		return avgLOCPerMethod;
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "totalPackages=%d, "
				+ "totalClasses=%d, "
				+ "totalMethods=%d, "
				+ "totalFields=%d, "
				+ "avgClassesPerPackage=%d, "
				+ "avgMethodsPerClass=%d, "
				+ "avgFieldsPerClass=%d, "
				+ "avgLOCPerPackage=%d, "
				+ "avgLOCPerClass=%d, "
				+ "avgLOCPerMethod=%d}")
				.formatted(
					this.getName(),
					totalPackages,
					totalClasses,
					totalMethods,
					totalFields,
					avgClassesPerPackage,
					avgMethodsPerClass,
					avgFieldsPerClass,
					avgLOCPerPackage,
					avgLOCPerClass,
					avgLOCPerMethod
				);
	}

}
