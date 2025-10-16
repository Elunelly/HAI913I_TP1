package com.visitors.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.visitors.base.BaseASTVisitor;
import com.visitors.base.VisitorResult;

public class ClassStructureVisitor extends BaseASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassStructureVisitor.class);
	
	private List<ClassInfo> classes = new ArrayList<>();
	private ClassInfo currentClass = null;
	private String currentPackage = "";
	private static final boolean PackageExploreChildren = true;
	private static final boolean TypeExploreChildren = true;
	private static final boolean MethodExploreChildren = true;
	private static final boolean FieldExploreChildren = true;
	
	public ClassStructureVisitor() {
		super();
	}
	
	public void reset() {
		this.classes.clear();
		logger.debug("Cleared all classes");
		this.currentClass = null;
		this.currentPackage = "";
		logger.debug("Reset currentClass and currentPackage value");
		this.result = new VisitorResult(getVisitorName());
	}
	
	public List<ClassInfo> getClasses() {return Collections.unmodifiableList(classes);}
	
	public List<ClassInfo> copyClasses() {return new ArrayList<>(classes);}
	
	@Override
	public String getVisitorName() {
		return "ClassStructureVisitor";
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		this.currentPackage = node.getName().getFullyQualifiedName();
		logger.trace("Visited Package: "+currentPackage);
		return PackageExploreChildren;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		this.currentClass = new ClassInfo(node.getName().getIdentifier());
		currentClass.setPackageName(this.currentPackage);
		currentClass.setInterface(node.isInterface());
		if (node.getSuperclassType()!=null) 
			currentClass.setSuperClass(node.getSuperclassType().toString());
		for (Object interfaceType : node.superInterfaceTypes()) {
			currentClass.addInterface(interfaceType.toString());
		}
		currentClass.setDefinedModifiers(node.getModifiers());
		logger.trace("Visited Class: "+currentClass);
		
		this.classes.add(currentClass);
		return TypeExploreChildren;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if (this.currentClass==null) return MethodExploreChildren;
		
		MethodInfo method = new MethodInfo(node.getName().getIdentifier());
		method.setIsConstructor(node.isConstructor());
		for (Object param : node.parameters()) {
			if (param instanceof SingleVariableDeclaration) {
				method.addParameter(((SingleVariableDeclaration) param).getName().getIdentifier());
			}
		}
		method.setDefinedModifiers(node.getModifiers());
        if (!method.isConstructor() && node.getReturnType2()!=null) {
            method.setReturnType(node.getReturnType2().toString());
        }
		logger.trace("Visited Method: "+method);
        
        this.currentClass.addMethod(method);
		return MethodExploreChildren;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		if (this.currentClass==null) return MethodExploreChildren;
		
		String fieldType = node.getType().toString();
		for (Object fragmentObj : node.fragments()) {
			if (fragmentObj instanceof VariableDeclarationFragment) {
				FieldInfo field = new FieldInfo(((VariableDeclarationFragment) fragmentObj).getName().getIdentifier());
				field.setType(fieldType);

				field.setDefinedModifiers(node.getModifiers());
				logger.trace("Visited Field: "+field);
				
				this.currentClass.addField(field);
			}
		}
		
		return FieldExploreChildren;
	}
	
	@Override
	protected void afterVisit() {
	    this.result.addData("classes", new ArrayList<>(this.classes));
	    logger.debug("Extracted %d classes".formatted(classes.size()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ClassStructureVisitor that = (ClassStructureVisitor) obj;
		return 
			Objects.equals(this.result, that.result) &&
			Objects.equals(this.classes, that.classes)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(result, classes);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "classes=%d, "
				+ "result=%s}")
				.formatted(this.getVisitorName(), classes.size(), this.getResult());
	}

}