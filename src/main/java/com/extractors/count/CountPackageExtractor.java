package com.extractors.count;

import com.extractors.ModelNavigator;

public class CountPackageExtractor extends CountExtractor<Object> {
	
	public CountPackageExtractor() {
		super("Package Count", ModelNavigator::getPackages);
	}

}
