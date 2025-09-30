package com.model.structural;

public class FieldInfo extends NodeInfo {

	protected String type;
	
	public FieldInfo() {
		super();
	}
	
	public String getType() {return this.type;}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isConstant() {
		return isStatic() && isFinal();
	}
	
	@Override
	public String getSignature() {
		return getType()+" "+getName();
	}
	
	@Override
	public String toString() {
		return "Field{%s %s %s}".formatted(getVisibility(),getType(),getName());
	}
	
}
