package com.model.utils;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NodeInfo implements ModelInfo {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(NodeInfo.class);
	
	protected final String name;
	
	public NodeInfo(String name) {
		this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
//		if (this.name.isBlank()) {
//			logger.error("Name cannot be empty");
//			throw new IllegalArgumentException("Name cannot be empty");
//		}
	}

	public String getName() {return name;}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		NodeInfo that = (NodeInfo) obj;
		return 
			Objects.equals(this.name, that.name)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s}")
				.formatted(
					name
				);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}