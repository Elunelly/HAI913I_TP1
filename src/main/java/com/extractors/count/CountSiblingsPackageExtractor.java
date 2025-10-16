package com.extractors.count;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.model.project.PackageInfo;

public class CountSiblingsPackageExtractor extends CountExtractor<PackageInfo> {
	
	public CountSiblingsPackageExtractor() {
		super("Sibling Packages Count", CountSiblingsPackageExtractor::extractSiblings);
	}
	
	private static List<PackageInfo> extractSiblings(PackageInfo source) {
		if (source.isRoot())
			return Collections.emptyList();
		List<PackageInfo> siblings = new ArrayList<>(source.getParentPackage().getSubPackages());
		siblings.remove(source);
		return siblings;
	}

}
