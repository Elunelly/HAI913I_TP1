package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInfo extends NodeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodInfo.class);

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

	public void setIsConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public List<String> getParameters() {return Collections.unmodifiableList(this.parameters);}
	
	public List<String> copyParameters() {return new ArrayList<>(this.parameters);}

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
		return ("Class{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "returnType=%s, "
				+ "isConstructor=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s, "
				+ "parameters=%d}")
				.formatted(
					this.getVisibility().name(),
					this.getName(),
					returnType,
					String.valueOf(isConstructor),
					String.valueOf(this.isStatic()),
					String.valueOf(this.isAbstract()),
					String.valueOf(this.isFinal()),
					parameters.size()
				);
	}

}
