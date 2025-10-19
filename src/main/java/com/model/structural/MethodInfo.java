package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.metrics.MethodMetrics;

public class MethodInfo extends StructuralNode<MethodDeclaration> {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodInfo.class);

	private final ClassInfo parentClass;
	private final String returnType;
    private final List<String> parameters = new ArrayList<>();
    
    private final MethodMetrics metrics;
    
    public MethodInfo(MethodDeclaration node, ClassInfo parentClass) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		this.parentClass = Objects.requireNonNull(parentClass, "Class cannot be null for '"+name+"' method");
		this.returnType = node.getReturnType2()==null?"":node.getReturnType2().toString();
		this.metrics = new MethodMetrics(getQualifiedName());
		logger.trace("{} -> MethodInfo(MethodDeclaration,ClassInfo)",name);
    }
    
	@Override
	public String getQualifiedName() {
		logger.trace("{} -> getQualifiedName()",name);
		if (getClassName().isBlank()) return getName();
		else return "%s::%s".formatted(getClassName(),getName());
	}
    
    public ClassInfo getParentClass() {
		logger.trace("{} -> getParentClass()",name);
    	return parentClass;
    }
    
    public String getClassName() {
		logger.trace("{} -> getClassName()",name);
    	return parentClass.getName();
    }
    
    public String getReturnType() {
		logger.trace("{} -> getReturnType()",name);
    	return returnType;
    }

	public boolean isConstructor() {
		logger.trace("{} -> isConstructor()",name);
		return node.isConstructor();
	}

	public List<String> getParameters() {
		logger.trace("{} -> getParameters()",name);
		return Collections.unmodifiableList(parameters);
	}
	
	public List<String> copyParameters() {
		logger.trace("{} -> copyParameters()",name);
		return new ArrayList<>(parameters);
	}

	public boolean addParameter(String parameter) {
		logger.trace("{} -> addParameter(String)",name);
		if (parameter!=null && !parameter.isBlank() && !parameters.contains(parameter) && parameters.add(parameter)) {
			logger.debug("Parameter added: %s".formatted(parameter));
			return true;
		}
		return false;
	}
	
	public boolean removeParameter(String parameter) {
		logger.trace("{} -> removeParameter(String)",name);
		if (parameter!=null && parameters.remove(parameter)) {
			logger.debug("Parameter removed: %s".formatted(parameter));
			return true;
		}
		return false;
	}
	
	public MethodMetrics getMetrics() {
		logger.trace("{} -> getMetrics()",name);
		return metrics;
	}
	
	@Override
	public String getSignature() {
		logger.trace("{} -> getSignature()",name);
		return 
			(isConstructor() ? "" : getReturnType()+" ")+
			getName()+
			"("+String.join(", ",getParameters())+")"
		;
	}
	
	@Override
	public String getShortSignature() {
		logger.trace("{} -> getShortSignature()",name);
		return getClassName()+"::"+getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
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
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(getSignature(), getClassName(), getReturnType(), isConstructor(), parameters.size());
	}

	@Override
	public String toString() {
		logger.trace("{} -> toString()",name);
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
