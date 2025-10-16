package com.extractors.count;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import com.extractors.AbstractMetricExtractor;
import com.extractors.MetricType;
import com.extractors.ModelNavigator;

public abstract class CountExtractor<T> extends AbstractMetricExtractor<T> {
	
	private final Function<T, Collection<?>> extractingFunction;
	
	protected CountExtractor(String metricName, Function<T, Collection<?>> extractingFunction) {
		super(metricName, MetricType.COUNT);
		this.extractingFunction = Objects.requireNonNull(extractingFunction);
	}

	@Override
	public Integer extract(T source) {
		return ModelNavigator.count(extractingFunction.apply(source));
	}

}
