package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInfo extends StructuralNode<MethodDeclaration> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodInfo.class);

	private final ClassInfo parentClass;
    private final List<String> parameters = new ArrayList<>();
    
    public MethodInfo(MethodDeclaration node, ClassInfo parentClass) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		this.parentClass = Objects.requireNonNull(parentClass, "Class cannot be null for '"+name+"' method");
    }
    
    public ClassInfo getParentClass() {return parentClass;}
    
    public String getClassName() {return parentClass.getName();}
    
    public String getReturnType() {return node.getReturnType2()==null?"":node.getReturnType2().toString();}

	public boolean isConstructor() {return node.isConstructor();}

	public List<String> getParameters() {return Collections.unmodifiableList(parameters);}
	
	public List<String> copyParameters() {return new ArrayList<>(parameters);}

	public boolean addParameter(String parameter) {
		if (parameter!=null && !parameter.isBlank() && !parameters.contains(parameter) && parameters.add(parameter)) {
			logger.debug("Parameter added: %s".formatted(parameter));
			return true;
		}
		return false;
	}
	
	public boolean removeParameter(String parameter) {
		if (parameter!=null && parameters.remove(parameter)) {
			logger.debug("Parameter removed: %s".formatted(parameter));
			return true;
		}
		return false;
	}
	
	@Override
	public String getSignature() {
		return 
			getName()+
			"("+String.join(", ",getParameters())+")"
		;
	}
	
	@Override
	public String getShortSignature() {
		return getClassName()+"::"+getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		MethodInfo that = (MethodInfo) obj;
		return 
			Objects.equals(this.getSignature(), that.getSignature()) &&
			Objects.equals(this.getClassName(), that.getClassName()) &&
			Objects.equals(this.getReturnType(), that.getReturnType()) &&
			Objects.equals(this.isConstructor(), that.isConstructor()) &&
			Objects.equals(this.parameters.size(), that.parameters.size())
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getSignature(), getClassName(), getReturnType(), isConstructor(), parameters.size());
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "returnType=%s, "
				+ "isConstructor=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s, "
				+ "parameters=%d}")
				.formatted(
					getVisibility().name(),
					getName(),
					getReturnType(),
					String.valueOf(isConstructor()),
					String.valueOf(isStatic()),
					String.valueOf(isAbstract()),
					String.valueOf(isFinal()),
					parameters.size()
				);
	}

}
