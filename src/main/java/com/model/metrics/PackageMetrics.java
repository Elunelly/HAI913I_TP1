package com.model.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.PackageInfo;
import com.utils.MetricsUtils;

public class PackageMetrics extends NodeMetrics<PackageInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(PackageMetrics.class);

	private int totalClasses;
	private int totalMethods;
	private int totalFields;
	
	private double avgMethodsPerClass;
	private double avgFieldsPerClass;
	
	private double avgLOCPerClass;
	private double avgLOCPerMethod;
	
	public PackageMetrics(String name) {
		super(name);
	}

	@Override
	protected void doCalculate(PackageInfo source) {
		this.totalClasses = MetricsUtils.countClasses(source);
		this.totalMethods = MetricsUtils.countMethods(source);
		this.totalFields = MetricsUtils.countFields(source);
		
		this.avgMethodsPerClass = MetricsUtils.average(totalMethods, totalClasses);
		this.avgFieldsPerClass = MetricsUtils.average(totalFields, totalClasses);
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

	public double getAvgMethodsPerClass() {
		return avgMethodsPerClass;
	}

	public double getAvgFieldsPerClass() {
		return avgFieldsPerClass;
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
				+ "totalClasses=%d, "
				+ "totalMethods=%d, "
				+ "totalFields=%d, "
				+ "avgMethodsPerClass=%d, "
				+ "avgFieldsPerClass=%d, "
				+ "avgLOCPerClass=%d, "
				+ "avgLOCPerMethod=%d}")
				.formatted(
					this.getName(),
					totalClasses,
					totalMethods,
					totalFields,
					avgMethodsPerClass,
					avgFieldsPerClass,
					avgLOCPerClass,
					avgLOCPerMethod
				);
	}

}
