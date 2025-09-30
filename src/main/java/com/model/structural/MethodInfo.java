package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodInfo extends NodeInfo {

    protected String returnType;
    protected boolean isConstructor;
    protected final List<String> parameters = new ArrayList<>();
    
    public MethodInfo() {
    	super();
    }
    
    public String getReturnType() {return this.returnType;}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public boolean isConstructor() {return this.isConstructor;}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public List<String> getParameters() {return Collections.unmodifiableList(this.parameters);}

	public void addParameter(String parameter) {
		if (parameter!=null &&
			!parameter.isBlank() &&
			!this.parameters.contains(parameter)) this.parameters.add(parameter);
	}
	
	@Override
	public String getSignature() {
		return 
			getName()+
			"("+String.join(", ",getParameters())+")"
		;
	}

	@Override
    public String toString() {
    	return "Method{%s %s(%d parameters)}".formatted(getVisibility(),getName(),getParameters().size());
    }

}
