package com.extractors.count;

import com.extractors.ModelNavigator;

public class CountMethodExtractor extends CountExtractor<Object> {
	
	public CountMethodExtractor() {
		super("Method Count", ModelNavigator::getMethods);
	}

}
