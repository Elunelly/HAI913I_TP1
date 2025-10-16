package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;
import com.model.structural.NodeVisibility;
import com.utils.MetricsUtils;

public class ClassMetrics extends NodeMetrics<ClassInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ClassMetrics.class);

	private int totalMethods;
	private int totalFields;
	private int totalPublicMethods;
	private int totalPrivateMethods;
	private int totalConstantFields;
	
	private double complexity;
	
	private double avgLOCPerMethod;
	
	public ClassMetrics(String name) {
		super(name);
	}

	@Override
	protected void doCalculate(ClassInfo source) {
		this.totalMethods = MetricsUtils.countMethods(source);
		this.totalFields = MetricsUtils.countFields(source);
		this.totalPublicMethods = MetricsUtils.countPublicMethods(source);
		this.totalPrivateMethods = MetricsUtils.countMethodsByVisibility(source, NodeVisibility.PRIVATE);
		this.totalConstantFields = MetricsUtils.countConstantFields(source);
	}

	public int getTotalMethods() {
		return totalMethods;
	}

	public int getTotalFields() {
		return totalFields;
	}

	public double getComplexity() {
		return complexity;
	}

	public int getTotalPublicMethods() {
		return totalPublicMethods;
	}

	public int getTotalPrivateMethods() {
		return totalPrivateMethods;
	}

	public int getTotalConstantFields() {
		return totalConstantFields;
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
		
		ClassMetrics that = (ClassMetrics) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.data, that.data) &&
			Objects.equals(this.linesOfCode, that.linesOfCode) &&
			Objects.equals(this.totalMethods, that.totalMethods) &&
			Objects.equals(this.totalFields, that.totalFields) &&
			Objects.equals(this.totalPublicMethods, that.totalPublicMethods) &&
			Objects.equals(this.totalPrivateMethods, that.totalPrivateMethods) &&
			Objects.equals(this.totalConstantFields, that.totalConstantFields)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				name,
				data,
				linesOfCode,
				totalMethods,
				totalFields,
				totalPublicMethods,
				totalPrivateMethods,
				totalConstantFields);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "totalMethods=%d, "
				+ "totalFields=%d, "
				+ "totalPublicMethods=%d, "
				+ "totalPrivateMethods=%d, "
				+ "totalConstantFields=%d, "
				+ "complexity=%d, "
				+ "avgLOCPerMethod=%d}")
				.formatted(
					this.getName(),
					totalMethods,
					totalFields,
					totalPublicMethods,
					totalPrivateMethods,
					totalConstantFields,
					complexity,
					avgLOCPerMethod
				);
	}

}
