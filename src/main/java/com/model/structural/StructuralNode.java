package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.model.utils.HasModifiers;
import com.model.utils.HasVisibility;
import com.model.utils.NodeInfo;
import com.model.utils.NodeModifiers;
import com.model.utils.NodeVisibility;

public abstract class StructuralNode<T extends ASTNode> extends NodeInfo implements HasModifiers, HasVisibility {

	protected final T node;
	protected NodeVisibility visibility = NodeVisibility.PACKAGE;
	protected final List<NodeModifiers> modifiers = new ArrayList<>();
	
	private int node_firstCharPos = -1;
	private int node_lastCharPos = -1;
	private int node_firstLineNum = -1;
	private int node_lastLineNum = -1;

	public StructuralNode(T node, String name) {
		super(name);
		this.node = Objects.requireNonNull(node, "Node cannot be null");
		extractNodeInfo();
	}
	
	private void extractNodeInfo() {
		CompilationUnit unit = (CompilationUnit) node.getRoot();
		this.node_firstCharPos = node.getStartPosition();
		this.node_lastCharPos = node.getLength() + node_firstCharPos -1;
		this.node_firstLineNum = unit.getLineNumber(node_firstCharPos);
		this.node_lastLineNum = unit.getLineNumber(node_lastCharPos);
	}
	
	public T getNode() {return node;}
	
	public boolean isPublic() {return visibility == NodeVisibility.PUBLIC;}
	
	public boolean isProtected() {return visibility == NodeVisibility.PROTECTED;}
	
	public boolean isDefault() {return visibility == NodeVisibility.PACKAGE;}
	
	public boolean isPrivate() {return visibility == NodeVisibility.PRIVATE;}
	
	public NodeVisibility getVisibility() {return visibility;}
	
	public void setVisibility(NodeVisibility visibility) {
		this.visibility = visibility;
	}
	
	public boolean isStatic() {return modifiers.contains(NodeModifiers.STATIC);}
	
	public boolean isAbstract() {return modifiers.contains(NodeModifiers.ABSTRACT);}
	
	public boolean isFinal() {return modifiers.contains(NodeModifiers.FINAL);}
	
	public List<NodeModifiers> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}
	
	public boolean setModifiers(int modifierFlag) {
		modifiers.clear();
		return modifiers.addAll(NodeModifiers.getAllFrom(modifierFlag));
	}
	
	public boolean hasModifiers() {return modifiers.isEmpty();}
	
	public boolean hasModifier(NodeModifiers modifier) {return modifiers.contains(modifier);}
	
	public String modifiersToString(String sep) {
		return !hasModifiers() ?
			getModifiers().stream().map(NodeModifiers::toString).collect(Collectors.joining(sep)) :
			""
		;
	}
	
	public int getNodeStartPosition() {return node_firstCharPos;}
	
	public int getNodeLastPosition() {return node_lastCharPos;}
	
	public int getNodeStartLine() {return node_firstLineNum;}
	
	public int getNodeLastLine() {return node_lastLineNum;}
	
	public int getNodeLength() {return node_lastCharPos - node_firstCharPos + 1;}
	
	public int getNodeLOC() {return node_lastLineNum - node_firstLineNum + 1;}
	
	public String getFullSignature() {
		return
			getVisibility().toString()+" "+
			(hasModifiers() ? modifiersToString()+" " : "")+
			getSignature()
		;
	}
	
	public abstract String getShortSignature();
	
	public abstract String getSignature();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		@SuppressWarnings("unchecked")
		StructuralNode<T> that = (StructuralNode<T>) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.node, that.node) &&
			Objects.equals(this.visibility, that.visibility) &&
			Objects.equals(this.modifiers, that.modifiers)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, node, visibility, modifiers);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "node=%s, "
				+ "visibility=%s, "
				+ "modifiers=%s}")
				.formatted(
					name,
					node.getClass().getSimpleName(),
					visibility,
					modifiers
				);
	}

}
