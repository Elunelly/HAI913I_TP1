package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.NodeVisibility;
import com.model.metrics.ClassMetrics;
import com.model.project.PackageInfo;

public class ClassInfo extends StructuralNode<TypeDeclaration> {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
	
	private final PackageInfo parentPackage;
	private final String superClass;
	private final List<String> interfaces = new ArrayList<>();
	private final List<MethodInfo> methods = new ArrayList<>();
	private final List<FieldInfo> fields = new ArrayList<>();
	
	private final ClassMetrics metrics;
	
	public ClassInfo(TypeDeclaration node, PackageInfo parentPackage) {
		super(Objects.requireNonNull(node),node.getName().getIdentifier());
		logger.trace("{} -> ClassInfo(TypeDeclaration,PackageInfo)",name);
		this.parentPackage = Objects.requireNonNull(parentPackage, "Package cannot be null for '"+name+"' class");
		this.superClass = node.getSuperclassType()!=null ?
				node.getSuperclassType().toString() :
				null;
		this.metrics = new ClassMetrics(getQualifiedName());
	}

	@Override
	public String getQualifiedName() {
		logger.trace("{} -> getQualifiedName()",name);
		if (getPackageName().isBlank()) return getName();
		else return "%s.%s".formatted(getPackageName(),getName());
	}
	
	// PARENT PACKAGE
	
	public PackageInfo getParentPackage() {
		logger.trace("{} -> getParentPackage()",name);
		return this.parentPackage;
	}
	
	public String getPackageName() {
		logger.trace("{} -> getPackageName()",name);
		return this.parentPackage.getName();
	}
	
	// SUPER CLASS
	
	public String getSuperClass() {
		logger.trace("{} -> getSuperClass()",name);
		return superClass;
	}
	
	public boolean hasInheritance() {
		logger.trace("{} -> hasInheritance()",name);
		return superClass!=null && !this.superClass.isBlank();
	}
	
	public boolean isComplex() {
		logger.trace("{} -> isComplex()",name);
		return hasInheritance() || hasImplementations();
	}
	
	// INTERFACES
	
	public boolean isInterface() {
		logger.trace("{} -> isInterface()",name);
		return node.isInterface();
	}
	
	public List<String> getInterfaces() {
		logger.trace("{} -> getInterfaces()",name);
		return Collections.unmodifiableList(interfaces);
	}
	
	public boolean addInterface(String interfaceName) {
		logger.trace("{} -> addInterface(String)",name);
		if (interfaceName!=null && !interfaces.contains(interfaceName) && interfaces.add(interfaceName)) {
			logger.debug("Interface added: %s".formatted(interfaceName));
			return true;
		}
		return false;
	}
	
	public boolean removeInterface(String interfaceName) {
		logger.trace("{} -> removeInterface(String)",name);
		if (interfaceName!=null && interfaces.remove(interfaceName)) {
			logger.debug("Interface removed: %s".formatted(interfaceName));
			return true;
		}
		return false;
	}
	
	public boolean hasImplementations() {
		logger.trace("{} -> hasImplementation()",name);
		return !interfaces.isEmpty();
	}
	
	// METHODS
	
	public List<MethodInfo> getMethods() {
		logger.trace("{} -> getMethods()",name);
		return Collections.unmodifiableList(methods);
	}
	
	public List<MethodInfo> copyMethods() {
		logger.trace("{} -> copyMethods()",name);
		return new ArrayList<>(methods);
	}
	
	public boolean addMethod(MethodInfo methodInfo) {
		logger.trace("{} -> addMethod(MethodInfo)",name);
		if (methodInfo!=null && !methods.contains(methodInfo) && methods.add(methodInfo)) {
			logger.debug("Method added: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllMethods(List<MethodInfo> methodInfos) {
		logger.trace("{} -> addAllMethods(List<MethodInfo>)",name);
		return methodInfos.stream().filter(c -> addMethod(c)).count() > 0;
	}
	
	public boolean removeMethod(MethodInfo methodInfo) {
		logger.trace("{} -> removeMethod(MethodInfo)",name);
		if (methodInfo!=null && methods.remove(methodInfo)) {
			logger.debug("Method removed: %s".formatted(methodInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllMethods(List<MethodInfo> methodInfos) {
		logger.trace("{} -> removeAllMethods(List<MethodInfo>)",name);
		return methodInfos.stream().filter(c -> removeMethod(c)).count() > 0;
	}
	
	public MethodInfo getMethod(String name) {
		logger.trace("{} -> getMethod(String)",name);
		for(MethodInfo methodInfo : methods) {
			if (name.equalsIgnoreCase(methodInfo.getName())) return methodInfo;
		}
		return null;
	}
	
	public boolean hasMethod(String name) {
		logger.trace("{} -> hasMethod(String)",name);
		return getMethod(name) != null;
	}
	
	public boolean hasMethods() {
		logger.trace("{} -> hasMethods()",name);
		return !methods.isEmpty();
	}
	
	public void clearMethods() {
		logger.trace("{} -> clearMethods()",name);
		methods.clear();
		logger.debug("All Methods removed from: "+getName());
	}
	
	public List<MethodInfo> getMethodsByVisibility(NodeVisibility visibility) {
		logger.trace("{} -> getMethodsByVisibility(NodeVisibility)",name);
		return copyMethods().stream().filter(m -> m.getVisibility()==visibility).toList();
	}
	
	// FIELDS
	
	public List<FieldInfo> getFields() {
		logger.trace("{} -> getFields()",name);
		return Collections.unmodifiableList(fields);
	}
	
	public List<FieldInfo> copyFields() {
		logger.trace("{} -> copyFields()",name);
		return new ArrayList<>(fields);
	}
	
	public boolean addField(FieldInfo fieldInfo) {
		logger.trace("{} -> addField(FieldInfo)",name);
		if (fieldInfo!=null && !fields.contains(fieldInfo) && fields.add(fieldInfo)) {
			logger.debug("Field added: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	public boolean addAllFields(List<FieldInfo> fieldInfos) {
		logger.trace("{} -> addAllFields(List<FieldInfo>)",name);
		return fieldInfos.stream().filter(c -> addField(c)).count() > 0;
	}
	
	public boolean removeField(FieldInfo fieldInfo) {
		logger.trace("{} -> removeField(FieldInfo)",name);
		if (fieldInfo!=null && fields.remove(fieldInfo)) {
			logger.debug("Field removed: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	public boolean removeAllFields(List<FieldInfo> fieldInfos) {
		logger.trace("{} -> removeAllFields(List<FieldInfo>)",name);
		return fieldInfos.stream().filter(c -> removeField(c)).count() > 0;
	}
	
	public FieldInfo getField(String name) {
		logger.trace("{} -> getField(String)",name);
		for(FieldInfo fieldInfo : fields) {
			if (name.equalsIgnoreCase(fieldInfo.getName())) return fieldInfo;
		}
		return null;
	}
	
	public boolean hasField(String name) {
		logger.trace("{} -> hasField(String)",name);
		return getField(name) != null;
	}
	
	public boolean hasFields() {
		logger.trace("{} -> hasFields()",name);
		return !fields.isEmpty();
	}
	
	public void clearFields() {
		logger.trace("{} -> clearFields()",name);
		fields.clear();
		logger.debug("All Fields removed from: "+getName());
	}
	
	public List<FieldInfo> getFieldsByVisibility(NodeVisibility visibility) {
		logger.trace("{} -> getFieldsByVisibility(NodeVisibility)",name);
		return copyFields().stream().filter(f -> f.getVisibility()==visibility).toList();
	}
	
	// METRICS
	
	public ClassMetrics getMetrics() {
		logger.trace("{} -> getMetrics()",name);
		return metrics;
	}
	
	// UTILITIES
	
	@Override
	public String getSignature() {
		logger.trace("{} -> getSignature()",name);
		return 
			(isInterface() ? "interface" : "class")+" "+
			getName()+
			(hasInheritance() ? " extends "+getSuperClass() : "")+
			(hasImplementations() ? " implements "+String.join(", "+getInterfaces()) : "")
		;
	}
	
	@Override
	public String getShortSignature() {
		logger.trace("{} -> getShortSignature()",name);
		return getName();
	}
	
	public String getOverviewSignature() {
		logger.trace("{} -> getOverviewSignature()",name);
		int fn = getFields().size();
		int mn = getMethods().size();
		return "%s { %d field%s, %d method%s }"
				.formatted(getFullSignature(),fn,fn==1?"":"s",mn,mn==1?"":"s")
		;
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
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
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(getSignature(), getPackageName(), methods.size(), fields.size());
	}

	@Override
	public String toString() {
		logger.trace("{} -> toString()",name);
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
