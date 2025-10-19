package com.model.metrics;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.ProjectInfo;

public class ProjectMetrics extends NodeMetrics<ProjectInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ProjectMetrics.class);
	
	public ProjectMetrics(String name) {
		super(name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ProjectMetrics that = (ProjectMetrics) obj;
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
