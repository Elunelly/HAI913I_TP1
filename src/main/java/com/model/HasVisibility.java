package com.model;

public interface HasVisibility {
	
	public boolean isPublic();
	
	public boolean isProtected();
	
	public boolean isPrivate();
	
	public NodeVisibility getVisibility();
	
	public void setVisibility(NodeVisibility visibility);

}
