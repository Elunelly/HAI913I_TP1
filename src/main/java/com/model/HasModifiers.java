package com.model;

import java.util.Collection;

public interface HasModifiers {
	
	public boolean isStatic();
	
	public boolean isAbstract();
	
	public boolean isFinal();
	
	public Collection<NodeModifiers> getModifiers();
	
	public boolean setModifiers(int modifiers);
	
	public boolean hasModifiers();
	
	public boolean hasModifier(NodeModifiers modifier);
	
	public String modifiersToString(String sep);
	
	default String modifiersToString() {
		return modifiersToString(" ");
	}

}
