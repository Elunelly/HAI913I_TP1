package com.visitors.structural;

import java.util.ArrayList;
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

import com.model.project.PackageInfo;
import com.model.structural.ClassInfo;
import com.model.structural.FieldInfo;
import com.model.structural.MethodInfo;
import com.visitors.base.BaseASTVisitor;
import com.visitors.base.VisitorResult;

public class ClassStructureVisitor extends BaseASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassStructureVisitor.class);
	
	private final List<ClassInfo> classes = new ArrayList<>();
	private final List<MethodInfo> methods = new ArrayList<>();
	private final List<FieldInfo> fields = new ArrayList<>();
	private ClassInfo currentClass;
	private static final boolean PackageExploreChildren = true;
	private static final boolean TypeExploreChildren = true;
	private static final boolean MethodExploreChildren = true;
	private static final boolean FieldExploreChildren = true;
	
	public ClassStructureVisitor() {
		super();
		logger.trace("{} -> ClassStructureVisitor()",getVisitorName());
	}
	
	public void reset() {
		logger.trace("{} -> reset()",getVisitorName());
		this.classes.clear();
		this.currentPackage = null;
		this.currentClass = null;
		this.currentUnit = null;
		this.result = new VisitorResult(getVisitorName());
	}
	
	@Override
	public String getVisitorName() {
		return "ClassStructureVisitor";
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		logger.trace("{} -> visit(PackageDeclaration)",getVisitorName());
		String packageName = node.getName().getFullyQualifiedName();
		if (!packageName.equals(currentPackage.getName())) {
			logger.error("PackageInfo '{}' does not have the correct name '{}'",currentPackage.getName(),packageName);
		}
		logger.trace("Visited Package: "+packageName);
		return PackageExploreChildren;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		logger.trace("{} -> visit(TypeDeclaration)",getVisitorName());
		this.currentClass = new ClassInfo(node, currentPackage);
		
		for (Object interfaceType : node.superInterfaceTypes()) {
			currentClass.addInterface(interfaceType.toString());
		}
		currentClass.setModifiers(node.getModifiers());
		logger.trace("Visited Class: "+currentClass);
		
		this.currentPackage.addClass(currentClass);
		this.classes.add(currentClass);
		return TypeExploreChildren;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		logger.trace("{} -> visit(MethodDeclaration)",getVisitorName());
		if (this.currentClass==null) return MethodExploreChildren;
		
		MethodInfo method = new MethodInfo(node, currentClass);
		for (Object param : node.parameters()) {
			if (param instanceof SingleVariableDeclaration) {
				method.addParameter(((SingleVariableDeclaration) param).getName().getIdentifier());
			}
		}
		method.setModifiers(node.getModifiers());
		logger.trace("Visited Method: "+method);
        
		this.methods.add(method);
        this.currentClass.addMethod(method);
		return MethodExploreChildren;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		logger.trace("{} -> visit(FieldDeclaration)",getVisitorName());
		if (this.currentClass==null) return FieldExploreChildren;
		
		String fieldType = node.getType().toString();
		for (Object fragmentObj : node.fragments()) {
			if (fragmentObj instanceof VariableDeclarationFragment) {
				FieldInfo field = new FieldInfo((VariableDeclarationFragment) fragmentObj, currentClass, fieldType);
				field.setModifiers(node.getModifiers());
				logger.trace("Visited Field: "+field);
				
				this.fields.add(field);
				this.currentClass.addField(field);
			}
		}
		
		return FieldExploreChildren;
	}
	
	@Override
	protected void preVisit(Object context) {
		logger.trace("{} -> preVisit(Object)",getVisitorName());
		if (context==null) return;
		else if (context.getClass() == PackageInfo.class) {
			this.currentPackage = (PackageInfo) context;
			logger.debug("Visiting in package '{}'...",currentPackage.getName());
		} else return;
	}
	
	@Override
	protected void afterVisit() {
		logger.trace("{} -> afterVisit()",getVisitorName());
		if (currentPackage!=null)
			logger.debug("End of visit in package '{}':",currentPackage.getName());
	    result.addData("classes", classes);
	    logger.debug("Extracted %d classes".formatted(classes.size()));
	    result.addData("methods", methods);
	    logger.debug("Extracted %d methods".formatted(methods.size()));
	    result.addData("fields", fields);
	    logger.debug("Extracted %d fields".formatted(fields.size()));
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",getVisitorName());
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ClassStructureVisitor that = (ClassStructureVisitor) obj;
		return 
			Objects.equals(this.getVisitorName(), that.getVisitorName()) &&
			Objects.equals(this.result, that.result)
		;
	}
	
	@Override
	public int hashCode() {
		logger.trace("{} -> hashCode()",getVisitorName());
		return Objects.hash(getVisitorName(), result);
	}
	
	@Override
	public String toString() {
		logger.trace("{} -> toString()",getVisitorName());
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "classes=%d, "
				+ "methods=%d, "
				+ "fields=%d, "
				+ "result=%s}")
				.formatted(
					getVisitorName(),
					classes.size(),
					methods.size(),
					fields.size(),
					getResult());
	}

}