package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;

public class ClassMetrics extends NodeMetrics<ClassInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ClassMetrics.class);
	
	public ClassMetrics(String name) {
		super(name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ClassMetrics that = (ClassMetrics) obj;
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
