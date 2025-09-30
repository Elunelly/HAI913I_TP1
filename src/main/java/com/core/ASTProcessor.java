package com.core;

import java.util.List;

import com.calculators.StatisticsCalculator;
import com.extractors.MetricExtractor;
import com.model.project.JavaProject;
import com.parser.ASTParserFacade;
import com.visitors.base.BaseASTVisitor;

public class ASTProcessor {
	
	private List<BaseASTVisitor> visitors;
	private List<MetricExtractor> extractors;
	private ASTParserFacade parserFacade;
	private ProjectExplorer explorer;
	private StatisticsCalculator calculator;
	
	public AnalysisResult processProject(String path) {
		// TODO
		return null;
	}
	
	public boolean addvisitor(BaseASTVisitor visitor) {
		// TODO
		return this.visitors.add(visitor);
	}
	
	public boolean addExtractor(MetricExtractor extractor) {
		// TODO
		return this.extractors.add(extractor);
	}
	
	private void executeVisitors(JavaProject project) {
		// TODO
	}
	
	private void calculateMetrics(AnalysisResult result) {
		// TODO
	}
}