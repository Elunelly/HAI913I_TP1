package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.MethodInfo;

public class MethodMetrics extends NodeMetrics<MethodInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodMetrics.class);
	
	private int totalParameters;
	
	public MethodMetrics(String name) {
		super(name);
	}

	@Override
	protected void doCalculate(MethodInfo source) {
		this.totalParameters = source.getParameters().size();
	}
	
	public int getTotalParameters() {
		return totalParameters;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		MethodMetrics that = (MethodMetrics) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.data, that.data) &&
			Objects.equals(this.linesOfCode, that.linesOfCode) &&
			Objects.equals(this.totalParameters, that.totalParameters)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				name,
				data,
				linesOfCode,
				totalParameters);
	}
	
	@Override
	public String toString() {
		// TODO
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "totalParameters=%d}")
				.formatted(this.getName(),totalParameters);
	}

}
