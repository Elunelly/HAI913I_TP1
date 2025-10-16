package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.interfaces.HasFields;
import com.model.interfaces.HasMethods;
import com.model.project.PackageInfo;

public class ClassInfo extends NodeInfo implements HasMethods, HasFields {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
	
	private String packageName = "";
	private boolean isInterface;
	private String superClass;
	private final List<String> interfaces = new ArrayList<>();
	private final List<MethodInfo> methods = new ArrayList<>();
	private final List<FieldInfo> fields = new ArrayList<>();
	
	private PackageInfo parentPackage;
	
	public ClassInfo(String name, PackageInfo parent) {
		super(name);
		setPackage(parent);
	}
	
	public ClassInfo(String name) {
		this(name,null);
	}
	
	public ClassInfo withPackage(PackageInfo parent) {
		setPackage(parent);
		return this;
	}
	
	public ClassInfo withCompilationUnit(TypeDeclaration node) {
		Objects.requireNonNull(node);
		setCompilationUnit((CompilationUnit) node.getRoot());
		this.unit_firstCharPos = node.getStartPosition();
		this.unit_lastCharPos = node.getLength() + unit_firstCharPos -1;
		this.unit_firstLineNum = unit.getLineNumber(unit_firstCharPos);
		this.unit_lastLineNum = unit.getLineNumber(unit_lastCharPos);
		return this;
	}
	
	public PackageInfo getParentPackage() {return this.parentPackage;}
	
	public void setPackage(PackageInfo parentPackage) {
		PackageInfo old = this.parentPackage;
		this.parentPackage = Objects.requireNonNull(parentPackage);
		this.packageName = parentPackage.getName();
		logger.debug("Change value of 'parentPackage': %s -> %s".formatted(old,this.parentPackage));
	}
	
	public String getPackageName() {return this.packageName;}

	public boolean isInterface() {return this.isInterface;}

	public void setInterface(boolean isInterface) {
		boolean old = this.isInterface;
		this.isInterface = isInterface;
		logger.debug("Change value of 'isInterface': %s -> %s".formatted(old,this.isInterface));
	}
	
	public String getSuperClass() {return this.superClass;}
	
	public void setSuperClass(String superClass) {
		String old = this.superClass;
		this.superClass = superClass;
		logger.debug("Change value of 'superClass': %s -> %s".formatted(old,this.superClass));
	}

	public List<String> getInterfaces() {return Collections.unmodifiableList(this.interfaces);}
	
	public List<String> copyInterfaces() {return new ArrayList<>(this.interfaces);}

	public void addInterface(String interfaceName) {
		if (interfaceName!=null && !interfaceName.isBlank() && !this.interfaces.contains(interfaceName) && this.interfaces.add(interfaceName))
				logger.debug("Interface added: %s".formatted(interfaceName));
	}
	
	public List<MethodInfo> methods() {return methods;}
	
	public List<FieldInfo> fields() {return fields;}
	
	public String getQualifiedName() {
		if (this.packageName.isBlank()) return getName();
		else return this.packageName + "." + getName();
	}
	
	public boolean hasInheritance() {
		return 
			this.superClass != null &&
			!this.superClass.isEmpty() &&
			!this.interfaces.isEmpty()
		;
	}
	
	public boolean hasImplementations() {
		return
			this.interfaces != null &&
			!this.interfaces.isEmpty()
		;
	}
	
	public List<MethodInfo> getMethodsByVisibility(NodeVisibility visibility) {
		List<MethodInfo> result = new ArrayList<>();
		for (MethodInfo method : this.methods) {
			if (method.getVisibility().equals(visibility)) result.add(method);
		}
		return result;
	}
	
	public List<MethodInfo> getPublicMethods() {
		return getMethodsByVisibility(NodeVisibility.PUBLIC);
	}
	
	public List<MethodInfo> getProtectedMethods() {
		return getMethodsByVisibility(NodeVisibility.PROTECTED);
	}
	
	public List<MethodInfo> getPrivateMethods() {
		return getMethodsByVisibility(NodeVisibility.PRIVATE);
	}
	
	public List<MethodInfo> getPackageMethods() {
		return getMethodsByVisibility(NodeVisibility.PACKAGE);
	}
	
	@Override
	public String getSignature() {
		return 
			(isInterface() ? "interface" : "class")+" "+
			getName()+
			(hasInheritance() ? " extends "+getSuperClass() : "")+
			(hasImplementations() ? " implements "+String.join(", "+getInterfaces()) : "")
		;
	}
	
	public String getShortSignature() {
		return getName();
	}
	
	public String getOverviewSignature() {
		int fn = getFields().size();
		int mn = getMethods().size();
		return "%s { %d field%s, %d method%s }"
				.formatted(getFullSignature(),fn,fn==1?"":"s",mn,mn==1?"":"s")
		;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ClassInfo that = (ClassInfo) obj;
		return 
			Objects.equals(this.getSignature(), that.getSignature()) &&
			Objects.equals(this.packageName, that.packageName) &&
			Objects.equals(this.methods.size(), that.methods.size()) &&
			Objects.equals(this.fields.size(), that.fields.size())
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getSignature(), packageName, methods.size(), fields.size());
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "isInterface=%s, "
				+ "superClass=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s, "
				+ "interfaces=%d, "
				+ "methods=%d, "
				+ "fields=%d}")
				.formatted(
					this.getVisibility().name(),
					this.getName(),
					String.valueOf(isInterface),
					superClass,
					String.valueOf(this.isStatic()),
					String.valueOf(this.isAbstract()),
					String.valueOf(this.isFinal()),
					interfaces.size(),
					methods.size(),
					fields.size()
				);
	}
	
}
