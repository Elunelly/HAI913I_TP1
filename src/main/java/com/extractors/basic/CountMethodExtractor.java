package com.extractors.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.ClassMetricExtractor;
import com.extractors.MetricType;
import com.extractors.PackageMetricExtractor;
import com.extractors.ProjectMetricExtractor;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;

public class CountMethodExtractor extends AbstractMetricExtractor
	implements ProjectMetricExtractor, PackageMetricExtractor, ClassMetricExtractor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CountMethodExtractor.class);
	
	public CountMethodExtractor() {
		super("Total Methods", MetricType.COUNT);
	}
	
	@Override
	public Integer extract(AnalysisResult source) {
        return (int) wrapper(getMethods(source).size());
	}

	@Override
	public Integer extract(ClassInfo source) {
        return (int) wrapper(getMethods(source).size());
	}

	@Override
	public Integer extract(PackageInfo source) {
        return (int) wrapper(getAllMethods(getClasses(source)).size());
	}

	@Override
	public Integer extract(ProjectInfo source) {
        return (int) wrapper(getAllMethods(getClasses(source)).size());
	}

	@Override
	public Integer extractAllByClasses(Collection<ClassInfo> source) {
        return (int) wrapper(getAllMethods(source).size());
	}

	@Override
	public Integer extractAllByPackages(Collection<PackageInfo> source) {
        return (int) wrapper(getAllMethods(getAllClasses(source)).size());
	}

	@Override
	public Integer extractAllByProjects(Collection<ProjectInfo> source) {
        return (int) wrapper(getAllMethods(getAllClasses(source)).size());
	}

	@Override
	public Integer extractByRecursive(PackageInfo source) {
        return (int) wrapper(getMethodsByRecursive(source).size());
	}

}
