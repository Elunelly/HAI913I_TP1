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
	
	public FieldInfo(VariableDeclarationFragment node, ClassInfo parentClass, String type) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		this.parentClass = Objects.requireNonNull(parentClass, "Class cannot be null for '"+name+"' method");
		this.type = Objects.requireNonNull(type, "Type cannot be null");
	}
	
	public ClassInfo getParentClass() {return parentClass;}
	
	public String getType() {return this.type;}
	
	public boolean isConstant() {
		return isStatic() && isFinal();
	}
	
	@Override
	public String getSignature() {
		return getType()+" "+getName();
	}

	@Override
	public String getShortSignature() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
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
		return Objects.hash(getSignature());
	}

	@Override
	public String toString() {
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
