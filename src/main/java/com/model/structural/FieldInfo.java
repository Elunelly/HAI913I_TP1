package com.model.structural;

import java.util.Objects;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInfo extends NodeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(FieldInfo.class);

	private ClassInfo parentClass;
	private String type;
	
	public FieldInfo(String name, ClassInfo parent) {
		super(name);
		setParentClass(parent);
	}
	
	public FieldInfo(String name) {
		this(name,null);
	}
	
	public FieldInfo withClass(ClassInfo parent) {
		setParentClass(parent);
		return this;
	}
	
	public FieldInfo withCompilationUnit(VariableDeclarationFragment node) {
		Objects.requireNonNull(node);
		setCompilationUnit((CompilationUnit) node.getRoot());
		this.unit_firstCharPos = node.getStartPosition();
		this.unit_lastCharPos = node.getLength() + unit_firstCharPos -1;
		this.unit_firstLineNum = unit.getLineNumber(unit_firstCharPos);
		this.unit_lastLineNum = unit.getLineNumber(unit_lastCharPos);
		return this;
	}
	
	public ClassInfo getParentClass() {return parentClass;}
	
	public void setParentClass(ClassInfo parent) {
		this.parentClass = parent;
	}
	
	public String getType() {return this.type;}
	
	public void setType(String type) {
		String old = this.type;
		this.type = type;
		logger.debug("Change value of 'type': %s -> %s".formatted(old,this.type));
	}
	
	public boolean isConstant() {
		return isStatic() && isFinal();
	}
	
	@Override
	public String getSignature() {
		return getType()+" "+getName();
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
					this.getVisibility().name(),
					this.getName(),
					type,
					String.valueOf(this.isStatic()),
					String.valueOf(this.isAbstract()),
					String.valueOf(this.isFinal())
				);
	}
	
}
