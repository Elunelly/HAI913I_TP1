package com.extractors.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.ClassMetricExtractor;
import com.extractors.FieldMetricExtractor;
import com.extractors.MethodMetricExtractor;
import com.extractors.MetricType;
import com.extractors.PackageMetricExtractor;
import com.extractors.ProjectMetricExtractor;
import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;

public class CountLinesOfCodeExtractor extends AbstractMetricExtractor
	implements ProjectMetricExtractor, PackageMetricExtractor, ClassMetricExtractor, MethodMetricExtractor, FieldMetricExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(CountLinesOfCodeExtractor.class);
    
    public CountLinesOfCodeExtractor() {
        super("Total Lines of Code", MetricType.COUNT);
    }
    
    @Override
    public Object extract(AnalysisResult result) {
        int count = result.getProject().getCompilationUnits().stream().mapToInt(c -> c.getLineNumber(c.getLength()-1)).sum();
        logger.debug("Extracted metric '{}': {}", getMetricName(), count);
        return count;
    }
    
    @Override
    public boolean isApplicable(AnalysisResult result) {
        return false;
    }

	@Override
	public Object extract(FieldInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extract(MethodInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extract(ClassInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extractAllByClasses(Collection<ClassInfo> source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extract(PackageInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extractAllByPackages(Collection<PackageInfo> source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extractByRecursive(PackageInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extract(ProjectInfo source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object extractAllByProjects(Collection<ProjectInfo> source) {
		// TODO Auto-generated method stub
		return null;
	}

}
