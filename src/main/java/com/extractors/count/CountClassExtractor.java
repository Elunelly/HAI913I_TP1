package com.extractors.count;

import com.extractors.ModelNavigator;

public class CountClassExtractor extends CountExtractor<Object> {
	
	public CountClassExtractor() {
		super("Class Count", ModelNavigator::getClasses);
	}

}
