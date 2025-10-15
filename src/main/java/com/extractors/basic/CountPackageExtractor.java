package com.extractors.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.MetricType;
import com.extractors.ProjectMetricExtractor;
import com.model.project.ProjectInfo;

public class CountPackageExtractor extends AbstractMetricExtractor
	implements ProjectMetricExtractor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CountPackageExtractor.class);
	
	public CountPackageExtractor() {
		super("Total Package", MetricType.COUNT);
	}
	
	@Override
	public Integer extract(AnalysisResult source) {
        return (int) wrapper(getPackages(source).size());
	}

	@Override
	public Integer extract(ProjectInfo source) {
        return (int) wrapper(source.getPackages().size());
	}

	@Override
	public Integer extractAllByProjects(Collection<ProjectInfo> source) {
        return (int) wrapper(source.stream().mapToInt(p -> p.getPackages().size()).sum());
	}

}
