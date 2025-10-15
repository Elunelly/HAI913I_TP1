package com.extractors;

import java.util.Collection;

import com.model.project.ProjectInfo;

public interface ProjectMetricExtractor {
	
	Object extract(ProjectInfo source);
	
	Object extractAllByProjects(Collection<ProjectInfo> source);

}
