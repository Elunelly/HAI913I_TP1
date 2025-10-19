package com.model.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.NodeInfo;

public abstract class ProjectNode extends NodeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectNode.class);

	public ProjectNode(String name) {
		super(name);
		logger.trace("{} -> ProjectNode()",this.name);
	}

}
