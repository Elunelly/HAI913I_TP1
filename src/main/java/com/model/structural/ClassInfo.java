package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.PackageInfo;
import com.model.utils.NodeVisibility;

public class ClassInfo extends StructuralNode<TypeDeclaration> {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
	
	private final PackageInfo parentPackage;
	private final String superClass;
	private final List<String> interfaces = new ArrayList<>();
	private final List<MethodInfo> methods = new ArrayList<>();
	private final List<FieldInfo> fields = new ArrayList<>();
	
	public ClassInfo(TypeDeclaration node, PackageInfo parentPackage) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		this.parentPackage = Objects.requireNonNull(parentPackage, "Package cannot be null for '"+name+"' class");
		this.superClass = node.getSuperclassType()!=null ?
				node.getSuperclassType().toString() :
				null;
	}
	
	// PARENT PACKAGE
	
	public PackageInfo getParentPackage() {return this.parentPackage;}
	
	public String getPackageName() {return this.parentPackage.getName();}
	
	// SUPER CLASS
	
	public String getSuperClass() {return superClass;}
	
	public boolean hasInheritance() {return superClass!=null && !this.superClass.isBlank();}
	
	public boolean isComplex() {
		return hasInheritance() || hasImplementations();
	}
	
	// INTERFACES
	
	public boolean isInterface() {return node.isInterface();}
	
	public List<String> getInterfaces() {return Collections.unmodifiableList(interfaces);}
	
	public boolean addInterface(String interfaceName) {
		if (interfaceName!=null && !interfaces.contains(interfaceName) && interfaces.add(interfaceName)) {
			logger.debug("Interface added: %s".formatted(interfaceName));
			return true;
		}
		return false;
	}
	
	public boolean removeInterface(String interfaceName) {
		if (interfaceName!=null && interfaces.remove(interfaceName)) {
			logger.debug("Interface removed: %s".formatted(interfaceName));
			return true;
		}
		return false;
	}
	
	public boolean hasImplementations() {
		return !interfaces.isEmpty();
	}
	
	// METHODS
	
	public List<MethodInfo> getMethods() {return Collections.unmodifiableList(methods);}
	
	public List<MethodInfo> copyMethods() {return new ArrayList<>(methods);}
	
	public boolean addMethod(MethodInfo methodInfo) {
		if (methodInfo!=null && !methods.contains(methodInfo) && methods.add(methodInfo)) {
			logger.debug("Method added: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllMethods(List<MethodInfo> methodInfos) {
		return methodInfos.stream().filter(c -> addMethod(c)).count() > 0;
	}
	
	public boolean removeMethod(MethodInfo methodInfo) {
		if (methodInfo!=null && methods.remove(methodInfo)) {
			logger.debug("Method removed: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllMethods(List<MethodInfo> methodInfos) {
		return methodInfos.stream().filter(c -> removeMethod(c)).count() > 0;
	}
	
	public MethodInfo getMethod(String name) {
		for(MethodInfo methodInfo : methods) {
			if (name.equalsIgnoreCase(methodInfo.getName())) return methodInfo;
		}
		return null;
	}
	
	public boolean hasMethod(String name) {
		return getMethod(name) != null;
	}
	
	public boolean hasMethods() {
		return !methods.isEmpty();
	}
	
	public void clearMethods() {
		methods.clear();
		logger.debug("All Methods removed from: "+getName());
	}
	
	public List<MethodInfo> getMethodsByVisibility(NodeVisibility visibility) {
		return copyMethods().stream().filter(m -> m.getVisibility()==visibility).toList();
	}
	
	// FIELDS
	
	public List<FieldInfo> getFields() {return Collections.unmodifiableList(fields);}
	
	public List<FieldInfo> copyFields() {return new ArrayList<>(fields);}
	
	public boolean addField(FieldInfo fieldInfo) {
		if (fieldInfo!=null && !fields.contains(fieldInfo) && fields.add(fieldInfo)) {
			logger.debug("Field added: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllFields(List<FieldInfo> fieldInfos) {
		return fieldInfos.stream().filter(c -> addField(c)).count() > 0;
	}
	
	public boolean removeField(FieldInfo fieldInfo) {
		if (fieldInfo!=null && fields.remove(fieldInfo)) {
			logger.debug("Field removed: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllFields(List<FieldInfo> fieldInfos) {
		return fieldInfos.stream().filter(c -> removeField(c)).count() > 0;
	}
	
	public FieldInfo getField(String name) {
		for(FieldInfo fieldInfo : fields) {
			if (name.equalsIgnoreCase(fieldInfo.getName())) return fieldInfo;
		}
		return null;
	}
	
	public boolean hasField(String name) {
		return getField(name) != null;
	}
	
	public boolean hasFields() {
		return !fields.isEmpty();
	}
	
	public void clearFields() {
		fields.clear();
		logger.debug("All Fields removed from: "+getName());
	}
	
	public List<FieldInfo> getFieldsByVisibility(NodeVisibility visibility) {
		return copyFields().stream().filter(f -> f.getVisibility()==visibility).toList();
	}
	
	// UTILITIES

	public String getQualifiedName() {
		if (getPackageName().isBlank()) return getName();
		else return getPackageName() + "." + getName();
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
	
	@Override
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
			Objects.equals(this.getPackageName(), that.getPackageName()) &&
			Objects.equals(this.methods.size(), that.methods.size()) &&
			Objects.equals(this.fields.size(), that.fields.size())
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getSignature(), getPackageName(), methods.size(), fields.size());
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
					getVisibility().name(),
					getName(),
					String.valueOf(isInterface()),
					superClass,
					String.valueOf(isStatic()),
					String.valueOf(isAbstract()),
					String.valueOf(isFinal()),
					interfaces.size(),
					methods.size(),
					fields.size()
				);
	}
	
}
