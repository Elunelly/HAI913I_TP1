package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;

import com.model.HasModifiers;
import com.model.HasVisibility;
import com.model.NodeInfo;
import com.model.NodeModifiers;
import com.model.NodeVisibility;

public abstract class StructuralNode<T extends ASTNode> extends NodeInfo implements HasModifiers, HasVisibility {

	protected final T node;
	protected NodeVisibility visibility = NodeVisibility.PACKAGE;
	protected final List<NodeModifiers> modifiers = new ArrayList<>();

	public StructuralNode(T node, String name) {
		super(name);
		this.node = Objects.requireNonNull(node, "Node cannot be null");
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
		setVisibility(NodeVisibility.getFrom(modifierFlag));
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
