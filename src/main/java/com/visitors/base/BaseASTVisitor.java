package com.visitors.base;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class BaseASTVisitor extends ASTVisitor {
	
	protected VisitorResult result;
	
	public BaseASTVisitor() {
		this.result = new VisitorResult(getVisitorName());
	}
	
	public abstract String getVisitorName();
	
	public VisitorResult visitAndExtract(CompilationUnit compilationUnit) {
		if (compilationUnit == null) {
			this.result.setError("Compilation Unit is null");
			return this.result;
		}
		try {
			this.result = new VisitorResult(getVisitorName());
			compilationUnit.accept(this);
			afterVisit();
		} catch (Exception e) {
			this.result.setError("Error occured during visit: "+e.getMessage());
		}
		return this.result;
	}
	
	public boolean visit(CompilationUnit compilationUnit) {
		return visitAndExtract(compilationUnit).isSuccessful();
	}
	
	protected void afterVisit() {}
	
	public VisitorResult getResult() {return this.result;}

}
