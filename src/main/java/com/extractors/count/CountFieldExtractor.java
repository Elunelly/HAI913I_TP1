package com.extractors.count;

import com.extractors.ModelNavigator;

public class CountFieldExtractor extends CountExtractor<Object> {
	
	public CountFieldExtractor() {
		super("Field Count", ModelNavigator::getFields);
	}

}
