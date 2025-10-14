package com.model.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.PackageInfo;

public interface HasPackages {
	
	static final Logger logger = LoggerFactory.getLogger(HasPackages.class);
	
	List<PackageInfo> packages();
	
	default List<PackageInfo> getPackages() {return Collections.unmodifiableList(packages());}
	
	default List<PackageInfo> copyPackages() {return new ArrayList<>(packages());}
	
	default boolean addPackage(PackageInfo packageInfo) {
		if (packageInfo!=null && !packages().contains(packageInfo) && packages().add(packageInfo)) {
			logger.debug("Package added: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	default boolean addAllPackages(List<PackageInfo> packages) {
		return packages.stream().filter(c -> addPackage(c)).count() > 0;
	}
	
	default boolean removePackage(PackageInfo packageInfo) {
		if (packageInfo!=null && packages().remove(packageInfo)) {
			logger.debug("Package removed: %s".formatted(packageInfo));
			return true;
		}
		return false;
	}
	
	default boolean removeAllPackages(List<PackageInfo> packages) {
		return packages.stream().filter(c -> removePackage(c)).count() > 0;
	}
	
	default PackageInfo getPackage(String name) {
		for(PackageInfo packageInfo : packages()) {
			if (name.equalsIgnoreCase(packageInfo.getName())) return packageInfo;
		}
		return null;
	}
	
	default boolean hasPackage(String name) {
		return getPackage(name) != null;
	}
	
	default boolean hasPackages() {
		return !packages().isEmpty();
	}

}
