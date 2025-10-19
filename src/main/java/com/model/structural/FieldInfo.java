package com.model.structural;

import java.util.Objects;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInfo extends StructuralNode<VariableDeclarationFragment> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FieldInfo.class);

	private final ClassInfo parentClass;
	private final String type;
	private final String defaultValue;
	
	public FieldInfo(VariableDeclarationFragment node, ClassInfo parentClass, String type) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		this.parentClass = Objects.requireNonNull(parentClass, "Class cannot be null for '"+name+"' method");
		this.type = Objects.requireNonNull(type, "Type cannot be null");
		this.defaultValue = node.getInitializer()==null ? null : node.getInitializer().toString();
		logger.trace("{} -> FieldInfo(VariableDeclarationFragment,ClassInfo,String)",name);
	}

	@Override
	public String getQualifiedName() {
		logger.trace("{} -> getQualifiedName()",name);
		if (getClassName().isBlank()) return getName();
		else return "%s[%s]".formatted(getClassName(),getName());
	}
	
	public ClassInfo getParentClass() {
		logger.trace("{} -> getParentClass()",name);
		return parentClass;
	}
	
	public String getClassName() {
		logger.trace("{} -> getClassName()",name);
		return parentClass.getName();
	}
	
	public String getType() {
		logger.trace("{} -> getType()",name);
		return type;
	}
	
	public String getDefaultValue() {
		logger.trace("{} -> getDefaultValue()",name);
		return defaultValue;
	}
	
	public boolean isInitialized() {
		logger.trace("{} -> isInitialized()",name);
		return defaultValue!=null;
	}
	
	public boolean isConstant() {
		logger.trace("{} -> isConstant()",name);
		return isStatic() && isFinal();
	}
	
	@Override
	public String getSignature() {
		logger.trace("{} -> getSignature()",name);
		return getType()+" "+getName()+(isInitialized()?" "+getDefaultValue():"");
	}

	@Override
	public String getShortSignature() {
		logger.trace("{} -> getShortSignature()",name);
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		FieldInfo that = (FieldInfo) obj;
		return 
			Objects.equals(this.getSignature(), that.getSignature())
		;
	}
	
	@Override
	public int hashCode() {
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(getSignature());
	}

	@Override
	public String toString() {
		logger.trace("{} -> toString()",name);
		return (this.getClass().getSimpleName()+"{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "type=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s}")
				.formatted(
					getVisibility().name(),
					getName(),
					type,
					String.valueOf(isStatic()),
					String.valueOf(isAbstract()),
					String.valueOf(isFinal())
				);
	}
	
}
