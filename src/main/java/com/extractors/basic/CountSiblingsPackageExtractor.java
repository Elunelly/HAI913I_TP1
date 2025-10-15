package com.extractors.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.MetricType;
import com.extractors.PackageMetricExtractor;
import com.model.project.PackageInfo;

public class CountSiblingsPackageExtractor extends AbstractMetricExtractor
	implements PackageMetricExtractor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CountSiblingsPackageExtractor.class);
	
	public CountSiblingsPackageExtractor() {
		super("Total Package", MetricType.COUNT);
	}
	
	@Override
	public Integer extract(AnalysisResult source) {
		return null;
	}

	@Override
	public Integer extract(PackageInfo source) {
		return (int) wrapper(count(source));
	}

	@Override
	public Integer extractAllByPackages(Collection<PackageInfo> source) {
		return (int) wrapper(count(source));
	}

	@Override
	public Integer extractByRecursive(PackageInfo source) {
		return (int) wrapper(source.getAllDescendants().size()+count(source));
	}
	
	private int count(PackageInfo source) {
		return source.isRoot() ? 0 : source.getParentPackage().getSubPackages().size() - 1;
	}
	
	private int count(Collection<PackageInfo> source) {
		return source.stream().mapToInt(p -> count(p)).sum();
	}

}
