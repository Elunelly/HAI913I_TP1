package com.extractors.count;

import com.core.AnalysisResult;
import com.extractors.AbstractMetricExtractor;
import com.extractors.MetricType;

public class CountLinesOfCodeExtractor extends AbstractMetricExtractor<AnalysisResult> {
	
	public CountLinesOfCodeExtractor() {
		super("Field Count", MetricType.COUNT);
	}

	@Override
	public Object extract(AnalysisResult source) {
		// TODO Auto-generated method stub
		return null;
	}

}
