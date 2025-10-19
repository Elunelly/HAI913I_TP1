package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.MethodInfo;

public class MethodMetrics extends NodeMetrics<MethodInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodMetrics.class);
	
	public MethodMetrics(String name) {
		super(name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		MethodMetrics that = (MethodMetrics) obj;
		return 
			Objects.equals(this.name, that.name)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s}")
				.formatted(
					this.getName()
				);
	}

}
