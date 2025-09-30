package visitors.structural;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import model.structural.ClassInfo;
import model.structural.FieldInfo;
import model.structural.MethodInfo;
import visitors.base.BaseASTVisitor;

public class ClassStructureVisitor extends BaseASTVisitor {
	
	private List<ClassInfo> classes = new ArrayList<>();
	private ClassInfo currentClass = null;
	private String currentPackage = "";
	private static final boolean TypeExploreChildren = true;
	private static final boolean MethodExploreChildren = true;
	private static final boolean FieldExploreChildren = true;
	
	public ClassStructureVisitor() {
		super();
	}
	
	@Override
	public String getVisitorName() {
		return "ClassStructureVisitor";
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		this.currentPackage = node.getName().getFullyQualifiedName();
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		this.currentClass = new ClassInfo();
		currentClass.setName(node.getName().getIdentifier());
		currentClass.setPackageName(this.currentPackage);
		currentClass.setInterface(node.isInterface());
		if (node.getSuperclassType()!=null) 
			currentClass.setSuperClass(node.getSuperclassType().toString());
		for (Object interfaceType : node.superInterfaceTypes()) {
			currentClass.addInterface(interfaceType.toString());
		}
		currentClass.setDefinedModifiers(node.getModifiers());
		
		this.classes.add(currentClass);
		return TypeExploreChildren;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if (this.currentClass==null) return MethodExploreChildren;
		
		MethodInfo method = new MethodInfo();
		method.setName(node.getName().getIdentifier());
		method.setConstructor(node.isConstructor());
		for (Object param : node.parameters()) {
			if (param instanceof SingleVariableDeclaration) {
				method.addParameter(((SingleVariableDeclaration) param).getName().getIdentifier());
			}
		}
		method.setDefinedModifiers(node.getModifiers());
        if (!method.isConstructor() && node.getReturnType2()!=null) {
            method.setReturnType(node.getReturnType2().toString());
        }
        
        this.currentClass.addMethod(method);
		return MethodExploreChildren;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		if (this.currentClass==null) return MethodExploreChildren;
		
		String fieldType = node.getType().toString();
		for (Object fragmentObj : node.fragments()) {
			if (fragmentObj instanceof VariableDeclarationFragment) {
				FieldInfo field = new FieldInfo();
				field.setName(((VariableDeclarationFragment) fragmentObj).getName().getIdentifier());
				field.setType(fieldType);

				field.setDefinedModifiers(node.getModifiers());
				
				this.currentClass.addField(field);
			}
		}
		
		return FieldExploreChildren;
	}

}