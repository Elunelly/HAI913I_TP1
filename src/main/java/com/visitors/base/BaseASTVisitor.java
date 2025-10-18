package com.visitors.base;

import java.util.Objects;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseASTVisitor extends ASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseASTVisitor.class);
	
	protected VisitorResult result;
	protected CompilationUnit currentUnit;
	
	public BaseASTVisitor() {
		this.result = new VisitorResult(getVisitorName());
	}
	
	public abstract String getVisitorName();
	
	public VisitorResult visitAndExtract(CompilationUnit compilationUnit, Object context) {
		this.currentUnit = compilationUnit;
		if (context!=null) preVisit(context);
		if (compilationUnit == null) {
			this.result.setError("Compilation Unit is null");
			return this.result;
		}
		try {
			logger.trace("Visiting and extracting: "+compilationUnit.getPackage());
			this.result = new VisitorResult(getVisitorName());
			compilationUnit.accept(this);
			afterVisit();
		} catch (Exception e) {
			this.result.setError("Error occured during visit: "+e.getMessage());
			e.printStackTrace();
		}
		return this.result;
	}
	
	public VisitorResult visitAndExtract(CompilationUnit compilationUnit) {
		return visitAndExtract(compilationUnit, null);
	}
	
	protected abstract void preVisit(Object context);
	
	protected abstract void afterVisit();
	
	public VisitorResult getResult() {return this.result;}
	
	protected CompilationUnit getCurrentUnit() {return currentUnit;}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		BaseASTVisitor that = (BaseASTVisitor) obj;
		return 
			Objects.equals(this.result, that.result)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(result);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "result=%s}")
				.formatted(getVisitorName(), result);
	}

}
