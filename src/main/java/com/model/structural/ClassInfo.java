package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.interfaces.HasFields;
import com.model.interfaces.HasMethods;

public class ClassInfo extends NodeInfo implements HasMethods, HasFields {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
	
	protected String packageName = "";
	protected boolean isInterface;
	protected String superClass;
	protected final List<String> interfaces = new ArrayList<>();
	protected final List<MethodInfo> methods = new ArrayList<>();
	protected final List<FieldInfo> fields = new ArrayList<>();
	
	public ClassInfo() {
		super();
	}
	
	public String getPackageName() {return this.packageName;}

	public void setPackageName(String packageName) {
		String old = this.packageName;
		this.packageName = packageName!=null ? packageName : "";
		logger.debug("Change value of 'packageName': %s -> %s".formatted(old,this.packageName));
	}

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
