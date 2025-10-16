package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.ProjectInfo;
import com.utils.MetricsUtils;

public class ProjectMetrics extends NodeMetrics<ProjectInfo> {
	
	@SuppressWarnings("unused")
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ProjectMetrics that = (ProjectMetrics) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.data, that.data) &&
			Objects.equals(this.linesOfCode, that.linesOfCode) &&
			Objects.equals(this.totalPackages, that.totalPackages) &&
			Objects.equals(this.totalClasses, that.totalClasses) &&
			Objects.equals(this.totalMethods, that.totalMethods) &&
			Objects.equals(this.totalFields, that.totalFields)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				name,
				data,
				linesOfCode,
				totalPackages,
				totalClasses,
				totalMethods,
				totalFields);
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
