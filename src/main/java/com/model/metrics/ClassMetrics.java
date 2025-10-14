package com.model.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;
import com.model.structural.NodeVisibility;
import com.utils.MetricsUtils;

public class ClassMetrics extends NodeMetrics<ClassInfo> {
	
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
