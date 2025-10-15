package com.model.metrics;

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
	public String toString() {
		// TODO
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "totalParameters=%d}")
				.formatted(this.getName(),totalParameters);
	}

}
