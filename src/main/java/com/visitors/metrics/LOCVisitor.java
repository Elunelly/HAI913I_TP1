package com.visitors.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.metrics.ClassMetrics;
import com.model.metrics.MethodMetrics;
import com.model.metrics.NodeMetrics;
import com.model.metrics.PackageMetrics;
import com.model.project.PackageInfo;
import com.model.structural.ClassInfo;
import com.visitors.LOCMetrics;
import com.visitors.base.BaseASTVisitor;
import com.visitors.base.VisitorResult;

public class LOCVisitor extends BaseASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(LOCVisitor.class);
	
	private List<ClassMetrics> classes = new ArrayList<>();
	private List<MethodMetrics> methods = new ArrayList<>();
	private ClassInfo currentClass;
	private static final boolean PackageExploreChildren = true;
	private static final boolean TypeExploreChildren = true;
	private static final boolean MethodExploreChildren = true;
	
	public LOCVisitor() {
		super();
	}
	
	public void reset() {
		this.currentClass = null;
		classes.clear();
		methods.clear();
		this.result = new VisitorResult(getVisitorName());
	}

	@Override
	public String getVisitorName() {
		return "LinesOfCodeVisitor";
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().getFullyQualifiedName();
		if (currentPackage==null || !packageName.equals(currentPackage.getName())) {
			logger.error("PackageInfo '{}' does not have the correct name '{}'",currentPackage.getName(),packageName);
			return false;
		}
		logger.trace("Visited Package: "+packageName);
		return PackageExploreChildren;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		String className = node.getName().getIdentifier();
		this.currentClass = currentPackage.getClass(className);
		ClassMetrics metric;
		if (this.currentClass==null) {
			metric = new ClassMetrics(className);
		} else {
			metric = currentClass.getMetrics();
		}
		extractNodeLOC(node,metric);
		classes.add(metric);
		return TypeExploreChildren;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		String methodName = node.getName().getIdentifier();
		MethodMetrics metric;
		if (this.currentClass==null || !currentClass.hasMethod(methodName)) {
			metric = new MethodMetrics(methodName);
		} else {
			metric = currentClass.getMethod(methodName).getMetrics();
		}
		extractNodeLOC(node,metric);
		methods.add(metric);
		return MethodExploreChildren;
	}
	
	@Override
	protected void preVisit(Object context) {
		if (context==null) return;
		else if (context.getClass() == PackageInfo.class) {
			this.currentPackage = (PackageInfo) context;
			logger.debug("Visiting in package '{}'...",currentPackage.getName());
		} else return;
	}
	
	@Override
	protected void afterVisit() {
		if (currentPackage!=null) {
			logger.debug("End of visit in package '{}':",currentPackage.getName());
			PackageMetrics metric = currentPackage.getMetrics();
			metric.addData(
					LOCMetrics.CHAR_LENGTH,
					classes.stream()
					.mapToInt(c -> c.getDataBy(LOCMetrics.CHAR_LENGTH, Integer.class))
					.sum()
			);
			metric.addData(
					LOCMetrics.LINE_LENGTH,
					classes.stream()
					.mapToInt(c -> c.getDataBy(LOCMetrics.LINE_LENGTH, Integer.class))
					.sum()
			);
		}
	    result.addData("classes", classes);
	    logger.debug("Calculated LOC in %d classes".formatted(classes.size()));
	    result.addData("methods", methods);
	    logger.debug("Calculated LOC in %d methods".formatted(methods.size()));
	}
	
	private void extractNodeLOC(ASTNode node, NodeMetrics<?> metric) {
		CompilationUnit unit = (CompilationUnit) node.getRoot();
		int firstCharPos = node.getStartPosition();
		int lastCharPos = node.getLength() + firstCharPos -1;
		int firstLineNum = unit.getLineNumber(firstCharPos);
		int lastLineNum = unit.getLineNumber(lastCharPos);
		metric.addData(LOCMetrics.FIRST_CHAR_POS, firstCharPos);
		metric.addData(LOCMetrics.LAST_CHAR_POS, lastCharPos);
		metric.addData(LOCMetrics.CHAR_LENGTH, node.getLength());
		metric.addData(LOCMetrics.FIRST_LINE_NUM, firstLineNum);
		metric.addData(LOCMetrics.LAST_LINE_NUM, lastLineNum);
		metric.addData(LOCMetrics.LINE_LENGTH, Math.max(0,lastLineNum-firstLineNum+1));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		LOCVisitor that = (LOCVisitor) obj;
		return 
			Objects.equals(this.getVisitorName(), that.getVisitorName()) &&
			Objects.equals(this.result, that.result)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getVisitorName(), result);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "classes=%d, "
				+ "methods=%d, "
				+ "result=%s}")
				.formatted(
					getVisitorName(),
					classes.size(),
					methods.size(),
					getResult());
	}

}
