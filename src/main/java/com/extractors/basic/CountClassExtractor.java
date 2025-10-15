package com.extractors.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.MetricType;
import com.extractors.PackageMetricExtractor;
import com.extractors.ProjectMetricExtractor;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;

public class CountClassExtractor extends AbstractMetricExtractor 
	implements ProjectMetricExtractor, PackageMetricExtractor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CountClassExtractor.class);
	
	public CountClassExtractor() {
		super("Total Classes", MetricType.COUNT);
	}
	
	@Override
	public Integer extract(AnalysisResult source) {
        return (int) wrapper(getClasses(source).size());
	}

	@Override
	public Integer extract(PackageInfo source) {
        return (int) wrapper(getClasses(source).size());
	}

	@Override
	public Integer extract(ProjectInfo source) {
        return (int) wrapper(getClasses(source).size());
	}

	@Override
	public Integer extractAllByPackages(Collection<PackageInfo> source) {
        return (int) wrapper(getAllClasses(source).size());
	}

	@Override
	public Integer extractAllByProjects(Collection<ProjectInfo> source) {
        return (int) wrapper(getAllClasses(source).size());
	}

	@Override
	public Integer extractByRecursive(PackageInfo source) {
        return (int) wrapper(getClassesByRecursive(source).size());
	}

}
