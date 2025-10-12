package com.visitors.base;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseASTVisitor extends ASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseASTVisitor.class);
	
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
	
	@Override
	public String toString() {
		return ("BaseASTVisitor{"
				+ "name=%s, "
				+ "result=%s}")
				.formatted(getVisitorName(), result);
	}

}
