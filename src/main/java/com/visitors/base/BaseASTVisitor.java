package com.visitors.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;

public abstract class BaseASTVisitor extends ASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseASTVisitor.class);
	
	protected VisitorResult result;
	protected CompilationUnit currentUnit;
	protected PackageInfo currentPackage;
	
	public BaseASTVisitor() {
		this.result = new VisitorResult(getVisitorName());
		logger.trace("{} -> BaseASTVisitor()",getVisitorName());
	}
	
	public abstract String getVisitorName();
	
	public Collection<VisitorResult> visit(ProjectInfo project) {
		logger.trace("{} -> visit(ProjectInfo)",getVisitorName());
		Objects.requireNonNull(project, "ProjectInfo cannot be null");
		Collection<VisitorResult> results = new ArrayList<>();
		for (PackageInfo packageInfo : project.copyPackages()) {
			this.currentPackage = packageInfo;
			for (CompilationUnit unit : packageInfo.copyUnits()) {
				this.currentUnit = unit;
				VisitorResult result = visitAndExtract(unit);
				if (result!=null) {
					results.add(result);
				}
			}
		}
		return results;
	}
	
	public VisitorResult visitAndExtract(CompilationUnit compilationUnit, Object context) {
		logger.trace("{} -> visitAndExtract(CompilationUnit,Object)",getVisitorName());
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
		logger.trace("{} -> visitAndExtract(CompilationUnit)",getVisitorName());
		return visitAndExtract(compilationUnit, null);
	}
	
	protected abstract void preVisit(Object context);
	
	protected abstract void afterVisit();
	
	public VisitorResult getResult() {
		logger.trace("{} -> getResult()",getVisitorName());
		return this.result;
	}
	
	protected CompilationUnit getCurrentUnit() {
		logger.trace("{} -> getCurrentUnit()",getVisitorName());
		return currentUnit;
	}
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",getVisitorName());
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
		logger.trace("{} -> hashCode()",getVisitorName());
		return Objects.hash(result);
	}
	
	@Override
	public String toString() {
		logger.trace("{} -> toString()",getVisitorName());
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "result=%s}")
				.formatted(getVisitorName(), result);
	}

}
